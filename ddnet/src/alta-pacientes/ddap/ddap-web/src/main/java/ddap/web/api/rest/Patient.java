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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ddap.ejb.LoggerManager;
import ddap.ejb.PacientesManager;
import ddap.ejb.PacientesSearchFilter;
import ddap.ejb.ProvinciaManager;
import ddap.ejb.UsuariosManager;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.Paciente;
import ddap.ejb.entities.Provincia;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/patient")
public class Patient {
	
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HHmmss");
	private static final SimpleDateFormat DATE_ALTA_FORMAT= new SimpleDateFormat("dd/MM/yyyy");
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UsuariosManager userManager;
	@EJB
	private PacientesManager patManager;
	@EJB
	private LoggerManager logManager;
	@EJB
	private ProvinciaManager provManager;

	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Paciente> getResult(@Context HttpServletRequest request,
			 							  @QueryParam("tipoDoc") String docType,
			 							  @QueryParam("nroDoc") String docNumber,
			 							  @QueryParam("nombrePac") String name,
			 							  @QueryParam("sexoPac") String patSex,
			 							  @QueryParam("fechaAlta") String insertDate){
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		PacientesSearchFilter filter = new PacientesSearchFilter(docType, docNumber, name, patSex, insertDate);
		Collection<Paciente> all = patManager.findPacientes(filter);
		
		return all == null || all.isEmpty() ? null : all;
	}
	
	@POST
	@Path("/add/{new}")
	public Response addNewPatient(@Context HttpServletRequest request,
								@FormParam("lastName") String lastName,
								@FormParam("firstName") String firstName,
								@FormParam("docType") String docType,
								@FormParam("docNumber") String docNumber,
								@FormParam("radioPatSex") String patSex,
								@FormParam("birthDate") String birthDate,
								@FormParam("phoneNumber") String phoneNumber,
								@FormParam("cellPhoneNumber") String cellPhoneNumber,
								@FormParam("mailAddress") String mailAddress,
								@FormParam("occupation") String occupation,
								@FormParam("observation") String observation,
								@FormParam("address") String address,
								@FormParam("location") String location,
								@FormParam("zipCode") String zipCode,
								@FormParam("province") String province,
								@FormParam("nationality") String nationality,
								@FormParam("medInsurance") String medInsurance,
								@FormParam("plan") String plan,
								@FormParam("numPlan") String numPlan,
								@PathParam("new") String newOrNot){
		
		
		Usuario u = securityHelper.getUser(request.getSession());
		
		Paciente pat = new Paciente();
		boolean isNew = newOrNot.equals("true");
		
		if(!isNew) pat = patManager.getByPatID(docNumber);
		
		if(lastName != null && !lastName.isEmpty())
			pat.setLastName(lastName);
		if(firstName != null && !firstName.isEmpty())
			pat.setFirstName(firstName);
		if(docType != null && !docType.isEmpty())
			pat.setDocType(docType);
		if(docNumber != null && !docNumber.isEmpty())
			pat.setDocNumber(docNumber);
		if(patSex != null && !patSex.isEmpty())
			pat.setPatSex(patSex);
		if(birthDate != null && !birthDate.isEmpty())
			pat.setBirthDate(birthDate);
		if(phoneNumber != null && !phoneNumber.isEmpty())
			pat.setPhoneNumber(phoneNumber);
		if(cellPhoneNumber != null && !cellPhoneNumber.isEmpty())
			pat.setCellPhoneNumber(cellPhoneNumber);
		if(mailAddress != null && !mailAddress.isEmpty())
			pat.setMailAddress(mailAddress);
		if(occupation != null && !occupation.isEmpty())
			pat.setOccupation(occupation);
		if(observation != null && !observation.isEmpty())
			pat.setObservation(observation);
		if(address != null && !address.isEmpty())
			pat.setAddress(address);
		if(location != null && !location.isEmpty())
			pat.setLocation(location);
		if(zipCode != null && !zipCode.isEmpty())
			pat.setZipCode(zipCode);
		if(province != null && !province.isEmpty())
			pat.setProvince(province);
		if(nationality != null && !nationality.isEmpty())
			pat.setNationality(nationality);
		if(medInsurance != null && !medInsurance.isEmpty())
			pat.setMedInsurance(medInsurance);
		if(plan != null && !plan.isEmpty())
			pat.setPlan(plan);
		if(numPlan != null && !numPlan.isEmpty())
			pat.setNumPlan(numPlan);
		
		if(isNew){
			for(Paciente p : patManager.getAll()){
				if(p.getDocNumber().equals(docNumber)) return Response.status(Status.NOT_ACCEPTABLE).build();
			}
			pat.setInsertDate(DATE_ALTA_FORMAT.format(new Date()));
			patManager.persist(pat);
			recordInLog(u.getLogin(), "ALTA DE PACIENTE", lastName + " " + firstName + " ("+docNumber+")");
		
		} else recordInLog(u.getLogin(), "EDICIÓN DE PACIENTE", lastName + " " + firstName + " ("+docNumber+")");
		
		return Response.ok().build();
	}

	@GET
	@Path("/find/{pat-id}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Paciente getByPatientID(@Context HttpServletRequest request,
									 @PathParam("pat-id") String docNumber){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		//recordInLog(u.getLogin(), "BÚSQUEDA DE PACIENTE", docNumber);
		
		if(patManager.getByPatID(docNumber) != null) 
				return patManager.getByPatID(docNumber);
		
		else return null;
	}
	
	@GET
	@Path("/find/{pat-name}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Paciente> getByPatientName(@Context HttpServletRequest request,
									 			@PathParam("pat-name") String name){
	
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		//recordInLog(u.getLogin(), "BÚSQUEDA DE PACIENTE", name);
		
		if(patManager.getByName(name) != null) 
				return patManager.getByName(name);
		
		return null;
	}
	
	@GET
	@Path("/search/{dato}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Paciente> searchPatient(@Context HttpServletRequest request,
									 			@PathParam("dato") String s){
	
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		//recordInLog(u.getLogin(), "BÚSQUEDA DE PACIENTE", s);
		
		if(patManager.searchPat(s) != null) 
				return patManager.searchPat(s);
		
		return null;
	}
	
	
	
	
	
	@POST
	@Path("delete/{patID}")
	public Response deletePatient(	@Context HttpServletRequest request,
									@PathParam("patID") String patID){
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(patID == null || "".equals(patID)) return Response.status(Status.NO_CONTENT).build();
		
		Paciente p = patManager.getByPatID(patID);
		
		patManager.remove(p);
		
		recordInLog(u.getLogin(), "ELIMINACIÓN DE PACIENTE", 
				p.getLastName()+" "+p.getFirstName()+" ("+p.getDocType()+" "+p.getDocNumber()+")");
		
		return Response.ok().build();
	}
	
	
	
	@GET
	@Path("all")
	public Collection<Paciente> getAll(@Context HttpServletRequest request){
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		return patManager.getAll();
	}
	
	
	
	@GET
	@Path("/provincias")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Provincia> getProvinces(@Context HttpServletRequest request){
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		Collection<Provincia> result = provManager.getAll();
		return result.isEmpty() ? null : result;
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

























