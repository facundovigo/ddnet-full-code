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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.spi.NotAcceptableException;

import ddap.ejb.LoggerManager;
import ddap.ejb.MedicosDerivantesManager;
import ddap.ejb.UsuariosManager;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/medicos")
public class MedicoDerivante {
	
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HHmmss");
	
	@EJB private SecurityHelper securityHelper;
	@EJB private UsuariosManager userManager;
	@EJB private MedicosDerivantesManager medManager;
	@EJB private LoggerManager logManager;
	
	@GET @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<ddap.ejb.entities.MedicoDerivante> getAll
	(	@Context HttpServletRequest request	){
		Usuario u= securityHelper.getUser(request.getSession());
		if(u==null) throw new NotAcceptableException("NO INICIÓ SESIÓN");
		return medManager.getAll();
	}
	
	@Path("new") @POST
	public Response addNewPhysician
	(	@Context HttpServletRequest request,
		@FormParam("name")String name,
		@FormParam("licType")String licenseType,
		@FormParam("licNumber")String licenseNumber		){
		Usuario u= securityHelper.getUser(request.getSession());
		if(u==null) return Response.status(Status.NO_CONTENT).build();
		
		ddap.ejb.entities.MedicoDerivante med= new ddap.ejb.entities.MedicoDerivante();
		med.setName(name);
		med.setLicenseType(licenseType);
		med.setLicenseNumber(licenseNumber);
		medManager.persist(med);
		
		recordInLog(u.getLogin(), "Alta de Médico", name);
		return Response.ok().build();
	}
	
	private void recordInLog(String login, String accion, String detalle){
		Logger log= new Logger();
		log.setDate(DATE_LOG_FORMAT.format(new Date()));
		log.setUser(login);
		log.setAction(accion);
		log.setDetails(detalle);
		logManager.persist(log);
	}
}

























