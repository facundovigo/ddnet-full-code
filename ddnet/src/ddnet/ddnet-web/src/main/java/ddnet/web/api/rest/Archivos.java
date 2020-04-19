package ddnet.web.api.rest;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.StudyLogManager;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.UserManager;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("/study/files")
public class Archivos {

	@EJB private SecurityHelper securityHelper;
	@EJB private StudyManagerLocal studyManager;
	@EJB private UserManager userManager;
	@EJB private ConfigurationManager configurationManager;
	@EJB private StudyLogManager loggerManager;
	
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat DATE_FOLDER_FORMAT= new SimpleDateFormat("yyyyMMdd");
	
	
	@GET @Path("{study-id}") @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	
	public Collection<AttachmentDTO> getFilesFromStudy
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	){

		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studyId);
		if(study == null) return Collections.<AttachmentDTO>emptyList();

		Collection<AttachmentDTO> files= new ArrayList<AttachmentDTO>();
		Collection<String> listaArchivos= getStudyFiles(study,"f");
		Collection<String> listaOrdenMedica= getStudyFiles(study,"om");
		Collection<String> listaMp3= getStudyFiles(study,"mp3");

		study.setArchive(listaArchivos.size()>0);
		study.setOrdenMed(listaOrdenMedica.size()>0);
		study.setMp3(listaMp3.size()>0);

		for(String file: listaArchivos) files.add(new AttachmentDTO(file,"Archivo"));
		for(String file: listaOrdenMedica) files.add(new AttachmentDTO(file,"Orden Médica"));
		for(String file: listaMp3) files.add(new AttachmentDTO(file,"MP3"));

		return files;
	}
	
	@GET @Path("f/{study-id}") @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	
	public Collection<AttachmentDTO> getStudyFiles
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	){

		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studyId);
		if(study == null) return Collections.<AttachmentDTO>emptyList();

		Collection<String> listaArchivos= getStudyFiles(study,"f");
		study.setArchive(listaArchivos.size()>0);
		
		Collection<AttachmentDTO> dto= new ArrayList<Archivos.AttachmentDTO>();
		for(String filename: listaArchivos) dto.add(new AttachmentDTO(filename,"Archivo"));
		return dto;
	}
	
	@GET @Path("om/{study-id}") @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	
	public Collection<AttachmentDTO> getStudyMedOrder
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	){

		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studyId);
		if(study == null) return Collections.<AttachmentDTO>emptyList();

		Collection<String> listaOrdenMedica= getStudyFiles(study,"om");
		study.setOrdenMed(listaOrdenMedica.size()>0);
		
		Collection<AttachmentDTO> dto= new ArrayList<Archivos.AttachmentDTO>();
		for(String filename: listaOrdenMedica) dto.add(new AttachmentDTO(filename,"Orden Médica"));
		return dto;
	}
	
	@GET @Path("mp3/{study-id}") @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")

	public Collection<AttachmentDTO> getStudyMp3
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	){
	
		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studyId);
		if(study == null) return Collections.<AttachmentDTO>emptyList();
	
		Collection<String> listaMp3= getStudyFiles(study,"mp3");
		study.setMp3(listaMp3.size()>0);
		
		Collection<AttachmentDTO> dto= new ArrayList<Archivos.AttachmentDTO>();
		for(String filename: listaMp3) dto.add(new AttachmentDTO(filename, "MP3"));
		return dto;
	}
	
	@POST @Path("{study-id}/{code}") @Consumes(MediaType.MULTIPART_FORM_DATA)
	
	public Response uploadStudyFile
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId,
		@PathParam("code") String code	)throws IOException{

		User user= securityHelper.getUser(request.getSession());		
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		if(study == null) return Response.status(Status.BAD_REQUEST).build();
		
		ServletFileUpload uploader= new ServletFileUpload(new DiskFileItemFactory());
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> parseRequest= uploader.parseRequest(request);
			for (FileItem fileItem: parseRequest) {
				if ("study-file".equals(fileItem.getFieldName())) {
					String uploadedFilename = new File(getStudyUploadDirectory(study,code),fileItem.getName()).getAbsolutePath();
					writeToFile(uploadedFilename, fileItem.get());
					try{ registroEnElLog(study,user,"Archivo anexado", fileItem.getName()); }catch(Exception e){e.printStackTrace();}
					break;
				}
			}
		} catch(FileUploadException e){ return Response.status(Status.INTERNAL_SERVER_ERROR).build(); }

		return Response.ok().build();
	}
	
	@GET @Path("file/{study-id}")
	
	public Response getFile
	(	@Context HttpServletRequest request,
		@Context HttpServletResponse response,
		@PathParam("study-id") String studyId,
		@QueryParam("name") String filename,
		@QueryParam("type") int type	)throws IOException{

		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studyId);
		if(study == null) return Response.status(Status.NOT_FOUND).build();

		File rootDirectory= getStudyUploadDirectory(study,type==1?"f":type==2?"om":"mp3");
		File requestedFile= new File(rootDirectory, filename);

		if(requestedFile==null || !requestedFile.exists()) return Response.status(Status.NOT_FOUND).build();
		if(!requestedFile.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath())) return Response.status(Status.NOT_FOUND).build();

		final String fileMimeType= getFileMimeType(requestedFile);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", requestedFile.getName()));
		
		try{ registroEnElLog(study,user,"Ver Archivo",requestedFile.getName()); }catch(Exception e){e.printStackTrace();}
		return Response.ok(requestedFile).header("Content-Type",fileMimeType).build();
	}
	
	@DELETE @Path("file/{study-id}")
	
	public Response deleteFile
	(	@Context HttpServletRequest request,
		@Context HttpServletResponse response,
		@PathParam("study-id")String studyId, 
		@QueryParam("name")String filename,
		@QueryParam("type") int type	)throws IOException{

		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		if(study == null) return Response.status(Status.NOT_FOUND).build();

		File rootDirectory= getStudyUploadDirectory(study,type==1?"f":"om");
		File requestedFile= new File(rootDirectory, filename);

		if(requestedFile==null || !requestedFile.exists()) return Response.status(Status.NOT_FOUND).build();
		if(!requestedFile.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath())) return Response.status(Status.NOT_ACCEPTABLE).build();

		requestedFile.delete();

		try{ registroEnElLog(study,user,"Eliminación de Archivo",requestedFile.getName()); }catch(Exception e){e.printStackTrace();}
		return Response.ok().build();
	}
	

	private Collection<String> getStudyFiles
	(	ddnet.ejb.entities.Study study, String code	){
		
		Collection<String> files= new ArrayList<String>();
		File studyUploadedFilesDirectory= getStudyUploadDirectory(study,code);
		for(File filename: studyUploadedFilesDirectory.listFiles((FileFilter)FileFileFilter.FILE)) files.add(filename.getName());
		return files;
	}
	private File getStudyUploadDirectory
	(	ddnet.ejb.entities.Study study, String code	){
		
		File rootDirectory= getStudyUploadedFilesRootDirectory(code);
		File studyFileDateFolder= new File(rootDirectory,DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
		if(!studyFileDateFolder.exists()) studyFileDateFolder.mkdirs();
		File studyFilesFolder= new File(studyFileDateFolder, study.getLegacyStudy().getStudyID());
		if(!studyFilesFolder.exists()) studyFilesFolder.mkdirs();
		return studyFilesFolder;
	}
	private File getStudyUploadedFilesRootDirectory(String code){
		return "f".equals(code)? new File(configurationManager.getConfigurationItem("study.uploaded-files.path").getValue()):
			   "om".equals(code)? new File(configurationManager.getConfigurationItem("study.uploaded-orden-medica.path").getValue()):
				   				  new File(configurationManager.getConfigurationItem("study.uploaded-mp3.path").getValue());
	}
	private String getFileMimeType(File file){
		try {
			TikaConfig config= TikaConfig.getDefaultConfig();
			Detector detector= config.getDetector();
			TikaInputStream stream= TikaInputStream.get(file);
			Metadata metadata= new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY,FilenameUtils.getBaseName(file.getCanonicalPath()));
			org.apache.tika.mime.MediaType mediaType = detector.detect(stream, metadata);
			return mediaType.toString();
		} catch(Throwable t){ return MediaType.APPLICATION_OCTET_STREAM; }
	}
	private void writeToFile( String filename,byte[]data )throws IOException{
		OutputStream out= null;
		try{ out= new FileOutputStream(new File(filename));out.write(data); }
		finally{ if(out!=null){ try{ out.flush();out.close(); }catch(Throwable t){} } }
	}	
	
	public static class AttachmentDTO {
		private String filename;
		private String desc;
		public AttachmentDTO(String filename,String desc) {
			this.filename= filename;
			this.desc= desc;
		}
		public String getFilename(){ return filename; }
		public void setFilename(String filename){ this.filename= filename; }
		public String getDesc(){ return desc; }
		public void setDesc(String desc){ this.desc= desc; }
	}
	
	
	public void registroEnElLog 
	(	ddnet.ejb.entities.Study study,
		User user,String accion,String parametro	){
		
		StudyLog logger = new StudyLog();
		logger.setStudyID(study.getId()); 
		logger.setDate(DATE_LOG_FORMAT.format(new Date()));
		logger.setUser(user.getLogin());
		logger.setAction(accion);
		logger.setParameter(parametro);
		loggerManager.persist(logger); 
	}
}
