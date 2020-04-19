package ddnet.web.api.rest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.fop.apps.FOPException;
import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.ReportManagerLocal;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.entities.ReportTemplate;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("reporting")
public class Reporting {
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
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
	
}
