package ddap.web.api.rest;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.FOPException;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

import ddap.ejb.ConfigurationManager;
import ddap.ejb.PracticaPacienteManager;
import ddap.ejb.PracticaPacienteSearchFilter;
import ddap.ejb.PracticasManager;
import ddap.ejb.LoggerManager;
import ddap.ejb.UsuariosManager;
import ddap.ejb.PracticaPacienteSearchFilter.ppSearchDateType;
import ddap.ejb.WorklistManager;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.Paciente;
import ddap.ejb.entities.PracticaxPaciente;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/practicaxpaciente")
public class PracticeByPatient {
	
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_BD_FORMAT = new SimpleDateFormat("HHmmss");
	private static final SimpleDateFormat DATE_PRINT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_PRINT_FORMAT1 = new SimpleDateFormat("dd-MM-yyyy");
	private static final SimpleDateFormat TIME_PRINT_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat DATE_PARSE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@EJB private SecurityHelper securityHelper;
	@EJB private UsuariosManager userManager;
	@EJB private PracticasManager pManager;
	@EJB private PracticaPacienteManager ppManager;
	@EJB private LoggerManager logManager;
	@EJB private ConfigurationManager configurationManager;
	@EJB private WorklistManager wlManager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<PracticaPacienteDTO> getRequested(@Context HttpServletRequest request,
														@QueryParam("date-type") int dateType,
														@QueryParam("between-date-from") String betweenDateFrom,
														@QueryParam("between-date-to") String betweenDateTo,
														@QueryParam("practice-state") String practiceState,
														@QueryParam("accession-number") String accessionNumber,
														@QueryParam("patient-filter") String patientFilter,
														@QueryParam("pat-sex") String patSex) throws ParseException{
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		PracticaPacienteSearchFilter filter = 
				new PracticaPacienteSearchFilter( ppSearchDateType.getByCode(dateType), 
												  getDateFromString(betweenDateFrom), 
												  getDateFromString(betweenDateTo), 
												  practiceState, accessionNumber, 
												  patientFilter, patSex);
		Collection<PracticaxPaciente> all= ppManager.findPracticaPaciente(filter);
		Collection<PracticaPacienteDTO> result= new ArrayList<PracticaPacienteDTO>();
		
		if(all == null || all.isEmpty()) return null;
		
		for(PracticaxPaciente pp : all){
			result.add(
					new PracticaPacienteDTO(
						DATE_PRINT_FORMAT.format(DATE_FOLDER_FORMAT.parse(pp.getRegDate())),
						TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getRegTime())),
						pp.getPatient().getDocNumber(),
						pp.getPatient().getLastName(),
						pp.getPatient().getFirstName(),
						pp.getPractice().getNombre(),
						pp.isOutside() ? "Terminó" : pp.isCancelled() ? "Cancelado" : pp.isInside() ? "Ingresó" : "En Espera",
						pp.getCode(),
						pp.getOmPath() == null ? 0 : new File(pp.getOmPath()).listFiles().length,
						pp.getPatient().getPatSex(),
						pp.getPatient().getBirthDate(),
						pp.getPatient().getDocType(),
						pp.getInDate() == null ? "" : DATE_PRINT_FORMAT1.format(DATE_FOLDER_FORMAT.parse(pp.getInDate())),
						pp.getInTime() == null ? "" : TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getInTime())),
						pp.getOutDate() == null ? "" : DATE_PRINT_FORMAT1.format(DATE_FOLDER_FORMAT.parse(pp.getOutDate())),
						pp.getOutTime() == null ? "" : TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getOutTime())),
						pp.getCancelDate() == null ? "" : DATE_PRINT_FORMAT1.format(DATE_FOLDER_FORMAT.parse(pp.getCancelDate())),
						pp.getCancelTime() == null ? "" : TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getCancelTime())),
						wlManager.getByAccessionNumber(pp.getCode())!=null?
								wlManager.getByAccessionNumber(pp.getCode()).getRefPhysician():"---"
					)
			);
		}
		return result;
	}
	
	@Path("{code}")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public PracticaPacienteDTO getRequested(@Context HttpServletRequest request,
			   								@PathParam("code") String code) throws ParseException {
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		PracticaxPaciente pp = ppManager.getByCode(code);
		if(pp == null) return null;
		
		return new PracticaPacienteDTO(
				DATE_PRINT_FORMAT1.format(DATE_FOLDER_FORMAT.parse(pp.getRegDate())),
				TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getRegTime())),
				pp.getPatient().getDocNumber(),
				pp.getPatient().getLastName(),
				pp.getPatient().getFirstName(),
				pp.getPractice().getNombre(),
				pp.isOutside() ? "Terminó" : pp.isCancelled() ? "Cancelado" : pp.isInside() ? "Ingresó" : "En Espera",
				pp.getCode(),
				pp.getOmPath() == null ? 0 : new File(pp.getOmPath()).listFiles().length,
				pp.getPatient().getPatSex(),
				pp.getPatient().getBirthDate(),
				pp.getPatient().getDocType(),
				pp.getInDate() == null ? "" : DATE_PRINT_FORMAT1.format(DATE_FOLDER_FORMAT.parse(pp.getInDate())),
				pp.getInTime() == null ? "" : TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getInTime())),
				pp.getOutDate() == null ? "" : DATE_PRINT_FORMAT1.format(DATE_FOLDER_FORMAT.parse(pp.getOutDate())),
				pp.getOutTime() == null ? "" : TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getOutTime())),
				pp.getCancelDate() == null ? "" : DATE_PRINT_FORMAT1.format(DATE_FOLDER_FORMAT.parse(pp.getCancelDate())),
				pp.getCancelTime() == null ? "" : TIME_PRINT_FORMAT.format(TIME_BD_FORMAT.parse(pp.getCancelTime())),
				wlManager.getByAccessionNumber(code)!=null?
					wlManager.getByAccessionNumber(code).getRefPhysician():"---"
		);
	}
	
	private Date getDateFromString(String input) throws ParseException {
		return (input != null && !input.trim().equals("")) ? DATE_PARSE_FORMAT.parse(input) : null;
	}
	
	
	@Path("pdf/{code}")
	@GET
	@Produces("application/pdf")
	public Response getPracticePDF(@Context HttpServletRequest request,
								   @Context HttpServletResponse response, 
								   @PathParam("code") String code ) throws FOPException, IOException, TransformerException {
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		PracticaxPaciente pp = ppManager.getByCode(code);
		if ( pp == null )
			return Response.status(Status.NOT_FOUND).build();
		
		String fileDownloadedName = pp.getPatient().getLastName()+", "+pp.getPatient().getFirstName()+" ["+pp.getRegDate()+"]";
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.pdf\"", fileDownloadedName));
		response.setContentType("application/pdf");
		
		final OutputStream outputStream = response.getOutputStream();
		outputStream.write(FileUtils.readFileToByteArray(new File(pp.getPdfPath())));
        //response.setHeader("Content-Length", String.valueOf(1));
		return Response.ok().build();
	}
	
	
	@Path("om/{code}")
	@GET
	@Produces("application/jpeg")
	public Response getOrdenMedica(@Context HttpServletRequest request,
								   @Context HttpServletResponse response, 
								   @PathParam("code") String code,
								   @QueryParam("name") String fileName) throws FOPException, IOException, TransformerException {
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		PracticaxPaciente pp = ppManager.getByCode(code);
		if ( pp == null )
			return Response.status(Status.NOT_FOUND).build();
		
		File directoryFile = new File(pp.getOmPath());
		if (directoryFile == null || !directoryFile.exists())
			return Response.status(Status.NOT_FOUND).build();
		
		File requestedFile = fileName == null || fileName.equals("") ?
				directoryFile.listFiles()[0] : new File(directoryFile, fileName);
		
		if (requestedFile == null || !requestedFile.exists())
			return Response.status(Status.NOT_FOUND).build();
		
		final String fileMimeType = getFileMimeType(requestedFile);
		String fileDownloadedName = pp.getPatient().getLastName()+", "+pp.getPatient().getFirstName()+" ["+requestedFile.getName()+"]";
		response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", fileDownloadedName));
		
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
	
	@Path("om/multiple/{code}")
	@GET
	public List<String> getFilesNames(@Context HttpServletRequest request,
									  @PathParam("code") String code) {
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		PracticaxPaciente pp = ppManager.getByCode(code);
		if ( pp == null ) return null;
		List<String> names = new ArrayList<String>();
		
		File directoryFile = new File(pp.getOmPath());
		if (directoryFile == null || !directoryFile.exists()) return null;
		
		for(File f : directoryFile.listFiles()) names.add(f.getName());
		
		return names;
	}
	
	
	@Path("{code}/ingreso")
	@POST
	public Response setAsInside(@Context HttpServletRequest request,
			  					@PathParam("code") String code) {
		Usuario u = securityHelper.getUser(request.getSession());
		
		PracticaxPaciente pp = ppManager.getByCode(code);
		if(pp == null) return Response.status(Status.NOT_FOUND).build();
		
		pp.setInside(true);
		pp.setInDate(DATE_FOLDER_FORMAT.format(new Date()));
		pp.setInTime(TIME_BD_FORMAT.format(new Date()));
		
		Paciente p = pp.getPatient();
		recordInLog(u.getLogin(), "Ingresó Paciente", p.getLastName()+" "+p.getFirstName()+" "+p.getDocNumber());
		
		return Response.ok().build();
	}
	
	@Path("{code}/cancelado")
	@POST
	public Response setAsCancelled(	@Context HttpServletRequest request,
			  						@PathParam("code") String code) {
		Usuario u = securityHelper.getUser(request.getSession());
		
		PracticaxPaciente pp = ppManager.getByCode(code);
		if(pp == null) return Response.status(Status.NOT_FOUND).build();
		
		pp.setCancelled(true);
		pp.setCancelDate(DATE_FOLDER_FORMAT.format(new Date()));
		pp.setCancelTime(TIME_BD_FORMAT.format(new Date()));
		
		Paciente p = pp.getPatient();
		recordInLog(u.getLogin(), "Canceló Paciente", p.getLastName()+" "+p.getFirstName()+" "+p.getDocNumber());
		
		return Response.ok().build();
	}
	
	
	public class PracticaPacienteDTO{
		
		private String date;
		private String time;
		private String patID;
		private String lastName;
		private String firstName;
		private String pract;
		private String state;
		private String code;
		private int cantOM;
		private String sex;
		private String birthDate;
		private String docType;
		private String dateIN;
		private String timeIN;
		private String dateOUT;
		private String timeOUT;
		private String dateCNLD;
		private String timeCNLD;
		private String medDerivante;
		
		public PracticaPacienteDTO(String date, String time, String patID,
				String lastName, String firstName, String pract, String state,
				String code, int cantOM, String sex, String birthDate, String docType,
				String dateIN, String timeIN, String dateOUT, String timeOUT, 
				String dateCNLD, String timeCNLD, String medDerivante) {
			super();
			this.date = date;
			this.time = time;
			this.patID = patID;
			this.lastName = lastName;
			this.firstName = firstName;
			this.pract = pract;
			this.state = state;
			this.code = code;
			this.cantOM = cantOM;
			this.sex = sex;
			this.birthDate= birthDate;
			this.docType = docType;
			this.dateIN = dateIN;
			this.timeIN = timeIN;
			this.dateOUT = dateOUT;
			this.timeOUT = timeOUT;
			this.dateCNLD = dateCNLD;
			this.timeCNLD = timeCNLD;
			this.medDerivante= medDerivante;
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

		public String getPatID() {
			return patID;
		}

		public void setPatID(String patID) {
			this.patID = patID;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getPract() {
			return pract;
		}

		public void setPract(String pract) {
			this.pract = pract;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
		public int getCantOM() {
			return cantOM;
		}

		public void setCantOM(int cantOM) {
			this.cantOM = cantOM;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getBirthDate() {
			return birthDate;
		}

		public void setBirthDate(String birthDate) {
			this.birthDate = birthDate;
		}

		public String getDocType() {
			return docType;
		}

		public void setDocType(String docType) {
			this.docType = docType;
		}

		public String getDateIN() {
			return dateIN;
		}

		public void setDateIN(String dateIN) {
			this.dateIN = dateIN;
		}

		public String getTimeIN() {
			return timeIN;
		}

		public void setTimeIN(String timeIN) {
			this.timeIN = timeIN;
		}

		public String getDateOUT() {
			return dateOUT;
		}

		public void setDateOUT(String dateOUT) {
			this.dateOUT = dateOUT;
		}

		public String getTimeOUT() {
			return timeOUT;
		}

		public void setTimeOUT(String timeOUT) {
			this.timeOUT = timeOUT;
		}

		public String getDateCNLD() {
			return dateCNLD;
		}

		public void setDateCNLD(String dateCNLD) {
			this.dateCNLD = dateCNLD;
		}

		public String getTimeCNLD() {
			return timeCNLD;
		}

		public void setTimeCNLD(String timeCNLD) {
			this.timeCNLD = timeCNLD;
		}

		public String getMedDerivante() {
			return medDerivante;
		}

		public void setMedDerivante(String medDerivante) {
			this.medDerivante = medDerivante;
		}
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






