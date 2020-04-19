package ddnet.web.api.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import javax.xml.transform.TransformerException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.FOPException;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.ReportManagerLocal;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.entities.ReportTemplate;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("reporting")
public class Reporting {
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_FILE_FORMAT= new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");
	
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private StudyManagerLocal studyManager;
	@EJB
	private ReportManagerLocal reportManager;
	@EJB
	private ConfigurationManager configurationManager;	
	
	@Path("templates/common")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getTemplateNames() {
		return reportManager.getTemplateNames();
	}
	
/***************************************************************************************************/
	
	@Path("templates/methods")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getMethods() {
		return reportManager.getMethods();
	}
	
	@Path("templates/methods/{name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getMethods(@PathParam("name") String name) {
		
		return reportManager.getMethods(name);
	}
	
	@Path("templates/methods/{name}/{name1}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getMethods(@PathParam("name") String name,
									@PathParam("name1") String name1) {
		
		return reportManager.getMethods(name , name1);
	}
	
	@Path("templates/methods/{name}/{name1}/{name2}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8") 
	public List<String> getMethods(@PathParam("name") String name,
									@PathParam("name1") String name1,
									@PathParam("name2") String name2){
		
		return reportManager.getMethods(name, name1, name2);
	}
	
	@Path("templates/methods/{study-id}/{name}/{name1}/{name2}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public ReportTemplate setMethods(@Context HttpServletRequest request,
										@PathParam("study-id") String studyID,
										@PathParam("name") String name,
										@PathParam("name1") String name1,
										@PathParam("name2") String name2) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		ReportTemplate template = reportManager.setMethods(name, name1, name2);
		
		if (template != null && study != null)
			template.readVariablesValuesFrom(study);
		return template;
		
	}
	
	@Path("templates/methods/{study-id}/{name}/{name1}/{name2}/{name3}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public ReportTemplate setMethods(@Context HttpServletRequest request,
										@PathParam("study-id") String studyID,
										@PathParam("name") String name,
										@PathParam("name1") String name1,
										@PathParam("name2") String name2,
										@PathParam("name3") String name3) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		ReportTemplate template = reportManager.setMethods(name, name1, name2, name3);
		
		if (template != null && study != null)
			template.readVariablesValuesFrom(study);
		return template;
	}

	
