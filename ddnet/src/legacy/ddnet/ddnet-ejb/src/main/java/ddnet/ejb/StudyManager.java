package ddnet.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;
import org.dcm4che2.tool.dcmdir.DcmDir;

import ddnet.ejb.dao.StudyDAO;
import ddnet.ejb.entities.Configuration;
import ddnet.ejb.entities.LegacyInstance;
import ddnet.ejb.entities.LegacySerie;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.User;

/**
 * Session Bean implementation class StudyManager
 */
@Stateless(mappedName = "ejb/ddnet/StudyManager")
@LocalBean
public class StudyManager implements StudyManagerLocal {

	@EJB
	private StudyDAO studyDAO;

	@EJB
	private ConfigurationManager configurationManager;
	
	public Collection<Study> findStudies(User user, StudySearchFilter filter) {
		return studyDAO.findStudies(user, filter);
	}	
	
	public Study getStudy(User user, String studyID) {
		return studyDAO.getStudy(user, studyID);
	}	
	
	public byte[] getStudyMedia(User user, String studyID) {		
		Study study = studyDAO.getStudy(user, studyID);
		Configuration config = configurationManager.getConfigurationItem("dcm4chee.server.default.path");		

		int fileIndex = 0;
		File tempDirectory = null;
		List<File> files = new ArrayList<File>();
		try {
			// Crear un directorio temporal para contener todos los archivos DICOM del estudio solicitado.
			tempDirectory = createTempDirectory();
			for(LegacySerie serie : study.getLegacyStudy().getLegacySeries()) {
				for(LegacyInstance instance : serie.getLegacyInstances()) {
					
					File instanceFile = null;
					
					if(instance.getLegacyFile().getLegacyFileSystem().getDirpath().equals("archive")){
					
						instanceFile = FileUtils.getFile(config.getValue(), 
											instance.getLegacyFile().getLegacyFileSystem().getDirpath(),
											instance.getLegacyFile().getFilepath());
					}else{
						
						instanceFile = FileUtils.getFile( 
								instance.getLegacyFile().getLegacyFileSystem().getDirpath(),
								instance.getLegacyFile().getFilepath());
					}
					
					File tempFile = new File(tempDirectory, String.format("%08d", ++fileIndex));
					FileUtils.copyFile(instanceFile, tempFile);
					files.add(tempFile);
				}				
			}
			
			// Una vez que contamos con todos los archivos, creamos el archivo DICOMDIR
			final File dicomDirFile = new File(tempDirectory, "DICOMDIR");
			DcmDir dicomDirTool = new DcmDir(dicomDirFile);
			dicomDirTool.create();
			for(File f : files)
				dicomDirTool.addFile(f);			
			dicomDirTool.close();
			
			// Finalmente, generamos datos en formato .zip,
			// conteniendo imagenes y el archivo DICOMDIR.
			files.add(dicomDirFile);
			return zipFiles(files);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		} finally {
			if (tempDirectory != null) {
				try { FileUtils.deleteDirectory(tempDirectory); } catch (Throwable t) { }
			}
		}		
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
	
	private byte[] zipFiles(List<File> files) throws IOException
	{
		byte[] buffer = new byte[8192];	   
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		File tempFile = null;
		
		try
		{
			tempFile = File.createTempFile("ddz", ".zip");
			fos = new FileOutputStream(tempFile);
			zos = new ZipOutputStream(fos);
			FileInputStream in = null;
			for (File file : files)
			{
				ZipEntry ze = new ZipEntry(file.getName());
				zos.putNextEntry(ze);
				try
				{
					in = new FileInputStream(file);
					int len;
					while ((len = in.read(buffer)) > 0)
						zos.write(buffer, 0, len);
				}
				finally
				{
					in.close();
				}
			}

			zos.closeEntry();
			zos.flush();
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
}
