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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.Status;



import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import ddnet.ejb.*;
import ddnet.ejb.entities.LegacyFileSystem;
import ddnet.ejb.entities.LegacyInstance;
import ddnet.ejb.entities.LegacySerie;
import ddnet.ejb.entities.Modality;
import ddnet.ejb.entities.Provincia;
import ddnet.ejb.entities.Role;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.Institution;
import ddnet.ejb.entities.UserInstitution;
import ddnet.ejb.entities.EstudiosEliminados;
import ddnet.ejb.entities.UserPermissions;
import ddnet.ejb.entities.UserProfile;
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
	@EJB
	private UserProfileManager profManager;
	@EJB
	private ProvinciaManager provManager;
	
	@POST
	@Path("newuser")
	public Response CreateNewUser(@Context HttpServletRequest request,
								  @FormParam("first-name") String fName,
								  @FormParam("last-name") String lName,
								  @FormParam("login") String login,
								  @FormParam("password") String password){
		@SuppressWarnings("unused")
		User u= securityHelper.getUser(request.getSession());
		
		User user= new User();
			user.setFirstName(fName);
			user.setLastName(lName);
			user.setLogin(login);
			user.setPassword(DigestUtils.md5Hex(password));
			user.setAdministrator(false);
			user.setDeleted(false);
			user.setPasswordExpired(false);
		
		for(User anotherUser: usermanager.getAll())
			if(anotherUser.getLogin().equals(login)) return Response.status(Status.NOT_ACCEPTABLE).build();
		
		manager.persist(user);
		return Response.ok().build();
	}
	
	@POST
	@Path("deleteUser")
	public void deleteUser(@FormParam("login") String login){
		User user= usermanager.getByLogin(login);
		manager.remove(user);
	}
	
	@POST
	@Path("newinstitution")
	public Response CreateNewInstitution(@Context HttpServletRequest request,
										 @FormParam("institutionNames[]") String[] names){
		
		if(names==null)	return Response.status(Status.NO_CONTENT).build();
		for(String name: names){
			Institution inst = new Institution();
			inst.setName(name);
			inst.setRelatedAET(name);
			inst.setAdministrativelyEnabled(true);
			inst.setDeleted(false);
			manager.persist(inst);
		}
		return Response.ok().build();
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
		return rolmanager.getNewRoles();
	}
	
	@GET
	@Path("provincias")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Provincia> getAll() {	
		return provManager.getAll();
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
										@FormParam("loginName")String login,
										@FormParam("roleName")String rolename,
										@FormParam("institutionId[]")long[] institutionId){
		
		User user = usermanager.getByLogin(login);
		Role role = rolmanager.findByName(rolename);
		for(long id: institutionId){
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
								@FormParam("loginName")String login,
								@FormParam("roleName")String roleName,
								@FormParam("newInstitutionId[]")long[] newInstitutionId,
								@FormParam("delInstitutionId[]")long[] delInstitutionId){
		@SuppressWarnings("unused")
		User u= securityHelper.getUser(request.getSession());
		
		User user= usermanager.getByLogin(login);
		Role role= rolmanager.findByName(roleName);
		for(long id: newInstitutionId){
			if(manager.getUI(instmanager.findById(id), user)==null){
				UserInstitution inst= new UserInstitution();
				inst.setUser(user);
				inst.setRole(role);
				inst.setInstitution(instmanager.findById(id));
				manager.persist(inst);	
			}
		}
		for(long id: delInstitutionId){
			UserInstitution inst= manager.getUI(instmanager.findById(id), user);
			if(inst!=null) manager.remove(inst);
		}
	}
	
	@POST
	@Path("usermodality")
	@Produces(MediaType.APPLICATION_JSON)
	public void setModality (	@FormParam("loginName") String login,
								@FormParam("names[]") String[] names) {
		
		User user = usermanager.getByLogin(login);
		Set<Modality> modalities = new HashSet<Modality>();
		for(String mod : names) modalities.add(modmanager.findByName(mod));
		user.setModalities(modalities);
	}
	
	
	@GET
	@Path("notdefinedinstitution")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getNotCreatedInstitution(@Context HttpServletRequest request){
		@SuppressWarnings("unused")
		User user= securityHelper.getUser(request.getSession());
		return manager.getNotCreatedInstitution();
	}
	
	@GET
	@Path("notdefinedmodality")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getNotCreatedModality(@Context HttpServletRequest request){
		@SuppressWarnings("unused")
		User user= securityHelper.getUser(request.getSession());
		return manager.getNotCreatedModality();
	}
	
	@POST
	@Path("newmodality")
	public Response createNewModality(@Context HttpServletRequest request,
									  @FormParam("modalityNames[]")String[]names){
		
		if(names==null) return Response.status(Status.NO_CONTENT).build();
		for(String name: names){
			Modality mod = new Modality();
			mod.setName(name);
			manager.persist(mod);
		}
		return Response.ok().build();
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
					for(UserInstitution ui : user.getInstitutions()) ui.setRole(rol);
				break;
			case 4: user.setDeleted(value.equals("false"));
				break;
		}
	}
	
	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<User> getUsers(@Context HttpServletRequest request){
		@SuppressWarnings("unused")
		User u= securityHelper.getUser(request.getSession());
		
		Collection<User> allUsers = new ArrayList<User>();
		for(User user: usermanager.getAll()) if(!user.isPatient()) allUsers.add(user);
		return allUsers;
	}
	
	@GET
	@Path("/info/{user}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public User getInfoByLogin(	@Context HttpServletRequest request,
								@PathParam("user") String login){
		
		@SuppressWarnings("unused")
		User u = securityHelper.getUser(request.getSession());
		
		return usermanager.getByLogin(login);
	}
	
	@POST @Path("/user-permissions")
	public Response setUserPermissions(
		@Context HttpServletRequest request,
		@QueryParam("login") String login,
		@QueryParam("chores") String chores,
		@QueryParam("new") boolean isNew	){
		@SuppressWarnings("unused")
		User u= securityHelper.getUser(request.getSession());
		
		User user= usermanager.getByLogin(login);
		if(user==null) return Response.status(Status.NO_CONTENT).build();
		
		UserPermissions perm;
		perm= isNew? new UserPermissions() : user.getPermissions();
		
		if(chores==null || "".equals(chores)) Response.status(Status.NO_CONTENT).build();
		System.out.println(chores);
		
		perm.setCanVisualStudies(chores.charAt(0)=='1');
		perm.setCanUseAgent(chores.charAt(1)=='1');
		perm.setCanDownloadDicomdir(chores.charAt(2)=='1');
		perm.setCanSimpleView(chores.charAt(3)=='1');
		perm.setCanIntermedView(chores.charAt(4)=='1');
		perm.setCanAdvancedView(chores.charAt(5)=='1');
		perm.setCanAssignStudy(chores.charAt(6)=='1');
		perm.setCanOpenStudyView(chores.charAt(7)=='1');
		perm.setCanDoReports(chores.charAt(8)=='1');
		perm.setCanSaveReports(chores.charAt(9)=='1');
		perm.setCanFinishReport(chores.charAt(10)=='1');
		perm.setCanRecordAudio(chores.charAt(11)=='1');
		perm.setCanPlayAudio(chores.charAt(12)=='1');
		perm.setCanDoMessages(chores.charAt(13)=='1');
		perm.setCanWriteMessage(chores.charAt(14)=='1');
		perm.setCanReassignStudy(chores.charAt(15)=='1');
		perm.setCanAssignPatient(chores.charAt(16)=='1');
		perm.setCanDeclareCD(chores.charAt(17)=='1');
		perm.setCanDoFiles(chores.charAt(18)=='1');
		perm.setCanManageFile(chores.charAt(19)=='1');
		perm.setCanManageOM(chores.charAt(20)=='1');
		perm.setCanManageAudio(chores.charAt(21)=='1');
		perm.setCanViewQR(chores.charAt(22)=='1');
		perm.setCanDoLog(chores.charAt(23)=='1');
		perm.setCanAccessABM(chores.charAt(24)=='1');
		perm.setCanManageUsers(chores.charAt(25)=='1');
		perm.setCanManageStudies(chores.charAt(26)=='1');
		perm.setCanDeleteStudies(chores.charAt(27)=='1');
		perm.setCanGetStatistics(chores.charAt(28)=='1');
		perm.setCanManageMails(chores.charAt(29)=='1');
		perm.setCanViewAllStudies(chores.charAt(30)=='1');
		
		if(isNew) {
			perm.setUserID(user.getId());
			manager.persist(perm);
		}
		
		return Response.ok().build();
	}
	
	
	@POST
	@Path("/new/userprofile/{login}")
	public void addNewUserProfile(	@Context HttpServletRequest request,
									@PathParam("login") String login,
									@FormParam("fancyName") String fancyName,
									@FormParam("mail") String mail,
									@FormParam("skype") String skype,
									@FormParam("phone1") String phone1,
									@FormParam("phone2") String phone2,
									@FormParam("address") String address,
									@FormParam("localidad") String localidad,
									@FormParam("provincia") String provincia,
									@FormParam("mn") String mn,
									@FormParam("mp") String mp,
									@FormParam("title") String title,
									@FormParam("addInfo") String addInfo){
		
		@SuppressWarnings("unused")
		User u = securityHelper.getUser(request.getSession());
		
		User user = usermanager.getByLogin(login);
		UserProfile profile = new UserProfile();
		
		profile.setUserID(user.getId());
		
		if(user.getFirstName()!=null && !user.getFirstName().isEmpty())
			profile.setFirstName(user.getFirstName());
		if(user.getLastName()!=null && !user.getLastName().isEmpty())
			profile.setLastName(user.getLastName());
		
		if(fancyName!=null && !fancyName.isEmpty())
			profile.setFancyName(fancyName);
		if(mail!=null && !mail.isEmpty())
			profile.setEmail(mail);
		if(skype!=null && !skype.isEmpty())
			profile.setSkype(skype);
		if(phone1!=null && !phone1.isEmpty())
			profile.setPhone1(phone1);
		if(phone2!=null && !phone2.isEmpty())
			profile.setPhone2(phone2);
		if(address!=null && !address.isEmpty())
			profile.setAddress(address);
		if(localidad!=null && !localidad.isEmpty())
			profile.setLocation(localidad);
		if(provincia!=null && !provincia.isEmpty())
			profile.setProvince(provincia);
		if(mn!=null && !mn.isEmpty())
			profile.setMN(mn);
		if(mp!=null && !mp.isEmpty())
			profile.setMP(mp);
		if(title!=null && !title.isEmpty())
			profile.setTitle(title);
		if(addInfo!=null && !addInfo.isEmpty())
			profile.setAdditionalInfo(addInfo);
		
		profManager.persist(profile);
	}
	
	
	@POST
	@Path("/modify/userprofile/{login}")
	public void modifyUserProfile(	@Context HttpServletRequest request,
									@PathParam("login") String login,
									@FormParam("fancyName") String fancyName,
									@FormParam("mail") String mail,
									@FormParam("skype") String skype,
									@FormParam("phone1") String phone1,
									@FormParam("phone2") String phone2,
									@FormParam("address") String address,
									@FormParam("localidad") String localidad,
									@FormParam("provincia") String provincia,
									@FormParam("mn") String mn,
									@FormParam("mp") String mp,
									@FormParam("title") String title,
									@FormParam("addInfo") String addInfo){
		@SuppressWarnings("unused")
		User u = securityHelper.getUser(request.getSession());
		
		User user = usermanager.getByLogin(login);
		UserProfile profile = user.getPerfil();
		
		if(profile != null){
			profile.setFancyName(fancyName);
			profile.setEmail(mail);
			profile.setSkype(skype);
			profile.setPhone1(phone1);
			profile.setPhone2(phone2);
			profile.setAddress(address);
			profile.setLocation(localidad);
			profile.setProvince(provincia);
			profile.setMN(mn);
			profile.setMP(mp);
			profile.setTitle(title);
			profile.setAdditionalInfo(addInfo);
		
		}else addNewUserProfile(request, login, fancyName, mail, skype, phone1, phone2, address, localidad, provincia, mn, mp, title, addInfo);
	}
	
	@POST @Path("user/{login}/change-password")
	public Response changeUserPassword
	(	@Context HttpServletRequest request,
		@PathParam("login") String login,
		@FormParam("newPassword")String password	){
		@SuppressWarnings("unused")
		User u = securityHelper.getUser(request.getSession());
		
		User user= usermanager.getByLogin(login);
		if(user==null) return Response.status(Status.NO_CONTENT).build();
		
		user.setPassword(DigestUtils.md5Hex(password));
		
		return Response.ok().build();
	}
	
	