/***************************************************************************************************/

	@Path("templates/common/{template-name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public ReportTemplate getTemplate(@Context HttpServletRequest request,
			@PathParam("template-name") String templateName,			
			@QueryParam("study-id") String studyID) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = null;
		if (studyID != null && !("".equals(studyID.trim())))
			study = studyManager.getStudy(user, studyID);
		
		
		ReportTemplate template = reportManager.getTemplate(templateName);
		if (template != null && study != null)
			template.readVariablesValuesFrom(study);
		return template;
	}
	
	
	@Path("templates/modalities/{modality}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getTemplateNamesByModality(@PathParam("modality") String modality) {
		return reportManager.getTemplateNames(modality);
	}
	
	@Path("templates/modalities/{modality}/{template-name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public ReportTemplate getTemplate(@Context HttpServletRequest request,
			@PathParam("modality") String modality,
			@PathParam("template-name") String templateName,			
			@QueryParam("study-id") String studyID) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = null;
		if (studyID != null && !("".equals(studyID.trim())))
			study = studyManager.getStudy(user, studyID);		
		
		ReportTemplate template = reportManager.getTemplate(modality, templateName);
		if (template != null && study != null)
			template.readVariablesValuesFrom(study);
		return template;
	}

	@Path("reports/text/{study-id}")
	@GET
	@Produces(MediaType.TEXT_PLAIN + "; charset=UTF-8")
	public Response getReportText(@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("study-id") String studyID) {
		
		User user = securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		if (!study.isReported())
			return Response.status(Status.NOT_FOUND).build();
		
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.txt\"", studyID));
		return Response.ok(study.getReport()).build();
	}
	
	@Path("reports/pdf/{study-id}")
	@GET
	@Produces("application/pdf")
	public Response getReportPDF(@Context HttpServletRequest request,
			@Context HttpServletResponse response, 
			@PathParam("study-id") String studyID) throws FOPException, IOException, TransformerException {
		
		User user = securityHelper.getUser(request.getSession());		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		if (!study.isReported())
			return Response.status(Status.NOT_FOUND).build();
		
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.pdf\"", study.getLegacyStudy().getStudyID()));
		response.setContentType("application/pdf");
		
		final OutputStream outputStream = response.getOutputStream();
		
		String savedReportsPath = configurationManager.getConfigurationItem("report.saved-reports.path").getValue();
		
		File studyReportDateFolder = new File(savedReportsPath, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
		
		outputStream.write(FileUtils.readFileToByteArray(new File(studyReportDateFolder, study.getLegacyStudy().getStudyID() + ".pdf")));
        
		return Response.ok().build();
	}
	
	@GET @Path("report/audio/{study-id}") @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getReportAudios
	(	@Context HttpServletRequest request,
			@PathParam("study-id") String studyId	) throws IOException{
		User user= securityHelper.getUser(request.getSession());		
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		if(study==null) return Collections.<String>emptyList();
		
		List<String> reportAudiosFilesList= new ArrayList<String>();
		
		File reportsPath= new File(getStudyReportPath(study));
		
		if(reportsPath.exists() && reportsPath.isDirectory()){
			File[] f= reportsPath.listFiles();
			
			for(int i=0; i<f.length; i++){
				String fileName= f[i].getName();
				reportAudiosFilesList.add(fileName);
			}
			return reportAudiosFilesList;
		}
		else return Collections.<String>emptyList();
	}
	
	@GET @Path("report/audio/{study-id}/{file}")
	public Response getAudioFile
	(	@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("study-id") String studyId,
			@PathParam("file") String filename		)throws IOException{
		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studyId);
		if(study == null) return Response.status(Status.NOT_FOUND).build();
		
		File reportsPath= new File(getStudyReportPath(study));
		File audioRequested= new File(reportsPath,filename);
		
		if(audioRequested==null || !audioRequested.exists()) return Response.status(Status.NOT_FOUND).build();
		if(!audioRequested.getCanonicalPath().startsWith(reportsPath.getCanonicalPath())) return Response.status(Status.NOT_FOUND).build();
		
		final String fileMimeType= getFileMimeType(audioRequested);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", audioRequested.getName()));
		return Response.ok(audioRequested).header("Content-Type",fileMimeType).build();
	}
	
	@POST @Path("report/audio/{study-id}")
	public Response saveReportAudio
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	) throws IOException{
		
		User user= securityHelper.getUser(request.getSession());		
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		if(study == null) return Response.status(Status.BAD_REQUEST).build();
		
		ServletFileUpload uploader= new ServletFileUpload(new DiskFileItemFactory());
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> parseRequest= uploader.parseRequest(request);
			for (FileItem fileItem: parseRequest) {
				if ("study-file".equals(fileItem.getFieldName())) {
					File reportsPath= new File(getStudyReportPath(study));
					
					//String uploadedFilename = new File(reportsPath,fileItem.getName()).getAbsolutePath();
					String uploadedFilename = new File(reportsPath,DATE_FILE_FORMAT.format(new Date())+".mp3").getAbsolutePath();
					writeToFile(uploadedFilename, fileItem.get());
					
					//try{ registroEnElLog(study,user,"Archivo anexado", fileItem.getName()); }catch(Exception e){e.printStackTrace();}
					study.setAudioReport(true);
					break;
				}
			}
		} catch(FileUploadException e){ return Response.status(Status.INTERNAL_SERVER_ERROR).build(); }

		return Response.ok().build();
	}
	private void writeToFile( String filename,byte[]data )throws IOException{
		OutputStream out= null;
		try{ out= new FileOutputStream(new File(filename));out.write(data); }
		finally{ if(out!=null){ try{ out.flush();out.close(); }catch(Throwable t){} } }
	}
	private String getStudyReportPath(ddnet.ejb.entities.Study study){
		File savedReportsPath= new File(configurationManager.getConfigurationItem("report.saved-reports.path").getValue());
		if (study.getLegacyStudy().getDate() != null) { 
			savedReportsPath= new File(savedReportsPath, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
			if (!savedReportsPath.exists()) savedReportsPath.mkdir();
		}
		savedReportsPath= new File(savedReportsPath, study.getLegacyStudy().getStudyID());
		if (!savedReportsPath.exists()) savedReportsPath.mkdir();
		
		return savedReportsPath.getAbsolutePath();
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
}
