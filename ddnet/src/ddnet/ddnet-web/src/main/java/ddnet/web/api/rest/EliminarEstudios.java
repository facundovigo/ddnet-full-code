package ddnet.web.api.rest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.EstudiosEliminadosManager;
import ddnet.ejb.StudyLogManager;
import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.UserManager;
import ddnet.ejb.abmManager;
import ddnet.ejb.entities.EstudiosEliminados;
import ddnet.ejb.entities.LegacyFileSystem;
import ddnet.ejb.entities.LegacyInstance;
import ddnet.ejb.entities.LegacySerie;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("/study/delete")
public class EliminarEstudios {

	@EJB private SecurityHelper securityHelper;
	@EJB private StudyManagerLocal studyManager;
	@EJB private UserManager userManager;
	@EJB private ConfigurationManager configurationManager;
	@EJB private EstudiosEliminadosManager delManager;
	@EJB private abmManager abmManager;
	@EJB private StudyLogManager loggerManager;
	
	private static final SimpleDateFormat DATE_LOG_FORMAT= new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	@POST @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	public void deleteTheStudies
	(	@Context HttpServletRequest request,
		@Context HttpServletResponse response,
		@FormParam("studyIds[]")String[]studyIds,
		@FormParam("opCode")int opCode,
		@FormParam("studyOrPatient")int studyOrPatient) throws IOException{
		
		User user= securityHelper.getUser(request.getSession());
		PrintWriter out= response.getWriter();
		List<deletedStudiesDTO> report= new ArrayList<deletedStudiesDTO>();

		for(String s: studyIds) {
			Study study= studyManager.getIndividualStudy(s);
			List<deletedReportStateDTO> state= new ArrayList<deletedReportStateDTO>();
			//LegacyStudy ls= study.getLegacyStudy();
			//LegacyPatient lp= ls.getLegacyPatient();
			
			EstudiosEliminados deletedStudy= new EstudiosEliminados();
			deletedStudy.setStudyID(study.getId());
			deletedStudy.setUserLogin(user.getLogin());
			deletedStudy.setPatientID(study.getLegacyStudy().getLegacyPatient().getPatientID());
			deletedStudy.setPatientName(study.getLegacyStudy().getLegacyPatient().getName().replace("^^^^","").replace("^^^","").replace("^^","").replace("^"," "));
			deletedStudy.setModality(study.getLegacyStudy().getModalities());
			deletedStudy.setInstitutionName(study.getInstitution().getName());
			deletedStudy.setState(opCode);
			deletedStudy.setDeletedDate(DATE_LOG_FORMAT.format(new Date()));
			if(study.getLegacyStudy().getTotalInstances()>0)deletedStudy.setImgPath(getStudyImagesPath(study));
			if(study.isReported()) deletedStudy.setReportPath(getStudyReportPath(study));
			if(study.hasArchive()) deletedStudy.setFilePath(getStudyArchivesPath(study));
			if(study.hasOrdenMed()) deletedStudy.setOmPath(getStudyOMPath(study));
			
			switch(opCode){
				case 1: state.add(new deletedReportStateDTO("imagenes",deleteStudyImagesFromDirectory(study,false)));
						state.add(new deletedReportStateDTO("informe",deleteStudyReport(study)));
						state.add(new deletedReportStateDTO("archivos",deleteStudyFiles(study)));
						state.add(new deletedReportStateDTO("orden-medica",deleteStudyOM(study)));
						state.add(new deletedReportStateDTO("datos",deleteStudyFromDataBase(study,studyOrPatient==1)));
				break;
				case 2: state.add(new deletedReportStateDTO("imagenes",deleteStudyImagesFromDirectory(study,true)));
						state.add(new deletedReportStateDTO("informe",false));
						state.add(new deletedReportStateDTO("archivos",false));
						state.add(new deletedReportStateDTO("orden-medica",false));
						state.add(new deletedReportStateDTO("datos",false));
				break;
				case 3: state.add(new deletedReportStateDTO("imagenes",false));
						state.add(new deletedReportStateDTO("informe",false));
						state.add(new deletedReportStateDTO("archivos",false));
						state.add(new deletedReportStateDTO("orden-medica",false));
						state.add(new deletedReportStateDTO("datos",deleteStudyFromDataBase(study,studyOrPatient==1)));
				break;
				case 4: state.add(new deletedReportStateDTO("imagenes",false));
						state.add(new deletedReportStateDTO("informe",deleteStudyReport(study)));
						state.add(new deletedReportStateDTO("archivos",deleteStudyFiles(study)));
						state.add(new deletedReportStateDTO("orden-medica",deleteStudyOM(study)));
						state.add(new deletedReportStateDTO("datos",false));
				break;
				default: break;
			}
			report.add(new deletedStudiesDTO(
				String.format("%d",study.getLegacyStudy().getId()),
				study.getLegacyStudy().getLegacyPatient().getName()
				.replace("^^^^","").replace("^^^","").replace("^^","").replace("^"," "),
				state
			));
			delManager.persist(deletedStudy);
		}
		com.google.gson.Gson gson= new com.google.gson.Gson();
        String strJson= gson.toJson(report);
		out.print(strJson);
		out.close();
	}
	
