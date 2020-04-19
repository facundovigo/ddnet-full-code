package ddnet.web.api.rest;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.DatosClinicosManager;
import ddnet.ejb.StudyLogManager;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.UserManager;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("/study/datos-clinicos")
public class DatosClinicos {

	@EJB private SecurityHelper securityHelper;
	@EJB private StudyManagerLocal studyManager;
	@EJB private UserManager userManager;
	@EJB private ConfigurationManager configurationManager;
	@EJB private DatosClinicosManager dcManager;
	@EJB private StudyLogManager loggerManager;
	
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	@GET @Path("{study-id}") @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	
	public ddnet.ejb.entities.DatosClinicos getDatosClinicosFromStudy
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	){
		
		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		return study.getDatosclinicos()!=null? study.getDatosclinicos():null;
	}
	
	@POST @Path("{study-id}")

	public Response generateDatosClinicosForStudy
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId,
		@FormParam("cldUrgency")int priority,
		@FormParam("cldNotes")String notes	){
		
		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		ddnet.ejb.entities.DatosClinicos data = new ddnet.ejb.entities.DatosClinicos();
		
		if(study.getDatosclinicos() != null){
			data= study.getDatosclinicos();
			data.setPriority(priority);
			data.setNotes(notes);
			data.setUser(user.getLogin());
		} 
		else {
			data.setStudyID(study.getId());
			data.setPriority(priority);
			data.setNotes(notes);
			data.setUser(user.getLogin());
			dcManager.persist(data);
		}
		try{
			String param= priority==0? "NORMAL": priority==1? "PREFERENTE":"URGENTE";
			registroEnElLog(study,user,"Petición de Datos Clínicos",param);
		} catch(Exception e){e.printStackTrace();}
		
		return Response.ok().build();
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
