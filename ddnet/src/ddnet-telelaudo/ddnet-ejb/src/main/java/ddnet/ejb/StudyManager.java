package ddnet.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dcm4che3.io.DicomEncodingOptions;
import org.dcm4che3.media.RecordFactory;
import org.dcm4che3.tool.dcm2dcm.Dcm2Dcm;
import org.dcm4che3.tool.dcmdir.DcmDir;

import ddnet.ejb.dao.StudyDAO;
import ddnet.ejb.dao.UserStudyDAO;
import ddnet.ejb.dao.LegacyPatientDAO;
import ddnet.ejb.entities.Configuration;
import ddnet.ejb.entities.LegacyFileSystem;
import ddnet.ejb.entities.LegacyInstance;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacySerie;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.StudyMedia;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserStudy;

/**
 * Session Bean implementation class StudyManager
 */
@Stateless(mappedName = "ejb/ddnet/StudyManager")
@LocalBean
public class StudyManager implements StudyManagerLocal {

	@EJB
	private StudyDAO studyDAO;

	@EJB
	private UserStudyDAO userStudyDAO;
	
	@EJB
	private LegacyPatientDAO LegacyPatientDAO;

	@EJB
	private ConfigurationManager configurationManager;
	
	public Collection<Study> findStudies(User user, StudySearchFilter filter) {
		return studyDAO.findStudies(user, filter);
	}	
	
	public Study getStudy(User user, String studyID) {
		return studyDAO.getStudy(user, studyID);
	}	