	private boolean deleteStudyFromDataBase(Study study,boolean delStudy){
		boolean deleted= true;
		try{
			if(delStudy) studyManager.removeAllStudyData(study.getLegacyStudy());
			else studyManager.removeAllPatientData(study.getLegacyStudy().getLegacyPatient());	
		} catch(Exception e){deleted= false;}
		
		return deleted;
	}
	private boolean deleteStudyImagesFromDirectory(Study study,boolean clean){
		if(study.getLegacyStudy().getTotalInstances()<=0) return false;
		boolean deleted= false;
		try{
			if(eliminarArchivos(getStudyImagesPath(study))){
				deleted= true;
				if(clean){
				for(LegacySerie s: study.getLegacyStudy().getLegacySeries()){
					for(LegacyInstance i: s.getLegacyInstances()) abmManager.remove(i);
					s.setCantInstances(0);
				} study.getLegacyStudy().setTotalInstances(0); }
			}
		} catch(Exception e){deleted= false;}
		
		return deleted;
	}
	private boolean deleteStudyReport(Study study){
		if(!study.isReported()) return false;
		if(eliminarArchivos(getStudyReportPath(study))){
			study.setReported(false);
			study.setReport("");
			return true;
		} else return false;
	}
	private boolean deleteStudyFiles(Study study){
		if(!study.hasArchive()) return false;
		if(eliminarArchivos(getStudyArchivesPath(study))){
			study.setArchive(false);
			return true;
		} else return false;
	}
	private boolean deleteStudyOM(Study study){
		if(!study.hasOrdenMed()) return false;
		if(eliminarArchivos(getStudyOMPath(study))){
			study.setOrdenMed(false);
			return false;
		} else return false;
	}
	
	private String getStudyImagesPath(Study study){
		String path= "";
		LegacyFileSystem fs= study.getLegacyStudy().getLegacyStudyOnfs().getLegacyFileSystem();
		
		if(fs.getDirpath().toLowerCase().equals("archive")){
			path= configurationManager.getConfigurationItem("dcm4chee.server.default.path").getValue().replace('\\','/');
			path+= "/archive";
		}
		else path= fs.getDirpath();
		String aux= studyManager.getFileOfStudy(study.getId());
		aux= aux.substring(0,aux.lastIndexOf('/'));
		aux= aux.substring(0,aux.lastIndexOf('/'));
		path+= "/"+aux;
		return path;
	}
	private String getStudyArchivesPath(Study study){
		String path= "";
		
		path= configurationManager.getConfigurationItem("study.uploaded-files.path").getValue().replace('\\','/');
		path+= "/"+ DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate());
		path+= "/"+ study.getLegacyStudy().getStudyID();
		return path;
	}
	private String getStudyOMPath(Study study){
		String path= "";
		
		path= configurationManager.getConfigurationItem("study.uploaded-orden-medica.path").getValue().replace('\\','/');
		path+= "/"+ DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate());
		path+= "/"+ study.getLegacyStudy().getStudyID();
		return path;
	}
	private String getStudyReportPath(Study study){
		String path= "";
		
		path= configurationManager.getConfigurationItem("report.saved-reports.path").getValue().replace('\\','/');
		path+= "/"+ DATE_FOLDER_FORMAT.format(study.getLegacyStudy().getDate());
		path+= "/"+ study.getLegacyStudy().getStudyID() + ".pdf";
		return path;
	}
	
	public boolean eliminarArchivos(String path){
		File f= new File(path);
		if(f.isDirectory()) borrarDirectorio(f);
		return f.delete();
    }
	public void borrarDirectorio(File directorio){
	    File[]ficheros= directorio.listFiles();
        try{ for(File fichero: ficheros){ if(fichero.isDirectory())borrarDirectorio(fichero); fichero.delete();}
        } catch(Exception e){System.out.println(e.getMessage());}
    }

	public class deletedStudiesDTO{
		private String studyId;
		private String patName;
		private List<deletedReportStateDTO> state;
		public deletedStudiesDTO(String studyId,String patName,List<deletedReportStateDTO> state){
			super();
			this.studyId= studyId;
			this.patName= patName;
			this.state= state;
		}
		public String getStudyId(){return studyId;}
		public void setStudyId(String studyId){this.studyId= studyId;}
		public String getPatName(){return patName;}
		public void setPatName(String patName){this.patName= patName;}
		public List<deletedReportStateDTO> getState(){return state;}
		public void setState(List<deletedReportStateDTO> state){this.state= state;}
	}
	public class deletedReportStateDTO{
		private String desc;
		private boolean flag;
		public deletedReportStateDTO(String desc, boolean flag) {
			super();
			this.desc= desc;
			this.flag= flag;
		}
		public String getDesc(){return desc;}
		public void setDesc(String desc){this.desc= desc;}
		public boolean isUp(){return flag;}
		public void setFlag(boolean flag){this.flag= flag;}
	}
	
	public void registroEnElLog 
	(	ddnet.ejb.entities.Study study,
		User user,String accion,String parametro	){
		
		StudyLog logger = new StudyLog();
		logger.setStudyID(study.getId()); 
		logger.setDate(DATE_LOG_FORMAT.format(new Date()));
		logger.setUser(user.getLogin());
		logger.setAction(accion);
		logger.setParameter(parametro);
		loggerManager.persist(logger); 
	}
}
