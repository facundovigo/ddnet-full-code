package ddhemo.web.api.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.Status;



import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.digest.DigestUtils;

import ddhemo.ejb.*;
import ddhemo.ejb.entities.Modality;
import ddhemo.ejb.entities.Role;
import ddhemo.ejb.entities.User;
import ddhemo.ejb.entities.Institution;
import ddhemo.ejb.entities.UserInstitution;
import ddhemo.web.security.SecurityHelper;

@Stateless
@Path("abm")
public class ABM {
	
	@EJB
	private abmManager manager;
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UserManager usermanager;
	@EJB
	private RoleManager rolmanager;
	@EJB
	private InstitutionManager instmanager;
	@EJB
	private ModalityManager modmanager;
	
	@POST
	@Path("newuser")
	public void CreateNewUser(	@Context HttpServletRequest request,
								@FormParam("first_name") String fName,
								@FormParam("last_name") String lName,
								@FormParam("login") String login,
								@FormParam("password") String password ) {
		
		User user = new User();
		
		user.setFirstName(fName);
		user.setLastName(lName);
		user.setLogin(login);
		user.setPassword(DigestUtils.md5Hex(password));
		user.setAdministrator(false);
		user.setDeleted(false);
		user.setPasswordExpired(false);
		
		manager.persist(user); 
	}
	
	@POST
	@Path("deleteUser")
	public void deleteUser(@FormParam("login") String login){
		
		User user = usermanager.getByLogin(login);
		manager.remove(user);
		
	}
	
	@POST
	@Path("newinstitution")
	public void CreateNewInstitution(	@Context HttpServletRequest request,
										@FormParam("nombre") String nombre,
										@FormParam("ae") String ae ) {
		
		Institution inst = new Institution();
		
		inst.setName(nombre);
		inst.setRelatedAET(ae);
		inst.setAdministrativelyEnabled(true);
		inst.setDeleted(false);
		
		manager.persist(inst); 
	}
	
	@GET
	@Path("allusers")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<String> getUsersLogin() {
		
		Collection<String> login = new ArrayList<String>();
		
		for(User user : usermanager.getAll()) login.add(user.getLogin());
		
		return login;
	}
	
	@GET
	@Path("allroles")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Role> getRoles() {
		
		return rolmanager.getAll();
	}
	
	@GET
	@Path("allinstitutions")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Institution> getInstitutions() {
		
		return instmanager.getAll();
	}
	
	@GET
	@Path("allmodalities")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Modality> getModalities() {
		
		return modmanager.getAll();
	}
	
	@GET
	@Path("userinstitution/{login}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Institution> getUserInstitution( @PathParam("login") String login) {
		
		User user = usermanager.getByLogin(login);
		
		return manager.getByUser(user);
	}
	
	@POST
	@Path("userinstitution/new")
	@Produces(MediaType.APPLICATION_JSON)
	public void setRolandInstitution(	@Context HttpServletRequest request,
										@FormParam("loginname") String login,
										@FormParam("roleName") String rolename,
										@FormParam("instIDs[]") long[] instIDs) {
		
		User user = usermanager.getByLogin(login);
		Role role = rolmanager.findByName(rolename);
		
		for(long id : instIDs){

			UserInstitution userInst = new UserInstitution();
			
			userInst.setUser(user);
			userInst.setRole(role);
			userInst.setInstitution(instmanager.findById(id)); 
			
			manager.persist(userInst);
		}
		
	}
	
	@POST
	@Path("userinstitution")
	@Produces(MediaType.APPLICATION_JSON)
	public void setInstitution(	@Context HttpServletRequest request,
								@FormParam("loginname") String login,
								@FormParam("roleName") String rolename,
								@FormParam("instIDs0[]") long[] instIDs0,
								@FormParam("instIDs1[]") long[] instIDs1) {
		
		User user = usermanager.getByLogin(login);
		Role role = rolmanager.findByName(rolename);
		
		for(long id : instIDs0){
			
			UserInstitution userInst = manager.getUI(instmanager.findById(id), user); 
			manager.remove(userInst);
		}
		for(long id : instIDs1){
			
			UserInstitution userInst = new UserInstitution();
			
			userInst.setUser(user);
			userInst.setRole(role);
			userInst.setInstitution(instmanager.findById(id)); 
					
			manager.persist(userInst);
		}
		
	}
	
	@POST
	@Path("usermodality")
	@Produces(MediaType.APPLICATION_JSON)
	public void setModality (	@FormParam("loginname") String login,
								@FormParam("names[]") String[] names) {
		
		User user = usermanager.getByLogin(login);
		
		Set<Modality> modalities = new HashSet<Modality>();
		
		for(String mod : names){
			
			modalities.add(modmanager.findByName(mod));
			
		}
		
		user.setModalities(modalities);
	}
	
	
	@GET
	@Path("notdefinedinstitution")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getNotCreatedInstitution() {
		
		return manager.getNotCreatedInstitution();
	}
	
	@POST
	@Path("update-user")
	@Produces(MediaType.APPLICATION_JSON)
	public void UpdateUserData(	@FormParam("value") String value,
								@FormParam("login") String login,
								@FormParam("numCol") int numCol) {
		
		User user = usermanager.getByLogin(login);
		
		switch(numCol){
			case 0: user.setLogin(value);
				break;
			case 1: user.setFirstName(value);
				break;
			case 2: user.setLastName(value);
				break;
			case 3: Role rol = rolmanager.findByName(value);
					for(UserInstitution ui : user.getInstitutions())
						ui.setRole(rol); 
				break;
			case 4: user.setDeleted(value.equals("false"));
				break;
		}
	}
	
	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<UserDTO> getUsers(@Context HttpServletRequest request) {
		
		@SuppressWarnings("unused")
		User currentUser = securityHelper.getUser(request.getSession());
		Collection<UserDTO> userInfo = new ArrayList<UserDTO>();
		Collection<User> allUser = usermanager.getAll();
		int index  = modmanager.getAll().size();
		
		for(User user : allUser){
			
			Role rol = null;
			String[] modalidades = new String[index];
			
			for(UserInstitution ui : user.getInstitutions()) rol = ui.getRole();
			for(Modality modality : user.getModalities()) modalidades[((int)modality.getId())-1] = modality.getName(); 
			
			userInfo.add(new UserDTO(user.getLogin(),
									 user.getFirstName(),
									 user.getLastName(),
									 user.isDeleted(),
									 rol.getName(),
									 modalidades));
		}
		
		return userInfo;
	}
	
	
public class UserDTO {
		
		private String login;
		private String fname;
		private String lname;
		private boolean blocked;
		private String role;
		private String[] mod;
		
		public UserDTO(String login, String fname, String lname, boolean blocked, String role, String[] mod) {			
			this.login = login;
			this.fname = fname;
			this.lname = lname;
			this.blocked = blocked;
			this.role = role;
			this.mod = mod;
		}

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getFname() {
			return fname;
		}

		public void setFname(String fname) {
			this.fname = fname;
		}

		public String getLname() {
			return lname;
		}

		public void setLname(String lname) {
			this.lname = lname;
		}

		public boolean isBlocked() {
			return blocked;
		}

		public void setBlocked(boolean blocked) {
			this.blocked = blocked;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String[] getMod() {
			return mod;
		}

		public void setMod(String[] mod) {
			this.mod = mod;
		}
		
	}
	
}