	public StudyMedia getStudyMedia(User user, String studyID, int quality, List<String> seriesIDs, List<String> options) {		
		final Study study = studyDAO.getStudy(user, studyID);
		final String serverStorageFolder = configurationManager.getConfigurationItem("dcm4chee.server.default.path").getValue();
		final String dcm4cheUtilityFolder = configurationManager.getConfigurationItem("dcm4che.utility.path").getValue();
		
		String transferSyntaxUID = "1.2.840.10008.1.2.4.51";
		final Configuration transferSyntaxUIDConfig = configurationManager.getConfigurationItem("study.media.lossy-transfer-syntax");
		if (transferSyntaxUIDConfig != null && transferSyntaxUIDConfig.getValue() != null && !"".equals(transferSyntaxUIDConfig.getValue().trim()))
			transferSyntaxUID = transferSyntaxUIDConfig.getValue();

		final boolean nonExecutable = options != null && options.contains("nonexec");
		int fileIndex = 0;
		File tempDirectory = null;
		List<File> files = new ArrayList<File>();
		try {
			// Crear un directorio temporal para contener todos los archivos 
			// del estudio solicitado. Luego se arma un .zip con todo.
			tempDirectory = createTempDirectory();
			
			final File imagesTempDirectory = FileUtils.getFile(tempDirectory, "imagenes");
			imagesTempDirectory.mkdirs();
			
			for(LegacySerie serie : study.getLegacyStudy().getLegacySeries()) {
				if (seriesIDs == null || seriesIDs.contains(serie.getSerieID())) {
					for(LegacyInstance instance : serie.getLegacyInstances()) {
						File instanceFile = FileUtils.getFile(serverStorageFolder, 
								instance.getLegacyFile().getLegacyFileSystem().getDirpath(),
								instance.getLegacyFile().getFilepath());
						File tempFile = new File(imagesTempDirectory, String.format("%08d", ++fileIndex));
						FileUtils.copyFile(instanceFile, tempFile);
						files.add(tempFile);
					}				
				}
			}

			// Conversión de imágenes a calidad Lossy (si es indicado)
			if (quality == StudyMedia.LOSSY) {
				System.out.println("Transfer syntax para imágenes LOSSY: " + transferSyntaxUID);
				// NOTA: debe estar presente "clib_jiio.dll/libclib_jiio.so" (segun el OS) en alguno 
				// de los directorios especificados por "java.library.path".
				System.out.println("java.library.path: " + System.getProperty("java.library.path"));
				
				Dcm2Dcm dcm2dcm = new Dcm2Dcm();
				dcm2dcm.setEncodingOptions(new DicomEncodingOptions(false, true, false, true, false));
				dcm2dcm.setTransferSyntax(transferSyntaxUID);
				dcm2dcm.setRetainFileMetaInformation(false);
				dcm2dcm.addCompressionParam("compressionQuality", new Double(0.5d));
				for(File f : files) {
					File originalFile = new File(f.getAbsolutePath());
					File tempConversionFile = new File(imagesTempDirectory, "DCM2DCM_TMP_" + f.getName());
					f.renameTo(tempConversionFile);
					
					boolean converted = false;
					try {
						dcm2dcm.transcode(tempConversionFile, originalFile);
						converted = true;
					} catch (Throwable t) {
						t.printStackTrace();
					}
					
					if (converted)
						// Si la conversión funciono, eliminamos el archivo temporal.
						tempConversionFile.delete();
					else 
						// Si hubo problemas en la conversión, se decide enviar el archivo original, sin convertir.
						if (originalFile.exists())
							originalFile.delete();
						f.renameTo(originalFile);
				}
			}
			
			
			if (!nonExecutable) {
				// Una vez que contamos con todos los archivos, creamos el archivo DICOMDIR.
				final File dicomDirFile = new File(tempDirectory, "DICOMDIR");
				DcmDir dicomDirTool = new DcmDir();
				dicomDirTool.create(dicomDirFile);
				dicomDirTool.setRecordFactory(new RecordFactory());
				for(File f : files)
					dicomDirTool.addReferenceTo(f);
				dicomDirTool.commit();
				dicomDirTool.close();			

				// Creamos launchers que envían efectivamente las imágenes al visualizador configurado por el usuario.
				String viewerHostOrIP = user.getProp() != null ? user.getProp().getHostname() : "127.0.0.1";
				int viewerPort =  user.getProp() != null ? Integer.parseInt(user.getProp().getPort()) : 11112;
				String viewerAETitle =  user.getProp() != null ? user.getProp().getAet() : "DCM4CHEE";
				String callingAETitle =  user.getProp() != null ? user.getProp().getCallingAet() : "SERVIDOR";
				StringBuilder filenames = new StringBuilder();
				for(File f : files)
					filenames.append("./imagenes/").append(FilenameUtils.getName(f.getAbsolutePath())).append(" ");			
	
				final String windowsRunCommand = "call .\\dcm4che\\bin\\storescu.bat";
				final String nixRunCommand = "sh ./dcm4che/bin/storescu ";
				
				// Windows launcher
				String windowsCommandLine = getSendImagesCommandLine(windowsRunCommand, viewerHostOrIP, viewerPort,
						viewerAETitle, callingAETitle, filenames);
				final File windowsLauncherFile = FileUtils.getFile(tempDirectory, "ENVIAR_IMAGENES.bat");
				FileUtils.write(windowsLauncherFile, windowsCommandLine);
	
				// Unix/Mac launcher
				String nixCommandLine = getSendImagesCommandLine(nixRunCommand, viewerHostOrIP, viewerPort,
						viewerAETitle, callingAETitle, filenames);
				final File nixLauncherFile = FileUtils.getFile(tempDirectory, "ENVIAR_IMAGENES.sh");
				FileUtils.write(nixLauncherFile, nixCommandLine);															
				
				// Finalmente, generamos datos en formato .zip, conteniendo imagenes, 
				// el archivo DICOMDIR y la utilidad de envío.
				final File tempUtilBin = FileUtils.getFile(tempDirectory, "dcm4che", "bin");
				final File tempUtilLib = FileUtils.getFile(tempDirectory, "dcm4che", "lib");
				final File tempUtilEtc = FileUtils.getFile(tempDirectory, "dcm4che", "etc", "storescu");
				tempUtilBin.mkdirs();
				tempUtilLib.mkdirs();
				tempUtilEtc.mkdirs();
	
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "bin", "storescu.bat"), tempUtilBin);			
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "bin", "storescu"), tempUtilBin);
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "dcm4che-tool-storescu-3.3.6.jar"), tempUtilLib);
				
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "dcm4che-core-3.3.6.jar"), tempUtilLib);
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "dcm4che-net-3.3.6.jar"), tempUtilLib);
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "dcm4che-tool-common-3.3.6.jar"), tempUtilLib);
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "slf4j-log4j12-1.7.5.jar"), tempUtilLib);
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "slf4j-api-1.7.5.jar"), tempUtilLib);
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "log4j-1.2.17.jar"), tempUtilLib);
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "lib", "commons-cli-1.2.jar"), tempUtilLib);
				
				FileUtils.copyFileToDirectory(FileUtils.getFile(dcm4cheUtilityFolder, "etc", "storescu", "log4j.properties"), tempUtilEtc);
			}
			
			StudyMedia studyMedia = new StudyMedia();
			studyMedia.setStudy(study);
			studyMedia.setContents(zipFilesRecursive(tempDirectory));
			
			return studyMedia;
			
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		} finally {
			if (tempDirectory != null) {
				try { FileUtils.deleteDirectory(tempDirectory); } catch (Throwable t) { }
			}
		}		
	}

	private String getSendImagesCommandLine(final String runCommand,
			String viewerHostOrIP, int viewerPort, String viewerAETitle,
			String callingAETitle, StringBuilder filenames) {
		String windowsCommandLine = String.format("%s -c %s%s:%d %s -b %s",
				runCommand, ((viewerAETitle != null && viewerAETitle.trim().length() > 0) ? viewerAETitle + "@" : ""), 
				viewerHostOrIP, viewerPort, filenames, callingAETitle);
		return windowsCommandLine;
	}

	@Override
	public int assignStudies(User requestingUser, User physician, String[] studiesIDs) {
		List<UserStudy> userStudies = new ArrayList<UserStudy>();
		
		Collection<UserStudy> alreadyAssigned = userStudyDAO.getUserStudies(physician.getLogin());
		for(String studyID : studiesIDs)
		{
			try {
				boolean add = true;
				for(UserStudy us : alreadyAssigned){
					if (us.getStudy().getLegacyStudy().getStudyID().equals(studyID) && 
							us.getUser().getId() == physician.getId()) {
						add = false;
						break;
					}
				}
				
				if (!add)
					continue;
				
				Study study = studyDAO.getStudy(requestingUser, studyID);
				UserStudy userStudy = new UserStudy(physician, study);
				userStudies.add(userStudy);
			} catch (Throwable t) {
				// TODO: logging!
				t.printStackTrace();
			}
		}

		for(UserStudy us : userStudies)
			try {
				userStudyDAO.persist(us);		
			} catch (Throwable t) {
				System.out.println("Error asignando:");
				t.printStackTrace();
			}

		return userStudies.size();
	}	
	
	private static File createTempDirectory() throws IOException
	{
		final File temp;
		temp = File.createTempFile("ddtmp", Long.toString(System.nanoTime()));

		if(!(temp.delete()))
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());

		if(!(temp.mkdir()))
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());

		return (temp);
	}	
	
	private byte[] zipFilesRecursive(File rootDirectory) throws IOException { 
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		File tempFile = null;
		
		try
		{
			tempFile = File.createTempFile("ddz", ".zip");
			fos = new FileOutputStream(tempFile);
			zos = new ZipOutputStream(fos);

			addDirToZip(rootDirectory, rootDirectory, zos);
			
			zos.finish();
			zos.close();
			
			byte[] fileContents = FileUtils.readFileToByteArray(tempFile);
			return fileContents;
		}
		finally
		{
			try { zos.close(); } catch (IOException e) { }
			if (tempFile != null)
				try { tempFile.delete(); } catch (Throwable t) { }
		}
	}
	
	private void addDirToZip(File topRoot, File dirObj, ZipOutputStream out) throws IOException {
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDirToZip(topRoot, files[i], out);
				continue;
			}
			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			Path pathRelative = topRoot.toPath().relativize(files[i].toPath());
			// El separador de partes del path debe ser siempre "/", independientemente del sistema operativo.
			out.putNextEntry(new ZipEntry(pathRelative.toString().replace("\\", "/")));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
	}
	
	@Override
	public Study getIndividualStudy(String studyID) {
		// TODO Auto-generated method stub
		return studyDAO.getIndividualStudy(studyID); 
	}
	
	@Override
	public LegacyFileSystem getStudyonfs(Long studyID) {
		// TODO Auto-generated method stub
		return studyDAO.getStudyonfs(studyID);
	}
	
	@Override
	public String getFileOfStudy(Long studyID) {
		// TODO Auto-generated method stub
		return studyDAO.getFileOfStudy(studyID); 
	}
	
	@Override
	public void removeAllStudyData(LegacyPatient pat) {
		
		LegacyPatientDAO.remove(pat);
	}
	
	@Override
	public Collection<LegacyStudy> getByPatId(Long studyID,Long patID) {
		
		return LegacyPatientDAO.getByPatId(studyID, patID);
	}
	
	@Override
	public Collection<LegacyStudy> getByPatId(Long studyID,String patID) {
		
		return LegacyPatientDAO.getByPatId(studyID, patID);
	}
}
