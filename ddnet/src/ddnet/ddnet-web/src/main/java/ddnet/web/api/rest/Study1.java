package ddnet.web.api.rest;

import java.awt.peer.SystemTrayPeer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.IncidenciaManager;
import ddnet.ejb.ModalityManager;
import ddnet.ejb.StudyLogManager;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.UserManager;
import ddnet.ejb.UserProfileManager;
import ddnet.ejb.abmManager;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacySerie;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.entities.Modality;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserProfile;
import ddnet.web.security.SecurityHelper;


@SuppressWarnings("unused")
@Stateless
@Path("/studies")
public class Study1 {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat CLEAN_DATE_FORMAT = new SimpleDateFormat("yyyymmdd");
	private static final SimpleDateFormat SERIES_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy"); 
	private static final SimpleDateFormat SERIES_TIME_FORMAT = new SimpleDateFormat("HH:mm"); 
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	@EJB
	private StudyManagerLocal studyManager;
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UserManager userManager;
	@EJB
	private abmManager abmManager;
	@EJB
	private ModalityManager modManager;
	@EJB
	private UserProfileManager upManager;
	@EJB
	private IncidenciaManager iManager;
	@EJB
	private ConfigurationManager configManager;
	@EJB
	private StudyLogManager stdLogmanager;
	
	
	@GET
	@Path("/prevReports/{study-id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PrevReportsDTO> getInformesPrevios(@PathParam("study-id") String studyID) {
		
		Study study = studyManager.getIndividualStudy(studyID);
		LegacyStudy legStudy = study.getLegacyStudy();
		LegacyPatient patient = legStudy.getLegacyPatient();
		String patName = patient.getName().replace("^^^^", "").replace("^^^", "")
										  .replace("^^", "").replace("^", " ");
		
		List<PrevReportsDTO> informes = new ArrayList<PrevReportsDTO>();
		
		if(studyManager.getByPatId(legStudy.getId(), patient.getId()) != null &&
		   !studyManager.getByPatId(legStudy.getId(), patient.getId()).isEmpty()) {
			
			for(LegacyStudy s : studyManager.getByPatId(legStudy.getId(), patient.getId())){
				
				Study std = studyManager.getIndividualStudy(s.getStudyID());
				if(std.isReported()){
					informes.add(new PrevReportsDTO(patName, 
													s.getStudyID(), 
													DATE_FORMAT.format(s.getDate()))
								);
				}
			}
		}
		
		else if(studyManager.getByPatId(legStudy.getId(), patient.getPatientID()) != null &&
				!studyManager.getByPatId(legStudy.getId(), patient.getPatientID()).isEmpty()) {
			
			for(LegacyStudy s : studyManager.getByPatId(legStudy.getId(), patient.getPatientID())){
				
				Study std = studyManager.getIndividualStudy(s.getStudyID());
				if(std.isReported()){
					informes.add(new PrevReportsDTO(patName, 
													s.getStudyID(), 
													DATE_FORMAT.format(s.getDate()))
								);
				}
			}
		}
		
		return informes;
	}
	
	
	@GET
	@Path("/patient")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<PatStudiesDTO> getStudiesByPatient(@Context HttpServletRequest request){
		
		User u = securityHelper.getUser(request.getSession());
		Collection<PatStudiesDTO> result = new ArrayList<PatStudiesDTO>();
		Collection<Study> patStudies = studyManager.getStudiesByPatID(u.getLogin());
		
		if(patStudies != null && !patStudies.isEmpty()){
			
			for(Study s : patStudies){
				result.add(new PatStudiesDTO(
						
						s.getLegacyStudy().getFormattedDate() != null && !s.getLegacyStudy().getFormattedDate().isEmpty() ? 
								s.getLegacyStudy().getFormattedDate() : "---",
						
						s.getLegacyStudy().getDescription() != null && !s.getLegacyStudy().getDescription().isEmpty() ? 
								s.getLegacyStudy().getDescription() : "---",
						
						s.getLegacyStudy().getModalities() != null && !	s.getLegacyStudy().getModalities().isEmpty() ? 
								s.getLegacyStudy().getModalities() : "---",
						
						s.getInstitution().getName() != null && !s.getInstitution().getName().isEmpty() ? 
								s.getInstitution().getName() : "---",
								
						s.getLegacyStudy().getPhysician() != null && !s.getLegacyStudy().getPhysician().isEmpty() ? 
								s.getLegacyStudy().getPhysician().replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " ") : "---",
						
						s.getReportStatus() == 3,
						
						s.getLegacyStudy().getStudyID() != null && !s.getLegacyStudy().getStudyID().isEmpty() ? 
								s.getLegacyStudy().getStudyID() : "---",
								
						s.getLegacyStudy().getAccessionNumber() != null && !s.getLegacyStudy().getAccessionNumber().isEmpty() ? 
								s.getLegacyStudy().getAccessionNumber() : "---"
				));
			}
		}
		
		
		return result;
	}
	
	@GET
	@Path("/currentStudy/series/{study-id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<seriesDTO> getCurrentStudySeries(@Context HttpServletRequest request,
													   @PathParam("study-id") String studyID){
		
		User u = securityHelper.getUser(request.getSession());
		Study s = studyManager.getStudy(u, studyID);
		Set<seriesDTO> series = new HashSet<seriesDTO>();
		
		for(LegacySerie ls : s.getLegacyStudy().getLegacySeries()){
			
			series.add(new seriesDTO(
				ls.getPerformedOn() != null ? SERIES_DATE_FORMAT.format(ls.getPerformedOn()) : 
											  SERIES_DATE_FORMAT.format(s.getLegacyStudy().getDate()), 
				ls.getPerformedOn() != null ? SERIES_TIME_FORMAT.format(ls.getPerformedOn()) : 
											  SERIES_TIME_FORMAT.format(s.getLegacyStudy().getDate()), 
				ls.getDescription() != null ? ls.getDescription() : "---", 
				ls.getCantInstances(), 
				ls.getSerieID()));
		}
		
		return series;
	}
	
	/**
	 * 
	 * @author server
	 *
	 *	Registro de eventos en el LOG
	 *
	 */
	public void registroLog(ddnet.ejb.entities.Study study, User user, String accion, String parametro) {
		
		StudyLog logger = new StudyLog();
		
		logger.setStudyID(study.getId()); 
		logger.setDate(DATE_LOG_FORMAT.format(new Date()));
		logger.setUser(user.getLogin()); 
		logger.setAction(accion);
		logger.setParameter(parametro);
		
		stdLogmanager.persist(logger);
	}
	
	
	public class PrevReportsDTO{
		
		private String patName;
		private String studyUID;
		private String studyDate;
		
		public PrevReportsDTO(String patName, String studyUID, String studyDate){
			
			this.patName = patName;
			this.studyUID = studyUID;
			this.studyDate = studyDate;
		}

		public String getPatName() {
			return patName;
		}

		public void setPatName(String patName) {
			this.patName = patName;
		}

		public String getStudyUID() {
			return studyUID;
		}

		public void setStudyUID(String studyUID) {
			this.studyUID = studyUID;
		}

		public String getStudyDate() {
			return studyDate;
		}

		public void setStudyDate(String studyDate) {
			this.studyDate = studyDate;
		}
	}
	
	
	public class PatStudiesDTO{
		
		private String date;
		private String desc;
		private String mod;
		private String inst;
		private String dr;
		private boolean rs;
		private String uid;		
		private String an;
		
		public PatStudiesDTO(String date, String desc, String mod, String inst,
							 String dr, boolean rs, String uid, String an){
			this.date = date;
			this.desc = desc;
			this.mod = mod;
			this.inst = inst;
			this.dr = dr;
			this.rs = rs;
			this.uid = uid;
			this.an = an;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
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

		public String getDr() {
			return dr;
		}

		public void setDr(String dr) {
			this.dr = dr;
		}

		public boolean getRs() {
			return rs;
		}

		public void setRs(boolean rs) {
			this.rs = rs;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getAn() {
			return an;
		}

		public void setAn(String an) {
			this.an = an;
		}
	}
	
	public class seriesDTO{
		
		private String date;
		private String time;
		private String desc;
		private int imgs;
		private String serieUID;
		
		public seriesDTO(String date, String time, String desc, int imgs,
				String serieUID) {
			
			this.date = date;
			this.time = time;
			this.desc = desc;
			this.imgs = imgs;
			this.serieUID = serieUID;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public int getImgs() {
			return imgs;
		}

		public void setImgs(int imgs) {
			this.imgs = imgs;
		}

		public String getSerieUID() {
			return serieUID;
		}

		public void setSerieUID(String serieUID) {
			this.serieUID = serieUID;
		}
		
		
	}
	
	
	
}
