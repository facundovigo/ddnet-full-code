package ddnet.web.api.rest;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

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
import ddnet.ejb.entities.ComprobarCaso;
import ddnet.ejb.entities.DatosClinicos;
import ddnet.ejb.entities.EnvioCorreo;
import ddnet.ejb.entities.Incidencia;
import ddnet.ejb.entities.Informe;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacySerie;
import ddnet.ejb.entities.SegundaLectura;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.StudyMedia;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("/studies")
public class Study {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat DATE_STUDY_LOG_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private static final FopFactory fopFactory = FopFactory.newInstance();
	private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private static final Executor saveReportsToFileExecutor = Executors.newFixedThreadPool(3);
	
	@EJB
	private StudyManagerLocal studyManager;
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private ConfigurationManager configurationManager;	
	@EJB
	private UserManager userManager;
	@EJB
	private DatosClinicosManager dcManager;
	@EJB
	private IncidenciaManager incidenciaManager;
	@EJB
	private StudyLogManager stdLogmanager;
	@EJB
	private InformeManager infomanager;
	@EJB
	private EnvioCorreoManager correomanager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<StudyDTO> getStudies(@Context HttpServletRequest request,
			@QueryParam("study-date-type") int studyDateType, 
			@QueryParam("study-date-between-from") String studyDateFrom,
			@QueryParam("study-date-between-to") String studyDateTo,
			@QueryParam("study-accessionnumber") String accessionNumber,
			@QueryParam("patient-data-id") String patientID,
			@QueryParam("patient-data-name") String patientName,
//..................................................................................................//
			@QueryParam("study-user") String medico,
			@QueryParam("urgente") int urgencia,
			@QueryParam("preferente") int preferente,
			@QueryParam("con-anexo") int archivo,
			@QueryParam("firmado") int firmado,
			@QueryParam("preinformado") int preinformado,
			@QueryParam("no-leido") int noleido,
			@QueryParam("incidencia") int incidencia,
			@QueryParam("new-study") int newStudy,
			@QueryParam("aux-filter") int auxFilter,
//..................................................................................................//
			@QueryParam("patient-data-dob") String patientDOB,
			@QueryParam("study-modality") String modality,
			@QueryParam("study-diagnostic-center") long institutionID,
			@QueryParam("study-report-status") int reportStatus) throws ParseException {
		User user = securityHelper.getUser(request.getSession());
		
		StudyReportStatus reportStatusFilter = StudyReportStatus.getByCode(reportStatus);
		if (reportStatusFilter == null)
			reportStatusFilter = StudyReportStatus.any; 
	
		StudySearchFilter filter = new StudySearchFilter
				(StudySearchDateType.getByCode(studyDateType), 
						getDateFromString(studyDateFrom), getDateFromString(studyDateTo), 
						accessionNumber, patientID, patientName,
						getDateFromString(patientDOB), modality, institutionID, reportStatusFilter,
						medico, urgencia, preferente, archivo, 
						firmado, preinformado, noleido,
						incidencia, newStudy, auxFilter);
		
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
		//User user = securityHelper.getUser(request.getSession());
		
		//return getFullStudy(studyManager.getStudy(userManager.getByLogin("admin"), studyID));  
		return getFullStudy(studyManager.getIndividualStudy(studyID));
	}	
	
