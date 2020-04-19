package ddnet.web.api.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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

import org.apache.commons.codec.digest.DigestUtils;

import ddnet.ejb.ModalityManager;
import ddnet.ejb.UserProfileManager;
import ddnet.ejb.abmManager;
import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.EnvioCorreoManager;
import ddnet.ejb.StudyLogManager;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.UserManager;
import ddnet.ejb.entities.EnvioCorreo;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.Modality;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserProfile;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("/study/assignments")
public class Asignaciones {

	@EJB private SecurityHelper securityHelper;
	@EJB private StudyManagerLocal studyManager;
	@EJB private UserManager userManager;
	@EJB private ConfigurationManager configurationManager;
	@EJB private StudyLogManager loggerManager;
	@EJB private EnvioCorreoManager mailManager;
	@EJB private abmManager abmManager;
	@EJB private ModalityManager modManager;
	@EJB private UserProfileManager profileManager;
	
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	@GET @Path("{study-id}") @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	
	public User getAssignmentFromStudy
	(	@Context HttpServletRequest request,
		@PathParam("study-id")String studyId	){
		
		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		return study.getUstudy()!=null? study.getUstudy().getUser():null;
	}
	
	@POST @Path("") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.APPLICATION_JSON)
	
	public int assignStudies
	(	@Context HttpServletRequest request,
		@FormParam("physicianCode")String physicianCode,
		@FormParam("studiesIDs[]")String[]studiesIDs	){
				
		User requestingUser= securityHelper.getUser(request.getSession());		
		User physician= userManager.getByLogin(physicianCode);
		
		int asignados= studyManager.assignStudies(requestingUser, physician, studiesIDs);
		if(asignados > 0){
		try{
			for(String s: studiesIDs){
			registroEnElLog(studyManager.getIndividualStudy(s),requestingUser,"Asignación de estudio",physician.getLastName()+", "+physician.getFirstName());
			if(physician.getPerfil()!=null && !physician.getPerfil().getEmail().isEmpty())
			sendMail(studyManager.getIndividualStudy(s),physician.getId(),1,physician.getPerfil().getEmail());
			}
		} catch(Exception e){e.printStackTrace();}}
		return asignados;
	}
	
	@POST @Path("reassign")	@Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.APPLICATION_JSON)
	
	public void reAssignStudies
	(	@Context HttpServletRequest request,
		@FormParam("physicianCode")String physicianCode,
		@FormParam("studiesIDs[]")String[] studiesIDs	){
		
		User user= securityHelper.getUser(request.getSession());
		User physician= userManager.getByLogin(physicianCode);
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studiesIDs[0]);
		User lastPhysician= study.getUstudy().getUser();
		
		study.getUstudy().setUser(physician);
		
		try{
			registroEnElLog(study,user,"Reasignación de estudio",physician.getLastName()+", "+physician.getFirstName());
			if(physician.getPerfil()!=null && !physician.getPerfil().getEmail().isEmpty())
				sendMail(study,physician.getId(),1,physician.getPerfil().getEmail());
			if(lastPhysician.getPerfil()!=null && !lastPhysician.getPerfil().getEmail().isEmpty())
				sendMail(study,lastPhysician.getId(),2,lastPhysician.getPerfil().getEmail());
		} catch(Exception e){e.printStackTrace();}
	}
	
	@GET @Path("patient/{study-id}") @Produces(MediaType.APPLICATION_JSON)
	
	public Response getPatientAssignedFromStudy
	(	@Context HttpServletRequest request,
		@PathParam("study-id") String studyId	){
		
		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user,studyId);
		if(study == null) return Response.status(Status.NOT_FOUND).build();
		
		LegacyPatient lp = study.getLegacyStudy().getLegacyPatient();
		return userManager.getByLogin(lp.getPatientID())!=null && userManager.getByLogin(lp.getPatientID()).isPatient()?
				Response.ok().build():Response.status(Status.NO_CONTENT).build();
	}
	
	@POST
	@Path("patient")
	public Response addPatientToSystem
	(	@Context HttpServletRequest request,
		@FormParam("studyId") String studyID,
		@FormParam("email") String email	){
		
		User u= securityHelper.getUser(request.getSession());
		Study study= studyManager.getStudy(u,studyID);
		if(study==null) return Response.status(Status.NO_CONTENT).build();
		
		LegacyPatient patient= study.getLegacyStudy().getLegacyPatient();
		String patName= patient.getName().replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " ");
		User newUser= new User();
		newUser.setFirstName("-");
		newUser.setLastName(patName);
		newUser.setLogin(patient.getPatientID());
		newUser.setPassword(DigestUtils.md5Hex(""));
		newUser.setPasswordExpired(false);
		newUser.setAdministrator(false);
		newUser.setDeleted(false);
		newUser.setPatient(true);
		abmManager.persist(newUser);
		
		completeUserData(patient, email);
		
		//registro del evento en el LOG
		registroEnElLog(study, u, "Asignar Usuario a Paciente", patName+"["+patient.getPatientID()+"]");
		return Response.ok().build();
	}
	
	private void completeUserData(LegacyPatient patient, String email){
		User user= userManager.getByLogin(patient.getPatientID());
		Set<Modality> modalities= new HashSet<Modality>();
		UserProfile prof= new UserProfile();
		
		for(Study s: studyManager.getStudiesByPatID(patient.getPatientID()))
			modalities.add(modManager.findByName(s.getLegacyStudy().getModalities()));
		
		user.setModalities(modalities);
		prof.setUserID(user.getId());
		prof.setLastName(patient.getName());
		prof.setEmail(email);
		prof.setAdditionalInfo("Paciente agregado al sistema por asignación");
		profileManager.persist(prof);
	}
	
	private void registroEnElLog
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
	private void sendMail
	(	ddnet.ejb.entities.Study study,
		Long userID, int asunto, String destinatario	){
		
		EnvioCorreo correo= new EnvioCorreo();
		correo.setStudyID(study.getId());
		correo.setUserID(userID);
		correo.setSubject(asunto);
		correo.setReceiver(destinatario);
		correo.setDateRecord(DATE_LOG_FORMAT.format(new Date()));
		mailManager.persist(correo);
	}
}
