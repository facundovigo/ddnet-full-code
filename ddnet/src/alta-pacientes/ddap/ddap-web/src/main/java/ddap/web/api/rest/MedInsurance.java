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

import ddap.ejb.LoggerManager;
import ddap.ejb.ObrasSocialesManager;
import ddap.ejb.UsuariosManager;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.ObraSocial;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/osocial")
public class MedInsurance {
	
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UsuariosManager userManager;
	@EJB
	private ObrasSocialesManager osManager;
	@EJB
	private LoggerManager logManager;
	
	@POST
	@Path("/new")
	public void addNewMedInsurance(@Context HttpServletRequest request,
								@FormParam("nombre") String name,
								@FormParam("domicilio") String address,
								@FormParam("localidad") String location,
								@FormParam("provincia") String province,
								@FormParam("cp") String zipCode,
								@FormParam("iva") String iva,
								@FormParam("cuit") String cuit,
								@FormParam("pago") String payment){
		
		
		Usuario u = securityHelper.getUser(request.getSession());
		
		ObraSocial os = new ObraSocial();
		
		if(name != null && !name.isEmpty())
			os.setName(name);
		if(address != null && !address.isEmpty())
			os.setAddress(address);
		if(location != null && !location.isEmpty())
			os.setLocation(location);
		if(province != null && !province.isEmpty())
			os.setProvince(province);
		if(zipCode != null && !zipCode.isEmpty())
			os.setZipCode(zipCode);
		if(iva != null && !iva.isEmpty())
			os.setIva(iva);
		if(cuit != null && !cuit.isEmpty())
			os.setCuit(cuit);
		if(payment != null && !payment.isEmpty())
			os.setPayment(payment);
		
		String str = osManager.getLastCode().replace("[","").replace("]","");
		str = str.equals("null") ? "0" : str;
		os.setCode(String.format("%03d", Integer.parseInt(str) + 1));
		
		
		osManager.persist(os);
		
		recordInLog(u.getLogin(), "ALTA DE OBRA SOCIAL", name);
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<ObraSocial> getAll( @Context HttpServletRequest request){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(!osManager.getAll().isEmpty()) return osManager.getAll();
		
		else return null;
	}
	
	
	
	public void recordInLog(String login, String accion, String detalle){
		
		Logger log = new Logger();
		
			log.setDate(DATE_LOG_FORMAT.format(new Date()));
			log.setUser(login);
			log.setAction(accion);
			log.setDetails(detalle);
			
		logManager.persist(log);
	}
}

























