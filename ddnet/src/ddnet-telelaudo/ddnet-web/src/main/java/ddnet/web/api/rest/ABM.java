package ddnet.web.api.rest;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import ddnet.ejb.*;
import ddnet.ejb.entities.LegacyFileSystem;
import ddnet.ejb.entities.Modality;
import ddnet.ejb.entities.Role;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.Institution;
import ddnet.ejb.entities.UserInstitution;
import ddnet.ejb.entities.EstudiosEliminados;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("abm")
public class ABM {
	
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
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
	@EJB
	private StudyManager studymanager;
	@EJB
	private ConfigurationManager configurationManager;
	@EJB
	private EstudiosEliminadosManager deletedManager;
	
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
	
	@GET
	@Path("notdefinedmodality")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getNotCreatedModality() {
		
		return manager.getNotCreatedModality();
	}
	
	@POST
	@Path("newmodality")
	public void createNewModality(@FormParam("nombre") String name){
		
		Modality mod = new Modality();
		
		mod.setName(name);
		
		manager.persist(mod);
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
	public Collection<UserDTO> getUsers() {
		
		Collection<UserDTO> userInfo = new ArrayList<UserDTO>();
		Collection<User> allUser = usermanager.getAll();
		Collection<Modality> mod = modmanager.getAll();
		int index  = mod.size();
		
		for(User user : allUser){
			
			Role rol = null;
			String[] modalidades = new String[index];
			int[] modIDs = new int[index];
			
			for(UserInstitution ui : user.getInstitutions()) rol = ui.getRole();
			
			int i = 0;
			for(Modality m : mod){
				
				modIDs[i] = (int)m.getId();
				i++;
			}
			for(int j = 0 ; j < index ; j++){
				
				for(Modality m : user.getModalities()){
					
					if((int)m.getId() == modIDs[j]) modalidades[j] = m.getName();
				}
			}
			
			userInfo.add(new UserDTO(user.getLogin(),
									 user.getFirstName(),
									 user.getLastName(),
									 user.isDeleted(),
									 rol.getName(),
									 modalidades));
		}
		
		return userInfo;
	}
	
	@POST
	@Path("deleteStudy")
	public boolean deleteStudy(@Context HttpServletRequest request,
								@FormParam("studyUID") String studyUID) throws ParseException{
		
		Study study = studymanager.getIndividualStudy(studyUID);
		User user = securityHelper.getUser(request.getSession());
		EstudiosEliminados deleted = new EstudiosEliminados();
		LegacyFileSystem fs = studymanager.getStudyonfs(study.getId());
		
		String strF = studymanager.getFileOfStudy(study.getId());
		String strFS = fs.getDirpath();
		
		if(strFS.toLowerCase().equals("archive")){
			
			strFS = configurationManager.getConfigurationItem("dcm4chee.server.default.path").getValue();
			strFS = strFS.replace('\\', '/');
			strFS += "/archive" ;
		}
		
		strF = strF.substring(0, strF.lastIndexOf('/'));
		strF = strF.substring(0, strF.lastIndexOf('/'));
		
		String stdPath = strFS + "/" + strF ;
		String filePath = "";
		String reportPath = "";
		
		if(eliminarcarpeta(stdPath)){
			
			deleted.setStudyID(study.getId());
			deleted.setUserLogin(user.getLogin());
			deleted.setPatientID(study.getLegacyStudy().getLegacyPatient().getPatientID());
			deleted.setPatientName(study.getLegacyStudy().getLegacyPatient().getName()
										.replace("^^^^", "").replace("^^^", "")
										.replace("^^", "").replace("^", " "));
			deleted.setModality(study.getLegacyStudy().getModalities());
			deleted.setInstitutionName(study.getInstitution().getName());
			deleted.setDeletedDate(DATE_LOG_FORMAT.format(new Date()));
			deleted.setState(1);
			
			
			if(study.isHasArchive()) {
				
				filePath = configurationManager.getConfigurationItem("study.uploaded-files.path").getValue();
				filePath += "/" + DATE_FOLDER_FORMAT.format(DATE_FORMAT.parse(study.getLegacyStudy().getFormattedDate()));
				filePath += "/" + study.getLegacyStudy().getStudyID();
				boolean ok = eliminarcarpeta(filePath);
				if(!ok) deleted.setFilePath(filePath);
			}
			if(study.isReported()){
				
				reportPath = configurationManager.getConfigurationItem("report.saved-reports.path").getValue();
				reportPath += "/" + DATE_FOLDER_FORMAT.format(DATE_FORMAT.parse(study.getLegacyStudy().getFormattedDate()));
				reportPath += "/" + study.getLegacyStudy().getStudyID() + ".pdf";
				boolean ok  = eliminarcarpeta(reportPath);
				if(!ok) deleted.setReportPath(reportPath);
			}
			
			studymanager.removeAllStudyData(study.getLegacyStudy().getLegacyPatient());
			deletedManager.persist(deleted);
			
			return true;
		
		}else return false;
		
	}
	
	public boolean eliminarcarpeta(String path){
        
        File f = new File(path);
        if(f.isDirectory()) borrarDirectorio(f);
        return f.delete();
    }
    
    public void borrarDirectorio (File directorio){
    
        File[] ficheros = directorio.listFiles();
        
        try{
        	for (File fichero : ficheros) {
                    if(fichero.isDirectory())borrarDirectorio(fichero);
                    fichero.delete();
             }
        }catch(Exception e){System.out.println(e.getMessage());}
    }
    
    @GET
    @Path("deletedStudies")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Collection<DeletedStudiesDTO> getDeletedStudies() throws ParseException{
    	
    	Collection<DeletedStudiesDTO> dto = new ArrayList<ABM.DeletedStudiesDTO>();
    	
    	for(EstudiosEliminados e : deletedManager.getAll()){
    		
    		dto.add(new DeletedStudiesDTO(	e.getStudyID(),
    										e.getUserLogin(), 
    										e.getPatientID(),
    										e.getPatientName(), 
    										e.getModality(), 
    										e.getInstitutionName(), 
    										DATE_FORMAT.format(DATE_LOG_FORMAT.parse(e.getDeletedDate())), 
    										e.getState())
    				);
    	}
    	
    	return dto;
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

public class DeletedStudiesDTO{
	
	private Long studyID;
	private String login;
	private String patID;
	private String patient;
	private String mod;
	private String inst;
	private String date;
	private int state;
	
	public DeletedStudiesDTO(Long studyID, String login, String patID, String patient,
								String mod, String inst, String date, int state){
		
		this.studyID = studyID;
		this.login = login;
		this.patID = patID;
		this.patient = patient;
		this.mod = mod;
		this.inst = inst;
		this.date = date;
		this.state = state;
	}
	
	public Long getStudyID() {
		return studyID;
	}


	public void setStudyID(Long studyID) {
		this.studyID = studyID;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPatID() {
		return patID;
	}

	public void setPatID(String patID) {
		this.patID = patID;
	}

	public String getPatient() {
		return patient;
	}

	public void setPatient(String patient) {
		this.patient = patient;
	}

	public String getMod() {
		return mod;
	}

	public void setMod(String mod) {
		this.mod = mod;
	}

	public String getInst() {
		return inst;
	}

	public void setInst(String inst) {
		this.inst = inst;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
	
}
