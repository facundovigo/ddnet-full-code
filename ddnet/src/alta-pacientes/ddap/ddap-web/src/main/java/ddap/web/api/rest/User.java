package ddap.web.api.rest;

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

import org.apache.commons.codec.digest.DigestUtils;

import ddap.ejb.LoggerManager;
import ddap.ejb.PrivilegiosManager;
import ddap.ejb.UsuariosManager;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.Privilegio;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/user")
public class User {
	
	@EJB private SecurityHelper securityHelper;
	@EJB private UsuariosManager userManager;
	@EJB private PrivilegiosManager pManager;
	@EJB private LoggerManager logManager;
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Usuario getCurrentUser(@Context HttpServletRequest request){
		
		return securityHelper.getUser(request.getSession());
	}

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Usuario> getAll(@Context HttpServletRequest request){
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		return userManager.getAll();
	}
	
	@POST @Path("new")
	public Response createTheUser
	(	@Context HttpServletRequest request,
		@FormParam("login")String login,
		@FormParam("password")String password,
		@FormParam("doPatient")String doPatient,
		@FormParam("doPatientPractice")String doPatientPractice,
		@FormParam("doPractice")String doPractice,
		@FormParam("doEquipment")String doEquipment,
		@FormParam("doLogger")String doLogger,
		@FormParam("doAbm")String doAbm		){
		Usuario u = securityHelper.getUser(request.getSession());
		
		for(Usuario user: userManager.getAll()){
			if(user.getLogin().equals(login)) return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		Usuario newUser= new Usuario();
		newUser.setLogin(login);
		newUser.setPassword(DigestUtils.md5Hex(password));
		userManager.persist(newUser);
		
		newUser= userManager.getByLogin(login);
		Privilegio privilegio= new Privilegio();
		privilegio.setUserId(newUser.getId());
		privilegio.setDoPatient(doPatient!=null);
		privilegio.setDoPatientPractice(doPatientPractice!=null);
		privilegio.setDoPractice(doPractice!=null);
		privilegio.setDoEquipment(doEquipment!=null);
		privilegio.setDoLogger(doLogger!=null);
		privilegio.setDoAbm(doAbm!=null);
		pManager.persist(privilegio);
		
		recordInLog(u.getLogin(),"Creación Nuevo Usuario",login);
		
		return Response.ok().build();
	}
	
	@POST @Path("{login}/privileges")
	public void setPrivileges
	(	@Context HttpServletRequest request,
		@PathParam("login")String login,
		@FormParam("doPatient")String doPatient,
		@FormParam("doPatientPractice")String doPatientPractice,
		@FormParam("doPractice")String doPractice,
		@FormParam("doEquipment")String doEquipment,
		@FormParam("doLogger")String doLogger,
		@FormParam("doAbm")String doAbm		){
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		Usuario currentUser= userManager.getByLogin(login);
		Privilegio privilegio= currentUser.getPrivilegio();
		privilegio.setDoPatient(doPatient!=null);
		privilegio.setDoPatientPractice(doPatientPractice!=null);
		privilegio.setDoPractice(doPractice!=null);
		privilegio.setDoEquipment(doEquipment!=null);
		privilegio.setDoLogger(doLogger!=null);
		privilegio.setDoAbm(doAbm!=null);
	}
	
	public void recordInLog(String login, String accion, String detalle){
		Logger log= new Logger();
		log.setDate(DATE_LOG_FORMAT.format(new Date()));
		log.setUser(login);
		log.setAction(accion);
		log.setDetails(detalle);
		logManager.persist(log);
	}
}
