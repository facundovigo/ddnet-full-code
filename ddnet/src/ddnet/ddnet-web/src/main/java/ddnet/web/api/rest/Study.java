package ddnet.web.api.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.DatosClinicosManager;
import ddnet.ejb.EnvioCorreoManager;
import ddnet.ejb.IncidenciaManager;
import ddnet.ejb.InformeManager;
import ddnet.ejb.StudyLogManager;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.StudySearchFilter;
import ddnet.ejb.StudySearchFilter.StudyReportStatus;
import ddnet.ejb.StudySearchFilter.StudySearchDateType;
import ddnet.ejb.UserManager;
import ddnet.ejb.entities.EnvioCorreo;
import ddnet.ejb.entities.Informe;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacySerie;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.StudyMedia;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserProfile;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("/studies")
public class Study {

	private static final SimpleDateFormat DATE_FORMAT= new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat DATE_FOLDER_FORMAT= new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat DATE_STUDY_LOG_FORMAT= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat DATE_STUDY_QR_FORMAT= new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@EJB private StudyManagerLocal studyManager;
	@EJB private SecurityHelper securityHelper;
	@EJB private ConfigurationManager configurationManager;
	@EJB private UserManager userManager;
	@EJB private DatosClinicosManager dcManager;
	@EJB private IncidenciaManager incidenciaManager;
	@EJB private StudyLogManager stdLogmanager;
	@EJB private InformeManager infomanager;
	@EJB private EnvioCorreoManager correomanager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<StudyDTO> getStudies(@Context HttpServletRequest request,
			@QueryParam("study-date-type") int studyDateType,
			@QueryParam("study-date-between-from") String studyDateFrom,
			@QueryParam("study-date-between-to") String studyDateTo,
			@QueryParam("study-modality") String modality,
			@QueryParam("patient-data") String patientData,
			@QueryParam("study-accessionnumber") String accessionNumber,
			@QueryParam("study-user") String studyUser,
			@QueryParam("urgente") String urgente,
			@QueryParam("prioridad") String prioridad,
			@QueryParam("adjunto") String adjunto,
			@QueryParam("firmado") String firmado,
			@QueryParam("preinformado") String preinformado,
			@QueryParam("no-leido") String noLeido,
			@QueryParam("incidencia") int incidencia,
			@QueryParam("another-filter") int anotherFilter,
			@QueryParam("patient-data-dob") String patientDOB,
			@QueryParam("study-diagnostic-center") long institutionID,
			@QueryParam("study-report-status") int reportStatus) throws ParseException {
		User user = securityHelper.getUser(request.getSession());
		
		StudyReportStatus reportStatusFilter = StudyReportStatus.getByCode(reportStatus);
		if (reportStatusFilter == null)
			reportStatusFilter = StudyReportStatus.any; 
	
		StudySearchFilter filter = new StudySearchFilter
				(StudySearchDateType.getByCode(studyDateType), 
						getDateFromString(studyDateFrom), getDateFromString(studyDateTo), 
						accessionNumber, patientData,
						getDateFromString(patientDOB), modality, institutionID, reportStatusFilter,
						studyUser, urgente!=null, prioridad!=null, adjunto!=null, 
						firmado!=null, preinformado!=null, noLeido!=null,
						anotherFilter, false);
		
		return flatten(studyManager.findStudies(user, filter));
	}

	@Path("{study-id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public StudyDTO getStudy(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID) throws ParseException {
		User user = securityHelper.getUser(request.getSession());
		
		return flatten(studyManager.getStudy(user, studyID));
	}

	@Path("{study-id}/full")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public FullStudyDTO getFullStudy(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID) throws ParseException {
		User user = securityHelper.getUser(request.getSession());
		
		return getFullStudy(studyManager.getStudy(user, studyID));
	}
	
	@Path("find-new")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<StudyDTO> findNewStudies(@Context HttpServletRequest request){
		User user = securityHelper.getUser(request.getSession());
		StudyReportStatus reportStatusFilter = StudyReportStatus.getByCode(0);
		StudySearchFilter filter = new StudySearchFilter(
			StudySearchDateType.getByCode(2),new Date(),new Date(),
			"","",null,"",-1,reportStatusFilter,"",
			false,false,false,false,false,true,0,true
		);
		return flatten(studyManager.findStudies(user, filter));
	}


	
	@POST @Path("{study-id}/report/body") @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
	
	public Response updateReportBody
	(	@Context final HttpServletRequest request,
		@PathParam("study-id") String studyId,
		@FormParam("reportBody") String reportBody,
		@FormParam("finished") boolean finished,
//		@FormParam("study-report-multiple") int cant,
//		@FormParam("check-case") int checkcase,
		@FormParam("needSecondRead")boolean needSecondRead,
		@FormParam("isTeachingFile")boolean isTeachingFile,
		@FormParam("isEmergency")boolean isEmergency	){
		
		final User user= securityHelper.getUser(request.getSession());
		final ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyId);
		if(study==null) return Response.status(Status.NO_CONTENT).build();
		
