package ddnet.web.api.rest;

import java.text.SimpleDateFormat;
import java.util.Collection;
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
import javax.ws.rs.core.Response.Status;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.IncidenciaManager;
import ddnet.ejb.entities.Study;
import ddnet.ejb.StudyLogManager;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.UserManager;
import ddnet.ejb.entities.Incidencia;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("/study/incidencias")
public class Incidencias {

	@EJB private SecurityHelper securityHelper;
	@EJB private StudyManagerLocal studyManager;
	@EJB private UserManager userManager;
	@EJB private ConfigurationManager configurationManager;
	@EJB private IncidenciaManager iManager;
	@EJB private StudyLogManager loggerManager;
	
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	@GET @Path("{study-id}") @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")

	public Collection<Incidencia> getIncidentFromStudy
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	){

		User user= securityHelper.getUser(request.getSession());
		Study study= studyManager.getStudy(user,studyId);
		return iManager.getByStudyId(study.getId());
	}

	@POST @Path("{study-id}")

	public Response generateIncidentForStudy
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId,
		@FormParam("msgBody")String mensaje,
		@FormParam("msgUrgencyType")int tipoUrgencia,
		@FormParam("msgSolvedUrgency")long urgResuelta,
		@FormParam("msgDeclareDate")String incidentDate,
		@FormParam("msgToSolveDate")String resolutionDate	){
		
		User user= securityHelper.getUser(request.getSession());
		Study study= studyManager.getStudy(user,studyId);
		if(study==null) return Response.status(Status.NO_CONTENT).build();
		
		Incidencia incident= new Incidencia();
		boolean isIncident= tipoUrgencia==1, isResolution= tipoUrgencia==2;
		String param= "";
		
		incident.setStudyID(study.getId());
		incident.setIncidencia(0);
		incident.setMensaje(mensaje);
		incident.setFecha(DATE_LOG_FORMAT.format(new Date()));
		incident.setUsuario(user.getLogin());
		incident.setRefInc(urgResuelta);
		
		if(isIncident){
			incident.setIncidencia(2);
			incident.setInicio(incidentDate);
			incident.setResolucion(resolutionDate);
			study.setState(2);
			param+= "Nueva Urgencia";
		}
		else if(isResolution){
			Incidencia oldIncident= iManager.getById(urgResuelta);
			if(oldIncident!=null) oldIncident.setRefInc(1);
			incident.setIncidencia(1);
			incident.setInicio(oldIncident!=null? oldIncident.getFecha():"");
			incident.setResolucion(oldIncident!=null? oldIncident.getResolucion():"");
			param+= "Urgencia resuelta";
			for(Incidencia i: iManager.getByStudyId(study.getId())){
				if(i.getIncidencia()==2 && i.getRefInc()==0){ study.setState(2); break; }
				else study.setState(1);
			}
		}
		iManager.persist(incident);
		try{ registroEnElLog(study,user,"Nuevo Mensaje",param); } catch(Exception e){e.printStackTrace();}
		
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
