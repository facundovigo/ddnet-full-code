package ddnet.web.api.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ddnet.ejb.CentroManager;
import ddnet.ejb.MedicoManager;
import ddnet.ejb.MedicoUserManager;
import ddnet.ejb.ModalityManager;
import ddnet.ejb.UserManager;
import ddnet.ejb.entities.Centro;
import ddnet.ejb.entities.User;

@Stateless
@Path("/medicos")
public class Medico {
	
	@EJB
	private MedicoManager medicoManager;
	@EJB
	private MedicoUserManager medicouserManager;
	@EJB
	private ModalityManager modManager;
	@EJB
	private UserManager uManager;
	@EJB
	private CentroManager cManager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<MedicoDTO> getMedicos() {
		List<MedicoDTO> medicos = new ArrayList<MedicoDTO>();
		
		for(ddnet.ejb.entities.Medico medico : medicoManager.getAll()){
			
		for(ddnet.ejb.entities.User us : medicouserManager.getAll(medico.getUserID()))
			
			if(!us.isAdministrator())
			medicos.add(new MedicoDTO(us.getLogin(),medico.getName(),us.getFirstName(),us.getLastName()));
			
		}
		
		return medicos;
	}
	
	@GET
	@Path("/deGuardia")
	@Produces(MediaType.APPLICATION_JSON)
	public List<MedicoDTO> getMedicoDeGuardia() {
		List<MedicoDTO> medicos = new ArrayList<MedicoDTO>();
		
		for(ddnet.ejb.entities.Medico medico : medicoManager.getOnCall()){
			
		for(ddnet.ejb.entities.User us : medicouserManager.getAll(medico.getUserID()))
			
			medicos.add(new MedicoDTO(us.getLogin(),medico.getName(),us.getFirstName(),us.getLastName()));
			
		}
		
		return medicos;
	}
	
	@POST
	@Path("/deGuardia/{med-before}/{med-after}")
	public void setMedicoDeGuardia(	@PathParam("med-before") String medBefore,
									@PathParam("med-after") String medAfter) {
		
		ddnet.ejb.entities.Medico old = medicoManager.getByName(medBefore),
								  now = medicoManager.getByName(medAfter);
		
		old.setOnCall(false);
		now.setOnCall(true);
	}
	
	@GET
	@Path("/info/{user}/{role}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<InfoDTO> getUserData( @PathParam("user") String user,
											@PathParam("role") String role ){
		
		Collection<InfoDTO> dto = new ArrayList<Medico.InfoDTO>();
		User u = uManager.getByLogin(user);
		
		if(role.startsWith("Admin")){
			if(u.getMedico() != null){
			ddnet.ejb.entities.Medico med = u.getMedico();
			
			dto.add(new InfoDTO(med.getMailAddress(), med.getPhone1(), med.getPhone2(), med.getSkype(),
								med.getName(), med.getTitle(), med.getMatricula(), 
								"", "", "", ""));
			}
		}
		else if(role.startsWith("Medico")){
			if(u.getMedico() != null){
			ddnet.ejb.entities.Medico med = u.getMedico();
			
			dto.add(new InfoDTO(med.getMailAddress(), med.getPhone1(), med.getPhone2(), med.getSkype(),
								med.getName(), med.getTitle(), med.getMatricula(), 
								"", "", "", ""));
			}
		}
		else if(role.startsWith("Centro")){
			if(u.getCentro() != null){
			Centro centro = u.getCentro();
			
			dto.add(new InfoDTO(centro.getMailAddress(), centro.getPhone1(), centro.getPhone2(), centro.getSkype(), 
								"", "", "", 
								centro.getAddress(), centro.getLocalidad(), centro.getProvincia(), centro.getName()));
			}
		}
		
		return dto;
	}
	
	@POST
	@Path("/new/{user}/{role}")
	public void setDrData(	@PathParam("user") String user,
							@PathParam("role") String role,
							@FormParam("mail") String mail,
							@FormParam("phone1") String phone1,
							@FormParam("phone2") String phone2,
							@FormParam("skype") String skype,
							@FormParam("ab_name") String abrevName,
							@FormParam("title") String title,
							@FormParam("matr") String matricula,
							@FormParam("address") String address,
							@FormParam("localidad") String localidad,
							@FormParam("provincia") String provincia,
							@FormParam("f_name") String fantName) {
		
		User u = uManager.getByLogin(user);
		
		if(role.startsWith("Admin")){
			ddnet.ejb.entities.Medico med = new ddnet.ejb.entities.Medico();
			
			u.setAdministrator(true);
			med.setUserID(u.getId());
			med.setName("adm");
			med.setMailAddress(mail);
			med.setPhone1(phone1);
			med.setPhone2(phone2);
			med.setSkype(skype);
			
			medicoManager.persist(med);
		}
		else if(role.startsWith("Medico")){
			ddnet.ejb.entities.Medico med = new ddnet.ejb.entities.Medico();
			
			u.setAdministrator(false);
			med.setUserID(u.getId());
			med.setMailAddress(mail);
			med.setName(abrevName);
			med.setMatricula(matricula);
			med.setPhone1(phone1);
			med.setPhone2(phone2);
			med.setSkype(skype);
			med.setTitle(title);
			
			medicoManager.persist(med);
		}
		else if(role.startsWith("Centro")){
			Centro centro = new Centro();
			
			u.setAdministrator(false);
			centro.setUserID(u.getId());
			centro.setMailAddress(mail);
			centro.setAddress(address);
			centro.setLocalidad(localidad);
			centro.setProvincia(provincia);
			centro.setPhone1(phone1);
			centro.setPhone2(phone2);
			centro.setSkype(skype);
			centro.setName(fantName);
			
			cManager.persist(centro);
		}
	}
	
