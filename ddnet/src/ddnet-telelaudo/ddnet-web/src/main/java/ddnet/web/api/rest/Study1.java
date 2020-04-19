package ddnet.web.api.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ddnet.ejb.StudyManagerLocal;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.entities.Study;


@Stateless
@Path("/studies")
public class Study1 {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@EJB
	private StudyManagerLocal studyManager;
	
	
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
	
}