		if(study.isReported()) return Response.notModified().build();
		
		//boolean needSecondRead= sr==1, isTeachingFile= tf==1, isEmergency= em==1;

		study.setReport(reportBody);
		study.setReported(finished);
		
		if(finished){	//saveReportFile(study, editor);
			try{ generateStudyPDF(study, user, reportBody); } catch(Exception e){e.printStackTrace();}
			
			if(study.getInforme() == null){
				registroInforme( study, user, 
					2, 										// Estado del Informe
					1, 										// Múltiples Informes [sin uso]
					needSecondRead,							// Informe pedido para Segunda Lectura
					isTeachingFile,							// Informe marcado como Teaching File
					isEmergency,							// Informe marcado como Emergencia Médica
					DATE_LOG_FORMAT.format(new Date()), 	// Registrar fecha última modificación
					DATE_LOG_FORMAT.format(new Date())		// Registrar fecha de firma Informe
				);

			} else {
				study.getInforme().setUserLogin(user.getLogin());
				study.getInforme().setState(2);
				study.getInforme().setMult(1);
				study.getInforme().settFile(isTeachingFile);
				study.getInforme().setEmergency(isEmergency);
				study.getInforme().setLastChangeDate(DATE_LOG_FORMAT.format(new Date()));
				study.getInforme().setSignedDate(DATE_LOG_FORMAT.format(new Date()));
			}
				
			//registrar informe FIRMADO en el logger
			registroLog(study, user, "Firma de informe", "");
				
			//si el informe se marcó como EMERGENCIA MÉDICA, envía un mail a cada administrador
			if(isEmergency) {
				for(User u : userManager.getAdministratorUsers()){
					if(u.getLogin().equals("admin")) continue;
					if(u.getPerfil()!=null && !u.getPerfil().getEmail().isEmpty())
					sendMail(study, user.getId(), 3, u.getPerfil().getEmail());
				}
			}

		} else {
			if(study.getInforme() == null){
				registroInforme( study, user, 
					1, 										// Estado del Informe
					1, 										// Múltiples Informes [sin uso]
					false,									// Informe pedido para Segunda Lectura
					false,									// Informe marcado como Teaching File
					false,									// Informe marcado como Emergencia Médica
					DATE_LOG_FORMAT.format(new Date()), 	// Registrar fecha última modificación
					""										// Registrar fecha de firma Informe
				);

			} else{
				study.getInforme().setUserLogin(user.getLogin());
				study.getInforme().setLastChangeDate(DATE_LOG_FORMAT.format(new Date()));
			}
			//registrar informe GUARDADO en el logger
			registroLog(study, user, "Informe editado", "");
		}
		return Response.ok().build();
	}
	
	private void registroInforme
	(	ddnet.ejb.entities.Study study, User user, 
		int estado, int cant, boolean sr, boolean tf, boolean em,
		String fecha_mod, String fecha_firma/*,int state*/	){

		Informe report= new Informe();
		report.setStudyID(study.getId());
		report.setUserLogin(user.getLogin());
		report.setState(estado);
		report.setSecondReading(sr);
		report.settFile(tf);
		report.setEmergency(em);
		report.setLastChangeDate(fecha_mod);
		report.setSignedDate(fecha_firma);
		report.setMult(cant);
		infomanager.persist(report);
	}
	

	@Path("{study-id}/report/body")
	@GET
	@Produces(MediaType.TEXT_PLAIN + "; charset=UTF-8")
	public String getReportBody(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID) {		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		return study.getReport();
	}
	
	@Path("clear-report/{study-id}")
	@POST
	public Response clearReport(@Context HttpServletRequest request,
								@PathParam("study-id") String studyId){
		User user= securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study= studyManager.getStudy(user, studyId);
		if(study==null) return Response.status(Status.NO_CONTENT).build();
		
		study.setReported(false);
		
		return Response.ok().build();
	}
	
	@Path("{study-id}/study-log")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<StudyLogDTO> getLogger(@PathParam("study-id") String studyID) throws ParseException {
		
		ddnet.ejb.entities.Study study = studyManager.getIndividualStudy(studyID);
		
		Collection<StudyLogDTO> logger = new ArrayList<StudyLogDTO>();
		
		for(StudyLog l : stdLogmanager.getStudyLogbyId(study.getId())){
			
			logger.add(new StudyLogDTO(	DATE_STUDY_LOG_FORMAT.format(DATE_LOG_FORMAT.parse(l.getDate())),
										l.getUser(),
										l.getTime(),
										l.getAction(),
										l.getParameter()));
		}
		
		return logger;
	}
	
	
	@Path("{study-id}/media")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getStudyMedia(@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("study-id") String studyID,
			@QueryParam("quality") int quality,
			@QueryParam("options") List<String> options,
			@QueryParam("series") List<String> series) {		
		User user = securityHelper.getUser(request.getSession());
		
		if (request.getParameter("series") == null)
			series = null;
		
		final StudyMedia studyMedia = studyManager.getStudyMedia(user, studyID, quality, series, options);
		
		String seriesDescription = null;
		if (series != null && !series.isEmpty()) {
			for(LegacySerie s : studyMedia.getStudy().getLegacyStudy().getLegacySeries()) {
				String serieDescription = (s.getDescription() == null || "".equals(s.getDescription().trim()) )? s.getNumber() : s.getDescription();  
				if (series.contains(s.getSerieID()) && serieDescription != null && !"".equals(serieDescription.trim())) {
					if (seriesDescription == null) 
						seriesDescription = "";
					seriesDescription += "[" + serieDescription + "]";
				}
			}
		}
		
		String qualityLabel = (quality == StudyMedia.LOSSLESS ? StudyMedia.LOSSLESS_LABEL : StudyMedia.LOSSY_LABEL);
		final LegacyPatient patient = studyMedia.getStudy().getLegacyStudy().getLegacyPatient();
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", 
				patient.getPatientID() + "_" + patient.getName().replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " ") 
					+ "-DICOMDIR-(" + qualityLabel.toUpperCase() + ")" +
					((seriesDescription != null) ? ("-SERIES-" + seriesDescription) : "") + ".zip"));
		response.setHeader("Content-Length", String.valueOf(studyMedia.getContents().length));
		
		return Response.ok(studyMedia.getContents()).build();
	}
		
	private File getStudyUploadedFilesRootDirectory(String code){
		return "f".equals(code)? new File(configurationManager.getConfigurationItem("study.uploaded-files.path").getValue()):
								 new File(configurationManager.getConfigurationItem("study.uploaded-orden-medica.path").getValue());
	}
	
	private Date getDateFromString(String input) throws ParseException {
		return (input != null && !input.trim().equals("")) ? DATE_FORMAT.parse(input) : null;
	}
	
	
	public void registroLog(ddnet.ejb.entities.Study study, User user, String accion, String parametro) {
		
		StudyLog logger = new StudyLog();
		
		logger.setStudyID(study.getId()); 
		logger.setDate(DATE_LOG_FORMAT.format(new Date()));  
		logger.setUser(user.getLogin()); 
		logger.setAction(accion);
		logger.setParameter(parametro);
		
		stdLogmanager.persist(logger); 
	}
	
	
	
	@Path("{study-id}/report-info")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public ReportDTO getStudyReportInfo(@Context HttpServletRequest request,
										@PathParam("study-id") String studyID){
		
		User user = securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		if(study == null) return null;
		
		ReportDTO dto = new ReportDTO();
		
		dto.setRs(study.getReportStatus());
		dto.setReportBody(study.getReport());
		if(study.getInforme() != null){
			Informe inf= study.getInforme();
			dto.setRegionCount(inf.getMult());
			dto.setTeachingFile(inf.istFile());
			dto.setEmergency(inf.isEmergency());
			dto.setSecondReading(inf.isSecondReading());
			//if(inf.isSecondReading()) dto.setDiscrep(inf.getSegundaLectura().getGrado());
			//else dto.setDiscrep(-1);
			dto.setValidateReport(inf.isCheckCase());
			if(inf.isCheckCase()) dto.setValidateReportState(inf.getCheckState());
			dto.setReportUser(inf.getUserLogin());
			if(inf.getSignedDate()!=null && !inf.getSignedDate().isEmpty())
			try {
				dto.setReportDate(DATE_FORMAT.format(DATE_LOG_FORMAT.parse(inf.getSignedDate())));
			} catch (ParseException e) {dto.setReportDate(inf.getSignedDate());}
		}
		
		return dto;
	}

	private StudyDTO flatten(ddnet.ejb.entities.Study study) {
		return flatten(Collections.singletonList(study)).iterator().next();
	}
	
	private Collection<StudyDTO> flatten(Collection<ddnet.ejb.entities.Study> studies) {
		Collection<StudyDTO> result = new ArrayList<StudyDTO>();
		File fUploadsRoot= getStudyUploadedFilesRootDirectory("f");
		File omUploadsRoot= getStudyUploadedFilesRootDirectory("om");
		File mp3UploadsRoot= getStudyUploadedFilesRootDirectory("mp3");
		
		StudyDTO dto = null;
		for(ddnet.ejb.entities.Study study : studies) {
			dto = new StudyDTO();
			File studyFileDateFolder = new File(fUploadsRoot, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
			File studyOMDateFolder = new File(omUploadsRoot, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
			File studyMp3DateFolder = new File(mp3UploadsRoot, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
			
			try { dto.setId(study.getLegacyStudy().getStudyID()); } catch (Throwable t) {}
			try { dto.setPID(study.getLegacyStudy().getLegacyPatient().getPatientID()); } catch (Throwable t) {}
			try { dto.setPN(study.getLegacyStudy().getLegacyPatient().getName().replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " "));}catch(Throwable t){}
			try { dto.setAge(study.getLegacyStudy().getLegacyPatient().getCalculatedAge()); } catch (Throwable t) {}
			try { dto.setDate(study.getLegacyStudy().getFormattedDate()); } catch (Throwable t) {}
			try { dto.setMod(study.getLegacyStudy().getModalities()); } catch (Throwable t) {}
			try { dto.setDesc(study.getLegacyStudy().getDescription()); } catch (Throwable t) {}
			try { dto.setRS(study.getReportStatus()); } catch (Throwable t) {}
			try { dto.setFC(new File(studyFileDateFolder, study.getLegacyStudy().getStudyID()).list().length +
							new File(studyOMDateFolder, study.getLegacyStudy().getStudyID()).list().length +
							new File(studyMp3DateFolder, study.getLegacyStudy().getStudyID()).list().length
				);} catch(Throwable t) {}
			try { dto.setOM(new File(studyOMDateFolder, study.getLegacyStudy().getStudyID()).list().length); } catch (Throwable t) {}
			try { dto.setMp3(new File(studyMp3DateFolder, study.getLegacyStudy().getStudyID()).list().length); } catch (Throwable t) {}
			try { dto.setInc(study.getState()==2? 2: incidenciaManager.getByStudyId(study.getId())!=null? 1:0); } catch (Throwable t) {}
			try { dto.setDr(
					study.getUstudy().getUser().getPerfil() != null && 
					study.getUstudy().getUser().getPerfil().getFancyName() != null ?
							study.getUstudy().getUser().getPerfil().getFancyName() :
							study.getUstudy().getUser().getLogin()
				);} catch (Throwable t) {}
			try { dto.setUrg(study.getDatosclinicos().getPriority());} catch (Throwable t) {}
			try { dto.setIID(study.getInstitution().getId()); } catch (Throwable t) {}
			try { dto.setTI(study.getLegacyStudy().getTotalInstances()); } catch (Throwable t) {} 
			try { dto.setC(study.getInstitution().getName()); } catch (Throwable t) {}
			try { dto.setAN(study.getLegacyStudy().getAccessionNumber()); } catch (Throwable t) {}
			if(study.getInforme() != null){
				try { dto.setCc(study.getInforme().isCheckCase()); } catch (Throwable t) {}
				try { dto.setTf(study.getInforme().istFile()); } catch (Throwable t) {}
				try { dto.setEm(study.getInforme().isEmergency()); } catch (Throwable t) {}
			}
			try { dto.setQrdate(DATE_STUDY_QR_FORMAT.format(study.getLegacyStudy().getDate())); } catch (Throwable t) {}
			result.add(dto);
		}
		return result;
	}
	
	private FullStudyDTO getFullStudy(ddnet.ejb.entities.Study study) {
		FullStudyDTO dto = new FullStudyDTO();
		
		dto.setStudyID(study.getLegacyStudy().getStudyID());
		dto.setDescription(study.getLegacyStudy().getDescription());
		dto.setAccessionNumber(study.getLegacyStudy().getAccessionNumber());
		dto.setPatientID(study.getLegacyStudy().getLegacyPatient().getPatientID());
		dto.setPatientName(study.getLegacyStudy().getLegacyPatient().getName()
				.replace("^^^^", "")
				.replace("^^^", "")
				.replace("^^", "")
				.replace("^", " "));
		dto.setModality(study.getLegacyStudy().getModalities());
		dto.setAge(study.getLegacyStudy().getLegacyPatient().getCalculatedAge());
		dto.setSex(study.getLegacyStudy().getLegacyPatient().getSex());
		dto.setStudyDate(study.getLegacyStudy().getDate());
		dto.setFormattedStudyDate(study.getLegacyStudy().getFormattedDate());
		dto.setInstitutionID(study.getInstitution().getId());
		dto.setInstitutionName(study.getInstitution().getName());
		dto.setNumImgs(study.getLegacyStudy().getTotalInstances());
		dto.setAudioReport(study.isAudioReport());
		
		return dto;
	}



	public class StudyDTO 
	{
		/**
		 * Study IUID.
		 */
		private String id = "?";
		/**
		 * Patient ID.
		 */
		private String pid = "?";
		/**
		 * Patient name.
		 */
		private String pn = "?";
		/**
		 * Patient age.
		 */
		private String age = "?";
		/**
		 * Study date.
		 */
		private String date = "?";
		/**
		 * Modalities.
		 */
		private String mod = "?";
		/**
		 * Study description.
		 */
		private String desc = "...";
		/**
		 * Study's report status.
		 */
		private int rs = 0;
		/**
		 * # of uploaded files for this study. 
		 */
		private int fc = 0;
		
		private int om = 0;
		
		private int mp3= 0;
		
		/**
		 * Note Status: 0 = no notes written. 1 = Notes written.
		 */
		private int ns = 0;
		
		private int inc = 0;
		
		private String dr = "";
		
		/**
		 * Estado de URGENCIA
		 */
		private int urg = 0;
		
		/**
		 * Instituion ID.
		 */
		private long iid = -1;

		/**
		 * Total Instances: cantidad total de imágenes que tiene este estudio.
		 */
		private int ti = 0;					

		/**
		 * Center: Centro de diagnóstico.
		 */
		private String c;

		/**
		 * Accession Number (ID ESTUDIO)
		 */
		private String an= "---";
		
		/**
		 * Comprobar caso 
		 */
		private boolean cc;
		
		/**
		 * Teaching File 
		 */
		private boolean tf;
		
		/**
		 * Emergencia Medica 
		 */
		private boolean em;
		/**
		 * Study date.
		 */
		private String qrdate = "?";
		
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id != null ? id : "?";
		}
		public String getPID() {
			return pid;
		}
		public void setPID(String pid) {
			this.pid = pid != null ? pid : "?";
		}
		public String getPN() {
			return pn;
		}
		public void setPN(String pn) {
			this.pn = pn != null ? pn : "?";
		}
		public String getAge() {
			return age;
		}
		public void setAge(String age) {
			this.age = age != null ? age : "?";
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date != null ? date : "?";
		}
		public String getMod() {
			return mod;
		}
		public void setMod(String mod) {
			this.mod = mod != null ? mod : "?";
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc != null ? desc : "---";
		}
		public int getRS() {
			return rs;
		}
		public void setRS(int rs) {
			this.rs = rs;
		}
		public int getFC() {
			return fc;
		}
		public void setFC(int fc) {
			this.fc = fc;
		}
		public int getOM() {
			return om;
		}
		public void setOM(int om) {
			this.om = om;
		}
		public int getMp3() {
			return mp3;
		}
		public void setMp3(int mp3) {
			this.mp3 = mp3;
		}
		public int getNS() {
			return ns;
		}
		public void setNS(int ns) {
			this.ns = ns;
		}
		public long getIID() {
			return iid;
		}
		public void setIID(long iid) {
			this.iid = iid;
		}
		public int getTI() {			
			return ti;
		}
		public void setTI(int ti) {		
			this.ti = ti;	
		}
		public String getC() {
			return c;
		}
		public void setC(String c) {
			this.c = c;
		}

		public String getAN() {
			return an;
		}
		public void setAN(String accessionNumber) {
			this.an= accessionNumber!=null ? accessionNumber:"---";
		}
		public int getInc() {
			return inc;
		}
		public void setInc(int inc) {
			this.inc = inc;
		}
		public String getDr() {
			return dr;
		}
		public void setDr(String dr) {
			this.dr = dr;
		}
		public int getUrg() {
			return urg;
		}
		public void setUrg(int urg) {
			this.urg = urg;
		}
		public boolean isCc() {
			return cc;
		}
		public void setCc(boolean cc) {
			this.cc = cc;
		}
		public boolean isTf() {
			return tf;
		}
		public void setTf(boolean tf) {
			this.tf = tf;
		}
		public boolean isEm() {
			return em;
		}
		public void setEm(boolean em) {
			this.em = em;
		}
		public String getQrdate() {
			return qrdate;
		}
		public void setQrdate(String qrdate) {
			this.qrdate = qrdate;
		}
		
	}

	public class FullStudyDTO {
		private String patientName;
		private String studyID;
		private String description;
		private String patientID;
		private String modality;
		private String age;
		private String sex;
		private Date studyDate;
		private String formattedStudyDate;
		private String accessionNumber = "";
		private long institutionID;
		private String institutionName;
		private int numImgs;
		private boolean audioReport;
		
		public String getPatientName() {
			return patientName;
		}

		public void setPatientName(String patientName) {
			this.patientName = patientName;
		}

		public String getStudyID() {
			return studyID;
		}

		public void setStudyID(String studyID) {
			this.studyID = studyID;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String studyDescription) {
			this.description = studyDescription;
		}
		
		public String getPatientID() {
			return patientID;
		}

		public void setPatientID(String patientID) {
			this.patientID = patientID;
		}

		public String getModality() {
			return modality;
		}

		public void setModality(String modality) {
			this.modality = modality;
		}

		public String getAge() {
			return age;
		}

		public void setAge(String age) {
			this.age = age;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public Date getStudyDate() {
			return studyDate;
		}

		public void setStudyDate(Date studyDate) {
			this.studyDate = studyDate;
		}

		public String getFormattedStudyDate() {
			return formattedStudyDate;
		}

		public void setFormattedStudyDate(String formattedStudyDate) {
			this.formattedStudyDate = formattedStudyDate;
		}

		public String getAccessionNumber() {
			return accessionNumber;
		}

		public void setAccessionNumber(String accessionNumber) {
			this.accessionNumber = accessionNumber;
		}

		public long getInstitutionID() {
			return institutionID;
		}

		public void setInstitutionID(long institutionID) {
			this.institutionID = institutionID;
		}

		public String getInstitutionName() {
			return institutionName;
		}

		public void setInstitutionName(String institutionName) {
			this.institutionName = institutionName;
		}

		public int getNumImgs() {
			return numImgs;
		}

		public void setNumImgs(int numImgs) {
			this.numImgs = numImgs;
		}

		public boolean isAudioReport() {
			return audioReport;
		}

		public void setAudioReport(boolean audioReport) {
			this.audioReport = audioReport;
		}
		
	}	
	
	public static class SerieDTO {
		private static final SimpleDateFormat SERIES_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy"); 
		private static final SimpleDateFormat SERIES_TIME_FORMAT = new SimpleDateFormat("HH:mm"); 
		
		private Date serieDate;
		private String description;
		private String number;
		private int numberOfInstances;
		private boolean lossy;
		private boolean lossless;
		private String serieID;
		
		public SerieDTO() {		
		}
		
		public SerieDTO(Date serieDate, String description, String number, int numberOfInstances, boolean lossy, boolean lossless, String serieID) {
			this.serieDate = serieDate;
			this.description = description;
			this.number = number;
			this.numberOfInstances = numberOfInstances;
			this.lossy = lossy;
			this.lossless = lossless;
			this.serieID = serieID;
		}
				
		public Date getSerieDateTime() {
			return serieDate;
		}
		public void setSerieDateTime(Date serieDate) {
			this.serieDate = serieDate;
		}
		public String getDescription() {
			return description == null || description.trim().equals("") ? number : description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public int getNumberOfInstances() {
			return numberOfInstances;
		}
		public void setNumberOfInstances(int numberOfInstances) {
			this.numberOfInstances = numberOfInstances;
		}
		public boolean isLossy() {
			return lossy;
		}
		public void setLossy(boolean lossy) {
			this.lossy = lossy;
		}
		public boolean isLossless() {
			return lossless;
		}
		public void setLossless(boolean lossless) {
			this.lossless = lossless;
		}
		public String getFormattedDate() {
			return serieDate != null ? SERIES_DATE_FORMAT.format(serieDate) : "";
		}
		public String getFormattedTime() {
			return serieDate != null ? SERIES_TIME_FORMAT.format(serieDate) : "";
		}
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}

		public String getSerieID() {
			return serieID;
		}

		public void setSerieID(String serieID) {
			this.serieID = serieID;
		}
		
	}		
	
	public static class AttachmentDTO {
		private String filename;
		private String desc;

		public AttachmentDTO(String filename, String desc) {
			this.filename = filename;
			this.desc = desc;
		}
		
		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
	
	public class StudyLogDTO {
		private String date;
		private String user;
		private String time;
		private String action;
		private String param;
		
		public StudyLogDTO(String date, String user, String time, String action, String param) {
			this.date = date;
			this.user = user;
			this.time = time;
			this.action = action;
			this.param = param;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getParam() {
			return param;
		}

		public void setParam(String param) {
			this.param = param;
		}	
	}
	
	public class ReportDTO{
		
		private int rs;
		private String reportBody;
		private int regionCount; // "Multiples"
		private boolean teachingFile;
		private boolean emergency;
		private boolean validateReport;
		private int validateReportState;
		private boolean secondReading;
		private int discrep;
		private String reportUser;
		private String reportDate;

		public int getRs() {
			return rs;
		}

		public void setRs(int rs) {
			this.rs = rs;
		}

		public String getReportBody() {
			return reportBody;
		}

		public void setReportBody(String reportBody) {
			this.reportBody = reportBody;
		}
		
		public int getRegionCount() {
			return regionCount;
		}

		public void setRegionCount(int regionCount) {
			this.regionCount = regionCount;
		}

		public boolean isTeachingFile() {
			return teachingFile;
		}

		public void setTeachingFile(boolean teachingFile) {
			this.teachingFile = teachingFile;
		}

		public boolean isEmergency() {
			return emergency;
		}

		public void setEmergency(boolean emergency) {
			this.emergency = emergency;
		}

		public boolean isValidateReport() {
			return validateReport;
		}

		public void setValidateReport(boolean validateReport) {
			this.validateReport = validateReport;
		}
		
		public int getValidateReportState() {
			return validateReportState;
		}

		public void setValidateReportState(int validateReportState) {
			this.validateReportState = validateReportState;
		}

		public boolean isSecondReading() {
			return secondReading;
		}

		public void setSecondReading(boolean secondReading) {
			this.secondReading = secondReading;
		}

		public int getDiscrep() {
			return discrep;
		}

		public void setDiscrep(int discrep) {
			this.discrep = discrep;
		}

		public String getReportUser() {
			return reportUser;
		}

		public void setReportUser(String reportUser) {
			this.reportUser = reportUser;
		}

		public String getReportDate() {
			return reportDate;
		}

		public void setReportDate(String reportDate) {
			this.reportDate = reportDate;
		}
		
	}
	
	public void sendMail(ddnet.ejb.entities.Study study, Long userID, int asunto, String destinatario){
		EnvioCorreo correo = new EnvioCorreo();
		correo.setStudyID(study.getId());
		correo.setUserID(userID);
		correo.setSubject(asunto);
		correo.setReceiver(destinatario);
		correo.setDateRecord(DATE_LOG_FORMAT.format(new Date()));
		correomanager.persist(correo);
	}
	
	
	
	/**
	 * 	GENERAR PDF Informe
	 */
	
	// Obtener la ruta hacia almacenamiento de Informes
	private String getStudyReportPath(ddnet.ejb.entities.Study study){
		File savedReportsPath= new File(configurationManager.getConfigurationItem("report.saved-reports.path").getValue());
		if (study.getLegacyStudy().getDate() != null) { 
			savedReportsPath= new File(savedReportsPath, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
			if (!savedReportsPath.exists()) savedReportsPath.mkdir();
		}
		savedReportsPath= new File(savedReportsPath, study.getLegacyStudy().getStudyID()+".pdf");
		return savedReportsPath.getAbsolutePath();
	}
	
	// Encabezado y Pie de página
	private class HeaderFooter extends PdfPageEventHelper {
		
		public HeaderFooter() {}
		public void onEndPage(PdfWriter writer, Document document) {

			try{
	        	PdfPTable table; Image foto; PdfPCell cell;

	        // Encabezado de Página
	        	table= new PdfPTable(1);
	        	table.setTotalWidth(590);
	        	foto= Image.getInstance(getInstitutionalLogo());
	        	foto.scaleAbsolute(590f, 70f);
	        	cell= new PdfPCell(foto);
	        	cell.setBorder(Rectangle.NO_BORDER);
	        	table.addCell(cell);

	        	table.writeSelectedRows(0, -1, document.left(), 830, writer.getDirectContent());
	        // Encabezado de Página

	        // Pie de Página
	        	Chunk c= new Chunk(String.format("página %d",writer.getPageNumber()), new Font(Font.FontFamily.COURIER,8,Font.ITALIC,BaseColor.BLACK));            
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(c), document.right(50), 25, 0);
				/*
					table= new PdfPTable(1);
					table.setTotalWidth(575);
					foto= Image.getInstance("resources/pie.jpg");
					foto.scaleAbsolute(575f, 20f);
					cell= new PdfPCell(foto);
					cell.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell);
	
					table.writeSelectedRows(0, -1, document.left(), 25, writer.getDirectContent());
				*/
			// Pie de Página

        	} catch(Exception e){e.printStackTrace();}
        }
		private String getInstitutionalLogo(){ return configurationManager.getConfigurationItem("report.logos.source-path").getValue()+"/institucional.jpg"; }
	}
	
	public void generateStudyPDF(ddnet.ejb.entities.Study study, User user, String reportBody) 
		throws DocumentException, IOException	{
		
		HeaderFooter event= new HeaderFooter();
		LegacyStudy ls= study.getLegacyStudy();
		LegacyPatient lp= ls.getLegacyPatient();
		
		Document document= new Document(PageSize.A4, 10, 10, 85, 40);
		document.addAuthor("Diagnóstico Digital");
		document.addTitle("Informe");
		
		File archivoPDF= new File(getStudyReportPath(study));
		if(archivoPDF.exists()) archivoPDF.delete();
		archivoPDF.createNewFile();
		FileOutputStream archivo= new FileOutputStream(archivoPDF);
		PdfWriter writer= PdfWriter.getInstance(document, archivo);
		Rectangle rct= new Rectangle(35, 35, 559, 788);
		
		writer.setInitialLeading(18);
		writer.setBoxSize("art", rct);
		writer.setPageEvent(event);
		
		document.open();
		
//			BaseColor backgroundColor= new BaseColor(new Color(230,230,230));
//		    BaseColor theadColor= new BaseColor(new Color(210,210,210));
	    PdfPCell cell;

		PdfPTable table= new PdfPTable(4);
		table.setWidthPercentage(95);
		table.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		table.setWidths(new float[]{25,50,15,10});
        table.setSplitLate(false);
        
     // th "PatientID" + "PatientName"
		cell= new PdfPCell(new Paragraph(new Chunk("PatientID",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		table.addCell(cell);
		
		cell= new PdfPCell(new Paragraph(new Chunk("PACIENTE",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(3);
		table.addCell(cell);
	// th "PatientID" + "PatientName"
		
	// data "PatientID" + "PatientName"
		String patId= lp.getPatientID()!=null && !lp.getPatientID().isEmpty()? lp.getPatientID():"---";
		
		cell= new PdfPCell(new Paragraph(new Chunk(patId,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		table.addCell(cell);
		
		String patName= lp.getName()!=null && !lp.getName().isEmpty()? 
							lp.getName().replace("^^^^","").replace("^^^","").replace("^^","").replace("^"," "):"---";
		
		cell= new PdfPCell(new Paragraph(new Chunk(patName,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(3);
		table.addCell(cell);
	// data "PatientID" + "PatientName"
		
	// th "StudyDate" + "StudyDesc" + "StudyMod"
		cell= new PdfPCell(new Paragraph(new Chunk("Fecha",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		table.addCell(cell);
	
		cell= new PdfPCell(new Paragraph(new Chunk("Estudio",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(2);
		table.addCell(cell);
	
		cell= new PdfPCell(new Paragraph(new Chunk("Mod",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		table.addCell(cell);
	// th "StudyDate" + "StudyDesc" + "StudyMod"
		
	// data "StudyDate" + "StudyDesc" + "StudyMod"
		String studyDate= ls.getDate()!=null? DATE_FORMAT.format(ls.getDate()):"---";
		
		cell= new PdfPCell(new Paragraph(new Chunk(studyDate,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		table.addCell(cell);
		
		String studyDesc= ls.getDescription()!=null && !ls.getDescription().isEmpty()? ls.getDescription().replace("^"," "):"---";
		
		cell= new PdfPCell(new Paragraph(new Chunk(studyDesc,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(2);
		table.addCell(cell);
		
		String studyMod= ls.getModalities()!=null && !ls.getModalities().isEmpty()? ls.getModalities():"---";
		
		cell= new PdfPCell(new Paragraph(new Chunk(studyMod,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		table.addCell(cell);
	// data "StudyDate" + "StudyDesc" + "StudyMod"
		
	// th "StudyReport"
		cell= new PdfPCell(new Paragraph(new Chunk("INFORME",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(22);
		cell.setColspan(4);
		table.addCell(cell);
	// th "StudyReport"
	
	// data	"StudyReport"
		cell= new PdfPCell(new Paragraph(new Chunk("\n"+reportBody+"\n",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_LEFT);
		cell.setColspan(4);
		table.addCell(cell);
	// data	"StudyReport"
		
		
		
	// th "StudyReportPhysician" + "StudyReportPhysicianMatr"
		cell= new PdfPCell(new Paragraph(new Chunk("Informe realizado por:",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(2);
		table.addCell(cell);
		
		cell= new PdfPCell(new Paragraph(new Chunk("Matrícula",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
		//cell.setBackgroundColor(theadColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(2);
		table.addCell(cell);
	// th "StudyReportPhysician" + "StudyReportPhysicianMatr"
		
	// data "StudyReportPhysician" + "StudyReportPhysicianMatr"
		String physName= "Dr/a. "+ user.getLastName() +", "+ user.getFirstName();
		
		cell= new PdfPCell(new Paragraph(new Chunk(physName,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(2);
		table.addCell(cell);
		
		String license= "---";
		if(user.getPerfil()!=null){
			UserProfile perfil= user.getPerfil();
			if(perfil.getMN()!=null && !perfil.getMN().isEmpty()) license= perfil.getMN();
			else if(perfil.getMP()!=null && !perfil.getMP().isEmpty()) license= perfil.getMP();
		}
		cell= new PdfPCell(new Paragraph(new Chunk(license,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
		//cell.setBackgroundColor(backgroundColor);
		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		cell.setFixedHeight(20);
		cell.setColspan(2);
		table.addCell(cell);
	// data "StudyReportPhysician" + "StudyReportPhysicianMatr"
	
		document.add(table);
		document.close();
	}
}