	@POST
	@Path("/modify/{user}/{old-role}/{new-role}")
	public void changeDrData(@PathParam("user") String user,
							 @PathParam("old-role") String oldRole,
							 @PathParam("new-role") String newRole,
							 @FormParam("mail") String mail,
							 @FormParam("phone1") String phone1,
							 @FormParam("phone2") String phone2,
							 @FormParam("skype") String skype,
							 @FormParam("ab_name") String abrevName,
							 @FormParam("title") String title,
							 @FormParam("matr") String matricula,
							 @FormParam("address") String address,
							 @FormParam("localidad") String localidad,
							 @FormParam("provincia") String provincia,
							 @FormParam("f_name") String fantName){
		
		User u = uManager.getByLogin(user);
		
		if(oldRole.startsWith("Admin")){
			if(u.getMedico() != null){
			ddnet.ejb.entities.Medico med = u.getMedico();
			medicoManager.remove(med);
			}
		}
		else if(oldRole.startsWith("Medico")){
			if(u.getMedico() != null){
			ddnet.ejb.entities.Medico med = u.getMedico();
			medicoManager.remove(med);
			}
		}
		else if(oldRole.startsWith("Centro")){
			if(u.getCentro() != null){
			Centro centro = u.getCentro();
			cManager.remove(centro);
			}
		}
		
		setDrData(	user, newRole, 
					mail, phone1, phone2, skype, 
					abrevName, title, matricula, 
					address, localidad, provincia, fantName);
	}
	
	//devolver los usuarios que pueden ser asignados según una modalidad específica
		@GET
		@Path("/modality")
		@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
		public Collection<ddnet.ejb.entities.User> getUserbyModality(
				/*@FormParam("instituciones[]") String[] instIDs,*/ @FormParam("modalidades[]") String[] modalities){
			
			Set<String> userModalities = new HashSet<String>();
			
			for(String mod : modalities) userModalities.add(modManager.findByName(mod).getName().toLowerCase());
			
			return medicouserManager.getUserbyModality(userModalities);
		}
	
	public class MedicoDTO {
		private long id;
		private String name;
		private String login;
		private String uname;
		private String ulname;
		
		public MedicoDTO() {			
			
		}
		public MedicoDTO(long id, String name) {			
			this.id = id;
			this.name = name;
		}
		public MedicoDTO(String login, String name, String uname, String ulname){
			this.login = login;
			this.name = name;
			this.uname = uname;
			this.ulname = ulname;
		}
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLogin() {
			return login;
		}
		public void setLogin(String login) {
			this.login = login;
		}
		public String getUname() {
			return uname;
		}
		public void setUname(String uname) {
			this.uname = uname;
		}
		public String getUlname() {
			return ulname;
		}
		public void setUlname(String ulname) {
			this.ulname = ulname;
		}				
	}
	
	public class InfoDTO {
		
		private String mail;
		private String phone1;
		private String phone2;
		private String skype;
		private String abrevName;
		private String title;
		private String matr;
		private String direcc;
		private String loc;
		private String prov;
		private String secName;
		
		public InfoDTO( String mail, String phone1, String phone2, String skype,
						String abrevName, String title, String matr,
						String direcc, String loc, String prov, String secName){
			
			this.mail = mail;
			this.phone1 = phone1;
			this.phone2 = phone2;
			this.skype = skype;
			this.abrevName = abrevName;
			this.title = title;
			this.matr = matr;
			this.direcc = direcc;
			this.loc = loc;
			this.prov = prov;
			this.secName = secName;
			
		}

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getPhone1() {
			return phone1;
		}

		public void setPhone1(String phone1) {
			this.phone1 = phone1;
		}

		public String getPhone2() {
			return phone2;
		}

		public void setPhone2(String phone2) {
			this.phone2 = phone2;
		}

		public String getSkype() {
			return skype;
		}

		public void setSkype(String skype) {
			this.skype = skype;
		}

		public String getAbrevName() {
			return abrevName;
		}

		public void setAbrevName(String abrevName) {
			this.abrevName = abrevName;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMatr() {
			return matr;
		}

		public void setMatr(String matr) {
			this.matr = matr;
		}

		public String getDirecc() {
			return direcc;
		}

		public void setDirecc(String direcc) {
			this.direcc = direcc;
		}

		public String getLoc() {
			return loc;
		}

		public void setLoc(String loc) {
			this.loc = loc;
		}

		public String getProv() {
			return prov;
		}

		public void setProv(String prov) {
			this.prov = prov;
		}

		public String getSecName() {
			return secName;
		}

		public void setSecName(String secName) {
			this.secName = secName;
		}
		
	}
}