	@Path("{study-id}/report/body")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
	public Response updateReportBody(@Context final HttpServletRequest request,
			@PathParam("study-id") String studyID,
			@FormParam("study-report-body") String reportBody, @FormParam("finished") boolean finished,
			@FormParam("study-report-multiple") int cant, @FormParam("check-case") int checkcase,
			@FormParam("study-report-is-teaching-file") String tf, 
			@FormParam("study-report-is-emergency") String em) {		
		
		final User user = securityHelper.getUser(request.getSession());
		
		User editor = user;
		
		final ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		boolean istf = tf!=null, isem = em!=null;	//si los checkboxes se enviaron genera un true o un false 
		boolean cc = checkcase == 1;	//si se activó el flag 'comprobar caso'
		
		if (!study.isReported()) {
			study.setReport(reportBody);
			study.setReported(finished);
			if(finished){
				
				saveReportFile(study, editor);
				
				//guardar los datos de informe en la BD
				
				if(study.getInforme() == null){	//si no existe el informe
					registroInforme(study, user, 2, cant, istf, cc, isem, DATE_LOG_FORMAT.format(new Date()), DATE_LOG_FORMAT.format(new Date()), cc ? 1:0);
				}
				
				else{	//sobreescribir si ya existe el informe
					study.getInforme().setUserLogin(user.getLogin());
					study.getInforme().setState(2);
					study.getInforme().setMult(cant);
					study.getInforme().settFile(istf);
					study.getInforme().setCheckCase(cc);
					study.getInforme().setEmergency(isem);
					study.getInforme().setLastChangeDate(DATE_LOG_FORMAT.format(new Date()));
					study.getInforme().setSignedDate(DATE_LOG_FORMAT.format(new Date()));
					if(cc) study.getInforme().setCheckState(1);
				}
				
				
				//registrar informe FIRMADO en el logger
				registroLog(study, user, "Firma de informe", "");
				
				//si el informe se marcó como EMERGENCIA MÉDICA, envía un mail a cada administrador
				if(isem) for(User u : userManager.getAdministratorUsers())
							if(u.getMedico() != null)
								sendMail(study, editor.getId(), 3, u.getMedico().getMailAddress());
				
			}
			
			
			else {
				//guardar los datos de informe en la BD
				if(study.getInforme() == null)	 //si no existe el informe
					registroInforme(study, user, 1, 1, false, false, false, DATE_LOG_FORMAT.format(new Date()), "", 0);
				
				else{	//sobreescribir si ya existe
					study.getInforme().setUserLogin(user.getLogin());
					study.getInforme().setLastChangeDate(DATE_LOG_FORMAT.format(new Date()));
				}
				
				//registrar informe GUARDADO en el logger
				registroLog(study, user, "Informe editado", "");
			}
			
			//comprobar el caso si ha sido pedido por el usuario
			if(cc) setCheckCase(user, studyID, request.getParameter("check-case-message"));
			
			return Response.ok().build();
			
		} else 
			return Response.notModified().build();
	}
	
	
	private void saveReportFile(final ddnet.ejb.entities.Study study, final User editor) {
		try  {
			saveReportsToFileExecutor.execute(new Runnable() {
				@Override
				public void run() {
					OutputStream outputFile = null;
					try {
						File savedReportsPath = new File(configurationManager.getConfigurationItem("report.saved-reports.path").getValue());
						if (study.getLegacyStudy().getDate() != null) { 
							savedReportsPath = new File(savedReportsPath, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
							if (!savedReportsPath.exists())
								savedReportsPath.mkdir();
						}
						
						outputFile = new FileOutputStream(new File(savedReportsPath, study.getLegacyStudy().getStudyID() + ".pdf"));
						writeStudyPDFTo(study, editor, outputFile);
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						if (outputFile != null) {
							try {
								outputFile.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								outputFile.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}			
			});
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	
	
	private void writeStudyPDFTo(ddnet.ejb.entities.Study study, User editor, OutputStream output) throws FOPException, IOException, TransformerException {
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, output);
        Transformer transformer = transformerFactory.newTransformer();
        String xslFO = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("study-report.fo"));        
        xslFO = xslFO.replaceAll("\\$\\{PATIENT_DATA\\}", Matcher.quoteReplacement(getPatientData(study)));
        xslFO = xslFO.replaceAll("\\$\\{REPORT_BODY\\}", Matcher.quoteReplacement(study.getReport()));
        xslFO = xslFO.replaceAll("\\$\\{INSTITUTION_LOGO_FILE\\}", Matcher.quoteReplacement(getInstitutionLogoFile(study)));
        xslFO = xslFO.replaceAll("\\$\\{SIGNATURE_FILE\\}", Matcher.quoteReplacement(getSignatureFile(editor)));
        xslFO = xslFO.replaceAll("\\$\\{DR_DATA\\}", Matcher.quoteReplacement(getDrData(editor)));
        transformer.transform(new StreamSource(new StringReader(xslFO)), new SAXResult(fop.getDefaultHandler()));
	}
	
	private String getInstitutionLogoFile(ddnet.ejb.entities.Study study) {
		try {
			String logosFolder = configurationManager
					.getConfigurationItem("report.logos.source-path").getValue();			
			return FileUtils.getFile(logosFolder, "INST" + study.getInstitution().getId() + ".jpg").getCanonicalPath();
		} catch (Throwable t) {
			return "";
		}
	}

	/*private String getInstitutionFooterFile(ddnet.ejb.entities.Study study) {
		try {
			String logosFolder = configurationManager
					.getConfigurationItem("report.logos.source-path").getValue();			
			return FileUtils.getFile(logosFolder, "INST_FOOTER" + study.getInstitution().getId() + ".jpg").getCanonicalPath();
		} catch (Throwable t) {
			return "";
		}
	}*/

	private String getSignatureFile(User user) {
		try {
			String signaturesFolder = configurationManager
					.getConfigurationItem("report.signatures.source-path").getValue();			
			return FileUtils.getFile(signaturesFolder, user.getLogin() + ".jpg").getCanonicalPath();
		} catch (Throwable t) {
			return "";
		}
	}
	
	private String getPatientData(ddnet.ejb.entities.Study study) {
		
		String data = "";
		
		if(study.getLegacyStudy().getLegacyPatient().getName() != null && 
				!study.getLegacyStudy().getLegacyPatient().getName().equals("")) 
			
			data += "Paciente: " + study.getLegacyStudy().getLegacyPatient().getName()
						.replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " ") + "\n\n";
		
		if(study.getLegacyStudy().getLegacyPatient().getPatientID() != null && 
				!study.getLegacyStudy().getLegacyPatient().getPatientID().equals("")) 
			
			data += "Identificación Paciente: " + study.getLegacyStudy().getLegacyPatient().getPatientID() + "\n\n";
		
		if(study.getLegacyStudy().getLegacyPatient().getCalculatedAge() != null && 
				!study.getLegacyStudy().getLegacyPatient().getCalculatedAge().equals(""))
			
			data += "Edad: " + study.getLegacyStudy().getLegacyPatient().getCalculatedAge() + "\n\n";
		
		if(study.getLegacyStudy().getDescription() != null && 
				!study.getLegacyStudy().getDescription().equals(""))
			
			data+= "Estudio: " + study.getLegacyStudy().getDescription() + "\n\n";
		
		if(study.getLegacyStudy().getFormattedDate() != null && 
				!study.getLegacyStudy().getFormattedDate().equals(""))
			
			data += "Fecha del Estudio: " + study.getLegacyStudy().getFormattedDate() + "\n\n";
			
		data += "\n\n";
		
		return data;
	}
	
	private String getDrData(User user) {
		
		String data = "\n\n";
		
		data += "Estudio realizado y firmado por el doctor(a) " + user.getFirstName() + " " + user.getLastName();
		
		if(user.getMedico() != null)
			data += ", " + user.getMedico().getMatricula();
			
		return data;
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

	
	@Path("{study-id}/notes")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
	public Response updateNotes(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID,
			@FormParam("notes") String notes) {		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);		
		study.setNotes(notes);
		return Response.ok().build();
	}

	@Path("{study-id}/notes")
	@GET
	@Produces(MediaType.TEXT_PLAIN + "; charset=UTF-8")
	public String getNotes(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID) {		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		return study.getNotes();
	}		
	
	@Path("{study-id}/files")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<String> getFiles(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID) {		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		if (study == null)
			return Collections.<String>emptyList();
		
		Collection<String> files = getStudyFiles(study);		
		return files;
	}

	private Collection<String> getStudyFiles(ddnet.ejb.entities.Study study) {
		Collection<String> files = new ArrayList<String>();
		File studyUploadedFilesDirectory = getStudyUploadDirectory(study);
		for(File filename : studyUploadedFilesDirectory.listFiles((FileFilter)FileFileFilter.FILE))
			files.add(filename.getName());
		return files;
	}

	@Path("{study-id}/files/file")
	@GET
	public Response getFile(@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("study-id") String studyID, 
			@QueryParam("name") String filename) throws IOException {		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		if (study == null)
			return Response.status(Status.NOT_FOUND).build();
		
		File rootDirectory = getStudyUploadDirectory(study);
		File requestedFile = new File(rootDirectory, filename);
		if (requestedFile == null || !requestedFile.exists())
			return Response.status(Status.NOT_FOUND).build();
		
		if (!requestedFile.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath()))
			return Response.status(Status.NOT_FOUND).build();

		final String fileMimeType = getFileMimeType(requestedFile);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", requestedFile.getName()));
		
		//registro del evento en el LOG
		registroLog(study, user, "Abrir anexo", requestedFile.getName());
		
		return Response.ok(requestedFile)
				.header("Content-Type", fileMimeType)
				.build();
	}

	private String getFileMimeType(File file) {
		try {
			TikaConfig config = TikaConfig.getDefaultConfig();
			Detector detector = config.getDetector();
			TikaInputStream stream = TikaInputStream.get(file);
			Metadata metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY,FilenameUtils.getBaseName(file.getCanonicalPath()));
			org.apache.tika.mime.MediaType mediaType = detector.detect(stream, metadata);
			return mediaType.toString();
		}
		catch(Throwable t) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}
	
	@Path("{study-id}/files/file")
	@DELETE
	public Response deleteFile(@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("study-id") String studyID, 
			@QueryParam("name") String filename) throws IOException {		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		if (study == null)
			return Response.status(Status.NOT_FOUND).build();
		
		File rootDirectory = getStudyUploadDirectory(study);
		File requestedFile = new File(rootDirectory, filename);
		if (requestedFile == null || !requestedFile.exists())
			return Response.status(Status.NOT_FOUND).build();
		
		if (!requestedFile.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath()))
			return Response.status(Status.NOT_ACCEPTABLE).build();

		requestedFile.delete();
		
		//registro del evento en el LOG
		registroLog(study, user, "Eliminación de anexo", requestedFile.getName());
		
		return Response.ok().build();
	}
	
	
	
	
	
	
	