/**
 * pedido POST para eliminar estudio seleccionado
 * 	
 * @param request
 * @param studyUID
 * @param op
 * @return ¿Se eliminó el estudio correctamente?
 * @throws ParseException
 */
	@POST
	@Path("deleteStudy")
	public boolean deleteStudy(@Context HttpServletRequest request,
								@FormParam("studyUID") String studyUID,
								@FormParam("op") int op) throws ParseException{
		
		Study study = studymanager.getIndividualStudy(studyUID);
		User user = securityHelper.getUser(request.getSession());
		boolean flag = false;
		
		switch(op){
					case 1: flag = deleteAllStudy(study, user);
							break;
					case 2:	flag = deleteOnlyImgs(study, user);
							break;
					
		}
		
		return flag;
	}
	
	/**
	 * Función que realiza la opción de eliminar toda la data
	 * 
	 * @param study
	 * @param user
	 * @return
	 * @throws ParseException
	 */
	public boolean deleteAllStudy(Study study, User user) throws ParseException{
		
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
		String omPath = "";
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
			deleted.setImgPath(stdPath);
			deleted.setState(1);
			
			
			if(study.hasArchive()) {
				
				filePath = configurationManager.getConfigurationItem("study.uploaded-files.path").getValue();
				filePath += "/" + DATE_FOLDER_FORMAT.format(DATE_FORMAT.parse(study.getLegacyStudy().getFormattedDate()));
				filePath += "/" + study.getLegacyStudy().getStudyID();
				boolean ok = eliminarcarpeta(filePath);
				if(!ok) deleted.setFilePath(filePath);
			}
			if(study.hasOrdenMed()) {
				
				omPath = configurationManager.getConfigurationItem("study.uploaded-orden-medica.path").getValue();
				omPath += "/" + DATE_FOLDER_FORMAT.format(DATE_FORMAT.parse(study.getLegacyStudy().getFormattedDate()));
				omPath += "/" + study.getLegacyStudy().getStudyID();
				boolean ok = eliminarcarpeta(omPath);
				if(!ok) deleted.setOmPath(omPath);
			}
			if(study.isReported()){
				
				reportPath = configurationManager.getConfigurationItem("report.saved-reports.path").getValue();
				reportPath += "/" + DATE_FOLDER_FORMAT.format(DATE_FORMAT.parse(study.getLegacyStudy().getFormattedDate()));
				reportPath += "/" + study.getLegacyStudy().getStudyID() + ".pdf";
				boolean ok  = eliminarcarpeta(reportPath);
				if(!ok) deleted.setReportPath(reportPath);
			}
			
			studymanager.removeAllStudyData(study.getLegacyStudy());
			deletedManager.persist(deleted);
			
			return true;
		
		}else return false;
		
	}
	
	
	/**
	 * Función que realiza la opción de eliminar solo
	 * las imágenes DICOM
	 * 
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean deleteOnlyImgs(Study study, User user){
		
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
			deleted.setImgPath(stdPath);
			deleted.setState(2);
		
		for(LegacySerie se : study.getLegacyStudy().getLegacySeries()){
			for(LegacyInstance i : se.getLegacyInstances()) manager.remove(i);
			se.setCantInstances(0);
		}
		
		study.getLegacyStudy().setTotalInstances(0);
		deletedManager.persist(deleted);
		
		return true;
		
		}	else return false;
	}
	
/**
 * Función que borra el directorio indicado
 * 
 * @param path
 * @return ¿carpeta eliminada?
 */
	public boolean eliminarcarpeta(String path){
        
        File f = new File(path);
        if(f.isDirectory()) borrarDirectorio(f);
        return f.delete();
    }
	
	
/**
 * Función que recorre dentro del directorio
 * preguntando si contiene otras carpetas
 * 
 * @param directorio
 */
    public void borrarDirectorio (File directorio){
    
        File[] ficheros = directorio.listFiles();
        
        try{
        	for (File fichero : ficheros) {
                    if(fichero.isDirectory())borrarDirectorio(fichero);
                    fichero.delete();
             }
        }catch(Exception e){System.out.println(e.getMessage());}
    }
    
    
    
    
/**
 * Función para obtener los estudios eliminados
 * 
 * @return Lista de estudios ya eliminados
 * @throws ParseException
 */
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
