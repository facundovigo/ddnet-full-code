package ddap.web.api.rest;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import ddap.ejb.ConfigurationManager;
import ddap.ejb.PracticaPacienteManager;
import ddap.ejb.PracticasManager;
import ddap.ejb.LoggerManager;
import ddap.ejb.PracticasSearchFilter;
import ddap.ejb.UsuariosManager;
import ddap.ejb.WorklistManager;
import ddap.ejb.entities.Practica;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.PracticaxPaciente;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/practica")
public class Practice {
	
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_FILE_FORMAT = new SimpleDateFormat("HHmmssSSS");
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UsuariosManager userManager;
	@EJB
	private PracticasManager pManager;
	@EJB
	private LoggerManager logManager;
	@EJB
	private ConfigurationManager configurationManager;
	@EJB
	private WorklistManager wManager;
	@EJB
	private PracticaPacienteManager ppManager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Practica> getRequested(@Context HttpServletRequest request,
											 @QueryParam("modality") String modality,
											 @QueryParam("name") String name,
											 @QueryParam("capability") String capability,
											 @QueryParam("region") String strRegion,
											 @QueryParam("need-report") String needReport){
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		int region = strRegion.equals("") ? -1 : Integer.parseInt(strRegion);
		PracticasSearchFilter filter = new PracticasSearchFilter(modality, name, capability, region, needReport);
		
		Collection<Practica> all = pManager.findPracticas(filter);
		
		return all == null || all.isEmpty() ? null : all;
	}
	
			
	@POST
	@Path("/add/{new}")
	public Response addNewEquipment(@Context HttpServletRequest request,
									@FormParam("id-practica") String id,
									@FormParam("modalidad") String mod,
									@FormParam("nombre") String nombre,
									@FormParam("prestacion") String prestacion,
									@FormParam("region") String strRegion,
									@FormParam("interno") String interno,
									@FormParam("abrev") String abrev,
									@FormParam("servicio") String servicio,
									@FormParam("especialidad") String especialidad,
									@FormParam("tipo") String tipo,
									@FormParam("reqInforme") String reqInforme,
									@FormParam("regionInforme") String regionInforme,
									@FormParam("valor") float valor,
									@FormParam("emergency-value") float emergencyValue,
									@PathParam("new") String newOrNot){
		
		Usuario u = securityHelper.getUser(request.getSession());
		
		Practica p = new Practica();
		boolean isNew = newOrNot.equals("true");
		
		if(!isNew) p = pManager.findById(Long.parseLong(id));
		
		if(mod != null && !mod.isEmpty())
			p.setModalidad(mod);
		if(nombre != null && !nombre.isEmpty())
			p.setNombre(nombre);
		if(prestacion != null && !prestacion.isEmpty())
			p.setPrestacion(prestacion);
		
		p.setValor(valor);
		p.setEmergencyValue(emergencyValue);
		
		int region = strRegion != null && !strRegion.equals("") ? Integer.parseInt(strRegion) : -1;
		if(region > -1) p.setRegion(region);
			
		if(interno != null && !interno.isEmpty())
			p.setInterno(interno);
		if(abrev != null && !abrev.isEmpty())
			p.setAbreviado(abrev);
		if(servicio != null && !servicio.isEmpty())
			p.setServicio(servicio);
		if(especialidad != null && !especialidad.isEmpty())
			p.setEspecialidad(especialidad);
		if(tipo != null && !tipo.isEmpty())
			p.setTipo(tipo);
		
			p.setRequiereInforme(reqInforme != null);
			
		if(regionInforme != null && !regionInforme.isEmpty())
			p.setRegionInforme(regionInforme);
		
		if(isNew){
			for(Practica prac : pManager.getAll()){
				if(p.getPrestacion().equals(prac.getPrestacion()) && p.getRegion() == prac.getRegion())
					return Response.status(Status.NOT_ACCEPTABLE).build();
			}
		pManager.persist(p);
		recordInLog(u.getLogin(), "ALTA DE PRÁCTICA", nombre);
		
		} else recordInLog(u.getLogin(), "EDICIÓN DE PRÁCTICA", nombre);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Practica> getAll( @Context HttpServletRequest request){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(!pManager.getAll().isEmpty()) return pManager.getAll();
		
		else return null;
	}
	
	
	
	
	@GET
	@Path("{practice-id}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Practica getCurrent(	@Context HttpServletRequest request,
								@PathParam("practice-id") String id){
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(id == null || id.equals("")) return null;
		else return pManager.findById(Long.parseLong(id));
	}
	
	
	
	@POST
	@Path("delete")
	public Response deletePractices(@Context HttpServletRequest request,
									@FormParam("practiceIDs[]") String[] practiceIDs){
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(practiceIDs == null) return Response.status(Status.NO_CONTENT).build();
		
		for(String s : practiceIDs){
			if(s != null && !"".equals(s)){
				
				Practica p = pManager.findById(Long.parseLong(s));
				pManager.remove(p);
				
				recordInLog(u.getLogin(), "ELIMINACIÓN DE PRÁCTICA", p.getNombre());
			}
		}
		
		return Response.ok().build();
	}
	
	
	
	@GET
	@Path("/modality/{mod-name}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Practica> getByModality( @Context HttpServletRequest request,
												@PathParam("mod-name") String mod){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(!pManager.getByModality(mod).isEmpty()) return pManager.getByModality(mod);
		
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
	
	@GET
	@Path("/mods")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getModalities( @Context HttpServletRequest request){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(pManager.getModalities() != null && !pManager.getModalities().isEmpty()) 
			return pManager.getModalities();
		
		else return null;
	}
	
	
	@POST
	@Path("{documento}/files")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadStudyFile(@Context HttpServletRequest request,
									@PathParam("documento") String doc) throws IOException{
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> parseRequest = uploader.parseRequest(request);
			for (FileItem fileItem : parseRequest) {
				if ("orden-medica-paciente".equals(fileItem.getFieldName())) {
					
					if(	!FilenameUtils.getExtension(fileItem.getName()).equals("jpg")  &&
						!FilenameUtils.getExtension(fileItem.getName()).equals("jpeg") &&
						!FilenameUtils.getExtension(fileItem.getName()).equals("png")  &&
						!FilenameUtils.getExtension(fileItem.getName()).equals("gif"))
							return Response.status(Status.NOT_ACCEPTABLE).build();
					
					Date now = new Date();
					String code = wManager.getLastAccessionNumber();
					String fileName = 	code + "-" + DATE_FILE_FORMAT.format(now) + 
										"." + FilenameUtils.getExtension(fileItem.getName());
					String uploadedFilename = new File(getOMUploadDirectory(doc, now), fileName).getAbsolutePath();
					
					writeToFile(uploadedFilename, fileItem.get());
					
					PracticaxPaciente pp = ppManager.getByCode(code);
					pp.setOmPath(uploadedFilename.substring(0, uploadedFilename.lastIndexOf('/')));
					
					break;
				}
			}
		} catch (FileUploadException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).build(); }
		
		return Response.ok().build();
	}
	
	private void writeToFile(String filename, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(filename));
			out.write(data);
		
		} finally {
			if (out != null){
				try {
					out.flush();
					out.close();
				} catch(Throwable t) {}
			}
		}
	}	
	
	private File getOMUploadDirectory(String doc, Date now) {
		File rootDirectory = getOMUploadedFilesRootDirectory();
		File studyFileDateFolder = new File(rootDirectory, DATE_FOLDER_FORMAT.format(now));
		if (!studyFileDateFolder.exists()) studyFileDateFolder.mkdirs();
		File studyFilesFolder = new File(studyFileDateFolder, doc.replace(" ", ""));
		if (!studyFilesFolder.exists()) studyFilesFolder.mkdirs();
		return studyFilesFolder;
	}

	private File getOMUploadedFilesRootDirectory() {
		return new File(configurationManager.getConfigurationItem("study.uploaded-orden-medica.path").getValue());
	}
}

