	@Path("{study-id}/attachments/{has-file}")
	@POST
	public Response setAttachments(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID,
			@PathParam("has-file") String hasFiles) throws IOException {
		
		User user = securityHelper.getUser(request.getSession());		
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		boolean flag;
		
		if (study == null)
			return Response.status(Status.BAD_REQUEST).build();
		
		if(hasFiles.equals("true")) flag = true;
		else flag = false;
		
		study.setHasArchive(flag);
				
		 
		return Response.ok().build();
	}
	
	@Path("{study-id}/datosclinicos")
	@GET
	public Collection<DatosClinicosDTO> getDC(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID) {
		
		//User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getIndividualStudy(studyID);
		
		if (study.getDatosclinicos() == null)
			return Collections.<DatosClinicosDTO>emptyList();
		
		else{
		Collection<DatosClinicosDTO> DC = new ArrayList<Study.DatosClinicosDTO>();
		
		DC.add(new DatosClinicosDTO(study.getDatosclinicos().getPriority(),
									study.getDatosclinicos().isOral(),
									study.getDatosclinicos().isEv(),
									study.getDatosclinicos().getNotes()));
		
		return DC;
		}
	}
	
	
	@Path("{study-id}/datosclinicos")
	@POST
	public void setDC(@Context HttpServletRequest request,
						@PathParam("study-id") String studyID){
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		int priority = Integer.parseInt(request.getParameter("study-clinical-data-urgency-h"));
		
		String cte_ev = request.getParameter("cte_ev");
		int ev = Integer.parseInt(cte_ev);
		
		String cte_oral = request.getParameter("cte_oral");
		int oral = Integer.parseInt(cte_oral);
		
		String notes = request.getParameter("study-clinical-data-other"); 
		
		if(study.getDatosclinicos() != null){
			DatosClinicos data = study.getDatosclinicos();
			
			data.setPriority(priority);
			data.setEv(ev==1);
			data.setOral(oral==1);
			data.setNotes(notes);
		}
		else{
			DatosClinicos data = new DatosClinicos();
		
			data.setStudyID(study.getId());
			data.setPriority(priority);
			data.setEv(ev==1);
			data.setOral(oral==1);
			data.setNotes(notes); 
		
			dcManager.persist(data);
		}
		
		//registro del evento en el LOG
		
		//generar el parámetro
		String param = "";
		
		param += priority == 0 ? "NORMAL": 
				 priority == 1 ? "PREFERENTE" :
				 "URGENTE";
		if(ev==1) param += ", EV";
		if(oral==1) param += ", ORAL";
		//generar el parámetro
		
		registroLog(study, user, "Petición de datos clínicos", param);
		
	}
	
	@Path("{study-id}/incidences")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<IncidenciasDTO> getIncidences( @Context HttpServletRequest request,
											   @PathParam("study-id") String studyID) {
		
		List<IncidenciasDTO> incidences = new ArrayList<IncidenciasDTO>();
		
		//User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getIndividualStudy(studyID);
		
		for(ddnet.ejb.entities.Incidencia incidencia : incidenciaManager.getAll(study.getId()))
			incidences.add(new IncidenciasDTO(study.getState(), incidencia.getIncidencia(), incidencia.getMensaje(), 
											  incidencia.getInc_date(), incidencia.getMsj_date(), incidencia.getUser())); 
		
		return incidences;
	}
	
	
	@Path("{study-id}/newincidence")
	@POST
	public void setNewIncidence(@Context HttpServletRequest request,
								@PathParam("study-id") String studyID) {

		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		Incidencia data = new Incidencia();
		
		int estado = Integer.parseInt(request.getParameter("has-incidence"));
		String mensaje = request.getParameter("study-message-or-incident");
		String fecha = request.getParameter("study-message-incident-resolution-date");
		
		String texto = "", param = "";
		
		if( estado == 1) {
			
			texto = "*** SE HA REGISTRADO UNA NUEVA INCIDENCIA ***\n\n\t";
			data.setStudyID(study.getId());
			data.setIncidencia(2);
			data.setMensaje(texto + mensaje);
			data.setInc_date(fecha);
			data.setMsj_date(fecha);
			data.setUser(user.getLogin()); 
			study.setState(2);
			param += "Nueva incidencia";
		}
		else {
			
			texto = "*** SE HA RESUELTO LA INCIDENCIA ***\n\n\t";
			data.setStudyID(study.getId());
			data.setIncidencia(1);
			data.setMensaje(texto + mensaje);
			data.setInc_date(fecha);
			data.setMsj_date(fecha);
			data.setUser(user.getLogin()); 
			study.setState(1);
			param += "Incidencia resuelta";
		}
		
		incidenciaManager.persist(data); 
		
		//registro del evento en el LOG
		registroLog(study, user, "Nuevo mensaje", param);
	}
	
	@Path("{study-id}/newmessage")
	@POST
	public void setNewMessage(@Context HttpServletRequest request,
								@PathParam("study-id") String studyID) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		Incidencia data = new Incidencia();
		
		String mensaje = request.getParameter("study-message-or-incident");
		String fecha = request.getParameter("message-date");
		
		data.setStudyID(study.getId()); 
		data.setIncidencia(study.getState()); 
		data.setMensaje(mensaje);
		data.setInc_date(fecha);
		data.setMsj_date(fecha);
		data.setUser(user.getLogin()); 
		
		incidenciaManager.persist(data); 
		
		//registro del evento en el LOG
		registroLog(study, user, "Nuevo mensaje", "");
	}
	
	@Path("{study-id}/assign-studies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getMedicoAsignado(@Context HttpServletRequest request,
									@PathParam("study-id") String studyID) {
		
		//User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getIndividualStudy(studyID);
		
		if(study.getUstudy() == null) return null;
		
		else{
			
			return study.getUstudy().getUser();
		}
	}
	
	@Path("reassign-studies")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public void changeMedicoAsignado(@Context HttpServletRequest request,			
			@FormParam("physicianCode") String physicianCode, @FormParam("studiesIDs[]") String[] studiesIDs) {
		
		User user = securityHelper.getUser(request.getSession());
		User physician = userManager.getByLogin(physicianCode);
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studiesIDs[0]);
		
		User last_physician = study.getUstudy().getUser();
		
		study.getUstudy().setUser(physician);
		
		//registro del evento en el LOG
		registroLog(study, user, "Reasignación de estudio", physician.getLastName() + ", " + physician.getFirstName());
		
		//manda el mail si el usuario tiene asignado un correo
		if(physician.getMedico() != null)
		sendMail(study, physician.getId(), 1, physician.getMedico().getMailAddress());
		if(last_physician.getMedico() != null)
		sendMail(study, last_physician.getId(), 2, last_physician.getMedico().getMailAddress());
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
	
	@Path("{study-id}/simpleView")
	@POST
	public void registroVisualizacionFull(	@Context HttpServletRequest request,
											@PathParam("study-id") String studyID) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		registroLog(study, user, "Visualización estudio", "WADO");
	}
	
	@Path("{study-id}/simpleView/{description}")
	@POST
	public void registroVisualizacionSerie(	@Context HttpServletRequest request,
											@PathParam("study-id") String studyID,
											@PathParam("description") String desc) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		registroLog(study, user, "Visualización serie", desc + " - WADO");
	}
	
	@Path("{study-id}/downloadSerie/{calidad}")
	@POST
	public void registroDescargaSerie(	@Context HttpServletRequest request,
										@PathParam("study-id") String studyID,
										@PathParam("calidad") String quality) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		study.setImg(true);
		
		registroLog(study, user, "Descarga Series", quality);
	}
	
	@Path("{study-id}/downloadStudy/{calidad}")
	@POST
	public void registroDescargaEstudio(@Context HttpServletRequest request,
										@PathParam("study-id") String studyID,
										@PathParam("calidad") String quality) {
		
		User user = securityHelper.getUser(request.getSession());
		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		study.setImg(true);
		
		registroLog(study, user, "Descarga Estudio", quality);
	}
	
	@POST
	@Path("{study-id}/files")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadStudyFile(@Context HttpServletRequest request,
			@PathParam("study-id") String studyID) throws IOException {
		User user = securityHelper.getUser(request.getSession());		
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		if (study == null)
			return Response.status(Status.BAD_REQUEST).build();
		
		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> parseRequest = uploader.parseRequest(request);
			for (FileItem fileItem : parseRequest) {
				if ("study-file".equals(fileItem.getFieldName())) {
					String uploadedFilename = new File(getStudyUploadDirectory(study), 
							fileItem.getName()).getAbsolutePath();
					writeToFile(uploadedFilename, fileItem.get());					
					
					//registro del evento en el LOG
					registroLog(study, user, "Anexo de archivo", fileItem.getName());
					break;
				}
			}
		} catch (FileUploadException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		
		return Response.ok().build();
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

	@Path("assign-studies")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public int assignStudies(@Context HttpServletRequest request,			
			@FormParam("physicianCode") String physicianCode, @FormParam("studiesIDs[]") String[] studiesIDs) {
				
		User requestingUser = securityHelper.getUser(request.getSession());		
		User physician = userManager.getByLogin(physicianCode);
		
		//antes de registrar en el LOG quiero saber si se asignaron los estudios
		int asignados = studyManager.assignStudies(requestingUser, physician, studiesIDs);
		
		//registro del evento en el LOG
		if(asignados > 0)
		for(String s : studiesIDs){
		registroLog(studyManager.getIndividualStudy(s), requestingUser, "Asignación de estudio", physician.getLastName() + ", " + physician.getFirstName());
		
		//avisar por mail si el usuario tiene asignado un correo
		if(physician.getMedico() != null)
		sendMail(studyManager.getIndividualStudy(s), physician.getId(), 1, physician.getMedico().getMailAddress());}
		
		// Retornamos la cantidad de estudios efectivamente asignados.
		return asignados;
		
	}
	
	private void writeToFile(String filename, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(filename));
			out.write(data);
		}
		finally {
			if (out != null)
			{
				try {
					out.flush();
					out.close();
				} catch(Throwable t) {}
			}
		}
	}	
	
	private File getStudyUploadDirectory(ddnet.ejb.entities.Study study) {
		File rootDirectory = getStudyUploadedFilesRootDirectory();
		File studyFileDateFolder = new File(rootDirectory, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
		if (!studyFileDateFolder.exists()) studyFileDateFolder.mkdirs();
		File studyFilesFolder = new File(studyFileDateFolder, study.getLegacyStudy().getStudyID());
		if (!studyFilesFolder.exists()) studyFilesFolder.mkdirs();
		return studyFilesFolder;
	}

	private File getStudyUploadedFilesRootDirectory() {
		return new File(configurationManager.getConfigurationItem("study.uploaded-files.path").getValue());
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
	
	public void registroInforme(ddnet.ejb.entities.Study study, User user, int estado, int cant,
								boolean tf, boolean cc, boolean em,
								String fecha_mod, String fecha_firma, int state) {
		
		Informe report = new Informe();
		
		report.setStudyID(study.getId());
		report.setUserLogin(user.getLogin());
		report.setState(estado);
		report.setMult(cant);
		if(tf) report.settFile(true);
		if(cc) report.setCheckCase(true);
		if(em) report.setEmergency(true);
		report.setLastChangeDate(fecha_mod);
		report.setSignedDate(fecha_firma);
		report.setCheckState(state);
		
		infomanager.persist(report);
	}
	
	@Path("{study-id}/secReading")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void setSecReading(	@Context HttpServletRequest request,
								@PathParam("study-id") String studyID,
								@FormParam("type-of-discrepance") int grado,
								@FormParam("txt_second-reading") String mensaje) {
		
		User user = securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		if (study.getInforme() != null){
			
			//generar nuevo registro en la BD
			SegundaLectura sl = new SegundaLectura();
			sl.setInformeID(study.getInforme().getId());
			sl.setInformante(study.getInforme().getUserLogin());
			sl.setGrado(grado);
			sl.setDiscrepante(user.getLogin());
			sl.setMsj(mensaje);
			sl.setDate(DATE_LOG_FORMAT.format(new Date()));
			infomanager.persist(sl);
			
			//modificar dato de informe
			study.getInforme().setSecondReading(true);
			
			//registro del evento en el LOG
			String param;
			if(grado == 0) param = "Sin Discrepancias"; 
			else param = "Discrepancia Grado " + grado;
			registroLog(study, user, "Segunda Lectura", param);
		}
	}
	
	@Path("{study-id}/checkCase")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<ComprobarCasoDTO> getCC(	@Context HttpServletRequest request,
												@PathParam("study-id") String studyID) throws ParseException{
		
		ddnet.ejb.entities.Study study = studyManager.getIndividualStudy(studyID);
		Collection<ComprobarCasoDTO> checkcases = new ArrayList<ComprobarCasoDTO>();
		
		if(study.getInforme() != null && study.getInforme().isCheckCase()){
			
			for(ComprobarCaso cc : infomanager.getCCbyReport(study.getInforme().getId()))
				checkcases.add(new ComprobarCasoDTO( 	DATE_STUDY_LOG_FORMAT.format(DATE_LOG_FORMAT.parse(cc.getDate())), 
														cc.getUser(), 
														cc.getState(), 
														cc.getNotes()));
		}
		
		return checkcases;
	}
	
	@Path("{study-id}/checkCase")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void setCheckCase(	@Context HttpServletRequest request,
								@PathParam("study-id") String studyID,
								@FormParam("check-case-val") int value,
								@FormParam("txt_check-case") String notes){
		
		User user = securityHelper.getUser(request.getSession());
		ddnet.ejb.entities.Study study = studyManager.getStudy(user, studyID);
		
		if(study.getInforme() != null){
			
			study.getInforme().setCheckState(value);
			
		ComprobarCaso cc = new ComprobarCaso();
			cc.setInformeID(study.getInforme().getId());
			cc.setDate(DATE_LOG_FORMAT.format(new Date()));
			cc.setUser(user.getLogin());
			cc.setState(value);
			cc.setNotes(notes);
		infomanager.persist(cc);
		
		//registro de eventos en el logger
		String param = value == 1 ? "Comprobar" : value == 2 ? "Solicitado" : "Comprobado" ;
		registroLog(study, user, "Comprobar Caso", param);
		
		}
	}
	
	//generar el "Comprobar Caso" ante un nuevo informe
	public void setCheckCase(User user, String studyID, String msg){
		
		ddnet.ejb.entities.Study study = studyManager.getIndividualStudy(studyID);
		
		for(Informe report : infomanager.getInformebyStudy(study.getId())){
		
		report.setCheckState(1);
			
		ComprobarCaso check = new ComprobarCaso();
		
		check.setInformeID(report.getId());
		check.setDate(DATE_LOG_FORMAT.format(new Date()));
		check.setUser(user.getLogin());
		check.setState(1);
		check.setNotes(msg);
		
		infomanager.persist(check);
		
		//registrar en el logger si se pidió comprobar el caso
		if(report != null && report.isCheckCase()) registroLog(study, user, "Comprobar Caso", "Comprobar");
		}
	}
	

	private StudyDTO flatten(ddnet.ejb.entities.Study study) {
		return flatten(Collections.singletonList(study)).iterator().next();
	}
	
	private Collection<StudyDTO> flatten(Collection<ddnet.ejb.entities.Study> studies) {
		Collection<StudyDTO> result = new ArrayList<StudyDTO>();
		File uploadsRoot = getStudyUploadedFilesRootDirectory();
		
		StudyDTO dto = null;
		for(ddnet.ejb.entities.Study study : studies) {
			dto = new StudyDTO();
			
			File studyFileDateFolder = new File(uploadsRoot, DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate()));
			
			try { dto.setId(study.getLegacyStudy().getStudyID()); } catch (Throwable t) {}
			try { dto.setPID(study.getLegacyStudy().getLegacyPatient().getPatientID()); } catch (Throwable t) {}
			try { dto.setPN(study.getLegacyStudy().getLegacyPatient().getName()
						.replace("^^^^", "")
						.replace("^^^", "")
						.replace("^^", "")
						.replace("^", " ")); } catch (Throwable t) {}
			try { dto.setAge(study.getLegacyStudy().getLegacyPatient().getCalculatedAge()); } catch (Throwable t) {}
			try { dto.setDate(study.getLegacyStudy().getFormattedDate()); } catch (Throwable t) {}
			try { dto.setMod(study.getLegacyStudy().getModalities()); } catch (Throwable t) {}
			try { dto.setDesc(study.getLegacyStudy().getDescription()); } catch (Throwable t) {}
			try { dto.setRS(study.getReportStatus()); } catch (Throwable t) {}
			try { dto.setFC(new File(studyFileDateFolder, study.getLegacyStudy().getStudyID()).list().length); } catch (Throwable t) {}
			try { dto.setInc(study.getState()); } catch (Throwable t) {}
			try { dto.setDr(study.getUstudy().getMedico().getName());} catch (Throwable t) {}
			try { dto.setUrg(study.getDatosclinicos().getPriority());} catch (Throwable t) {}
			try { dto.setIID(study.getInstitution().getId()); } catch (Throwable t) {}
			try { dto.setTI(study.getLegacyStudy().getTotalSeries()+" - "+study.getLegacyStudy().getTotalInstances()); } catch (Throwable t) {} 
			try { dto.setC(study.getInstitution().getName()); } catch (Throwable t) {}
			try { dto.setAN(study.getLegacyStudy().getAccessionNumber()); } catch (Throwable t) {}
			if(study.getInforme() != null){
				try { dto.setCc(study.getInforme().isCheckCase()); } catch (Throwable t) {}
				try { dto.setTf(study.getInforme().istFile()); } catch (Throwable t) {}
				try { dto.setEm(study.getInforme().isEmergency()); } catch (Throwable t) {}
			}

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
		dto.setReportBody(study.getReport());
		dto.setFirmado( study.isReported() ); 
		
		for(String file : getStudyFiles(study))
			dto.getAttachments().add(new AttachmentDTO(file));
		for(LegacySerie serie : study.getLegacyStudy().getLegacySeries())
			dto.getSeries().add(new SerieDTO(serie.getPerformedOn(), serie.getDescription(), serie.getNumber(), 
					serie.getLegacyInstances().size(), false, false, serie.getSerieID())); // TODO: faltan flags "lossy/lossless".
		dto.setInstitutionID(study.getInstitution().getId());
		dto.setInstitutionName(study.getInstitution().getName());
		
		// TODO: falta ver de donde sacar estos datos de un estudio.
		dto.setOralContrast(false);
		dto.setEndovenousContrast(false);
		dto.setOtherClinicalData("");
		dto.setUrgency("Sin prioridad");
		dto.setAssignedTo("");
		dto.setProblemMessage("");
		dto.setProblemIsIncident(false);
		dto.setProblemResolutionTime(null);
		if(study.getInforme() != null){
			dto.setRegionCount(study.getInforme().getMult());
			dto.setTeachingFile(study.getInforme().istFile());
			dto.setEmergency(study.getInforme().isEmergency());
			dto.setSecondReading(study.getInforme().isSecondReading());
			if(study.getInforme().isSecondReading()) dto.setDiscrep(study.getInforme().getSegundaLectura().getGrado());
			else dto.setDiscrep(-1);
			dto.setValidateReport(study.getInforme().isCheckCase());
			if(study.getInforme().isCheckCase()) dto.setValidateReportState(study.getInforme().getCheckState());
		}
		
		dto.setLogger(stdLogmanager.getStudyLogbyId(study.getId()).isEmpty());
		
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
		private String desc = "?";
		/**
		 * Study's report status.
		 */
		private int rs = 0;
		/**
		 * # of uploaded files for this study. 
		 */
		private int fc = 0;
		
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
		private String ti = "?";					

		/**
		 * Center: Centro de diagnóstico.
		 */
		private String c;

		/**
		 * Accession Number (ID ESTUDIO)
		 */
		private String an;
		
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
			this.desc = desc != null ? desc : "?";
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
		public String getTI() {			
			return ti;
		}
		public void setTI(String ti) {		
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
			this.an = accessionNumber;
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
		private boolean oralContrast;
		private boolean endovenousContrast;
		private String otherClinicalData;
		private String urgency;
		private String assignedTo;
		private String problemMessage;
		private boolean problemIsIncident;
		private Date problemResolutionTime;		
		private String reportBody;
		private boolean firmado;
		private int regionCount; // "Multiples" 
		private boolean teachingFile;
		private boolean emergency;
		private boolean validateReport;
		private int validateReportState;
		private boolean secondReading;
		private int discrep;
		private final Set<SerieDTO> series = new HashSet<SerieDTO>();
		private final Set<AttachmentDTO> attachments = new HashSet<AttachmentDTO>();
		private String accessionNumber = "";
		private long institutionID;
		private String institutionName;
		private boolean logger;
		
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
		
		public boolean isOralContrast() {
			return oralContrast;
		}

		public void setOralContrast(boolean oralContrast) {
			this.oralContrast = oralContrast;
		}
		
		public boolean isEndovenousContrast() {
			return endovenousContrast;
		}

		public void setEndovenousContrast(boolean endovenousContrast) {
			this.endovenousContrast = endovenousContrast;
		}
		
		public String getOtherClinicalData() {
			return otherClinicalData;
		}

		public void setOtherClinicalData(String otherClinicalData) {
			this.otherClinicalData = otherClinicalData;
		}

		public String getUrgency() {
			return urgency;
		}

		public void setUrgency(String urgency) {
			this.urgency = urgency;
		}
		
		public String getAssignedTo() {
			return assignedTo;
		}

		public void setAssignedTo(String assignedTo) {
			this.assignedTo = assignedTo;
		}

		public String getProblemMessage() {
			return problemMessage;
		}

		public void setProblemMessage(String problemMessage) {
			this.problemMessage = problemMessage;
		}

		public boolean getProblemIsIncident() {
			return problemIsIncident;
		}

		public void setProblemIsIncident(boolean problemIsIncident) {
			this.problemIsIncident = problemIsIncident;
		}

		public Date getProblemResolutionTime() {
			return problemResolutionTime;
		}

		public void setProblemResolutionTime(Date problemResolutionTime) {
			this.problemResolutionTime = problemResolutionTime;
		}
		
		public String getReportBody() {
			return reportBody;
		}

		public void setReportBody(String reportBody) {
			this.reportBody = reportBody;
		}
		
		public void setFirmado(boolean firmado) {
			this.firmado = firmado;
		}
		
		public boolean isFirmado() {
			
			return firmado;
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

		public Set<SerieDTO> getSeries() {
			return series;
		}

		public Set<AttachmentDTO> getAttachments() {
			return attachments;
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

		public boolean isLogger() {
			return logger;
		}

		public void setLogger(boolean logger) {
			this.logger = logger;
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

		public AttachmentDTO(String filename) {
			this.filename = filename;
		}
		
		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
	}
	
	public class DatosClinicosDTO {
		private int priority;
		private boolean isOral;
		private boolean isEv;
		private String notes; 
		
		public DatosClinicosDTO(int priority,boolean isOral,boolean isEv,String notes) {			
			
			this.priority = priority;
			this.isOral = isOral;
			this.isEv = isEv;
			this.notes = notes;
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public boolean isOral() {
			return isOral;
		}

		public void setOral(boolean isOral) {
			this.isOral = isOral;
		}

		public boolean isEv() {
			return isEv;
		}

		public void setEv(boolean isEv) {
			this.isEv = isEv;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
		
		
	}
	
	public class IncidenciasDTO {
		
		private int state;
		private int mstate;
		private String msj;
		private String idate;
		private String mdate;
		private String user;
		
		public IncidenciasDTO(int state, int mstate, String msj, String idate, String mdate, String user) {
			
			this.state = state;
			this.mstate = mstate;
			this.msj = msj;
			this.idate = idate;
			this.mdate = mdate;
			this.user = user;
		}

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public String getMsj() {
			return msj;
		}

		public void setMsj(String msj) {
			this.msj = msj;
		}

		public String getIdate() {
			return idate;
		}

		public void setIdate(String idate) {
			this.idate = idate;
		}

		public String getMdate() {
			return mdate;
		}

		public void setMdate(String mdate) {
			this.mdate = mdate;
		}

		public int getMstate() {
			return mstate;
		}

		public void setMstate(int mstate) {
			this.mstate = mstate;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
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
	
	public class ComprobarCasoDTO {
		
		private String date;
		private String user;
		private int state;
		private String notes;
		
		public ComprobarCasoDTO(String date, String user, int state, String notes) {			
			
			this.date = date;
			this.user = user;
			this.state = state;
			this.notes = notes;
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

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
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
}
