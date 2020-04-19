package ddnet.ejb.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import ddnet.ejb.StudySearchFilter;
import ddnet.ejb.StudySearchFilter.StudyReportStatus;
import ddnet.ejb.StudySearchFilter.StudySearchDateType;
import ddnet.ejb.entities.LegacyFileSystem;
import ddnet.ejb.entities.Modality;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserInstitution;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class StudyDAO extends AbstractDAO<Long, Study> {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public StudyDAO() {
		super(Study.class);
	}

	public List<Study> findStudies(User user, StudySearchFilter filter) {
		if (filter == null)
			throw new IllegalArgumentException("'filter' can not be null");
		
		if (filter.getDateType() == StudySearchDateType.between &&
			(filter.getStudyDateFrom() == null || filter.getStudyDateTo() == null))
			throw new IllegalArgumentException("From and To dates are required when the 'between' study date type is specified.");
		
		final Calendar now = Calendar.getInstance();
		Calendar studyDateFrom = Calendar.getInstance();
		Calendar studyDateTo = Calendar.getInstance();
		studyDateFrom.clear();
		studyDateTo.clear();
		
		switch(filter.getDateType()) {
			case any:
				studyDateFrom = null;
				studyDateTo = null;
				break;
			case today:
				studyDateFrom.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				studyDateTo.setTime(now.getTime());
				break;
			case yesterday:
				studyDateFrom.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				studyDateFrom.add(Calendar.DATE, -1);
				studyDateTo.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				break;
			case lastweek:
				studyDateFrom.setTime(now.getTime());
				studyDateFrom.add(Calendar.DATE, -7);
				studyDateTo.setTime(now.getTime());
				break;
			case lastmonth:
				studyDateFrom.setTime(now.getTime());
				studyDateFrom.add(Calendar.DATE, -30);
				studyDateTo.setTime(now.getTime());
				break;
			case between:
				studyDateFrom.setTime(filter.getStudyDateFrom());
				studyDateTo.setTime(filter.getStudyDateTo());
				break;
		}
		
		String jpql = "SELECT s " + 
					  "FROM Study s INNER JOIN FETCH s.legacyStudy ls INNER JOIN FETCH ls.legacyPatient lp ";
				
		// Limitar el dominio de estudios a aquellos que correspondan segun las instituciones y/o
		// estudios asignados puntualmente para el usuario consultante. 
		jpql += " WHERE (" + 
				"      (EXISTS (SELECT 'x' FROM UserInstitution ui " + 
				"      WHERE ui.id IN :allowedInstitutionsIDs AND ui.user.id = :userID AND ui.institution.relatedAET = ls.custom3 AND (ui.user.administrator = TRUE OR ui.institution.administrativelyEnabled = TRUE))) OR " +
				"      ( s.id IN (SELECT us.study.id FROM UserStudy us WHERE us.user.id = :userID) ) " +
				")";
		
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		jpql += " AND LOWER(TRIM(BOTH FROM ls.modalities)) IN :userModalities ";
		
		if (filter.isSetPatientData())
			jpql += " AND LOWER(TRIM(BOTH FROM lp.name)) LIKE :patientData "
					+ " OR LOWER(TRIM(BOTH FROM lp.patientID)) LIKE :patientData "; 
		
		
		if (filter.isSetModality())
			jpql += " AND LOWER(TRIM(BOTH FROM ls.modalities)) IN :modality ";			
		
		
		if (filter.isSetAccessionNumber())
			jpql += " AND LOWER(TRIM(BOTH FROM ls.accessionNumber)) LIKE :accessionNumber "; 
		if (filter.isSetPatientDOB())
			jpql += " AND TRIM(BOTH FROM lp.dob) = :patientDOB ";
		
		
		if (filter.getDateType() != StudySearchDateType.any)
			jpql += " AND ls.date BETWEEN :studyDateFrom AND :studyDateTo ";
		
		
		if (filter.isSetInstitutionID())
			jpql += " AND s.legacyStudy.institution.id = :institutionID ";
		if (filter.getReportStatus() == StudyReportStatus.nonreported)
			jpql += " AND s.isReported = false ";
		
		if (filter.isSetUserStudy())
			jpql += " AND (LOWER(TRIM(BOTH FROM s.Ustudy.user.perfil.fancyName)) LIKE :userStudy "
					+ " OR LOWER(TRIM(BOTH FROM s.Ustudy.user.login)) LIKE :userStudy) ";
		
		if (filter.isUrgente())
			jpql += " AND s.datosclinicos.priority = 2 ";
		
		if (filter.isPrioridad()) 
			jpql += " AND s.datosclinicos.priority = 1 ";
			
		if (filter.hasArchive())
			jpql += " AND s.hasArchive = true ";
		
		if (filter.isFirmado())
			jpql += " AND s.isReported = true ";
		
		if (filter.isPreinformado())
			jpql += " AND (s.isReported = false AND length(s.report) > 0) ";
			
		if (filter.isNoLeido())
			jpql += " AND (s.isReported = false AND quote_nullable(s.report) = 'NULL') ";
		
		if (filter.isIncidencia())
			jpql += " AND s.state > 0 ";
		if (filter.isSecReading())
			jpql += " AND s.informe.secondReading = true ";
		if (filter.isTeachingFile())
			jpql += " AND s.informe.tFile = true ";
		if (filter.isEmergencyCase())
			jpql += " AND s.informe.emergency = true ";
		if (filter.isMultiple())
			jpql += " AND s.informe.mult > 1 ";
		if (filter.toCheck())
			jpql += " AND s.informe.checkState = 1 ";
		if (filter.Solicited())
			jpql += " AND s.informe.checkState = 2 ";
		if (filter.Checked())
			jpql += " AND s.informe.checkState = 3 ";
		
		if(filter.isNewer())
			jpql += " AND s.state = 0 " +
					" AND NOT EXISTS ( select us from UserStudy us where us.study.id = s.id ) " +
					" AND NOT EXISTS ( select d from DatosClinicos d where d.studyID = s.id ) " +
					" AND s.hasArchive = false ";
		
		jpql += " ORDER BY ls.date DESC ";
		
		TypedQuery<Study> query = entityManager.createQuery(jpql, Study.class);

		query.setParameter("userID", user.getId());

		final Collection<Long> allowedInstitutionsIDs = getAllowedInstitutionsForStudiesAccess(user);
		query.setParameter("allowedInstitutionsIDs", allowedInstitutionsIDs);
		
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		Set<String> userModalities = new HashSet<String>();
		for(Modality m : user.getModalities())
			userModalities.add(m.getName().toLowerCase());				
		query.setParameter("userModalities", userModalities.isEmpty() ? 
				Collections.singleton("<<null>>") : userModalities);

		if (filter.isSetPatientData())
			query.setParameter("patientData", "%" + filter.getPatientData().trim().toLowerCase() + "%");
//..................................................................................................//
		if (filter.isSetUserStudy())
			query.setParameter("userStudy", "%" + filter.getUserStudy().trim().toLowerCase() + "%");
//..................................................................................................//
		
		
		if (filter.isSetModality()){
			Set<String> modalities= new HashSet<String>();
			String[] filterModalities= filter.getModality().split(",");
			System.out.println("/********************************************************************************/");
			for(int idx=0; idx<filterModalities.length; idx++) {
				modalities.add(filterModalities[idx].toLowerCase().replace('_','\\'));
				System.out.println(filterModalities[idx]);
			}
			System.out.println("/********************************************************************************/");
			
			query.setParameter("modality", modalities.isEmpty()? Collections.singleton("<<null>>") : modalities);
		}
			//query.setParameter("modality", "%" + filter.getModality().trim().toLowerCase() + "%");
		
		if (filter.isSetAccessionNumber())
			query.setParameter("accessionNumber", "%" + filter.getAccessionNumber().trim().toLowerCase() + "%");
		if (filter.isSetPatientDOB())
			query.setParameter("patientDOB", DATE_FORMAT.format(filter.getPatientDOB()));
		
		
		if (filter.getDateType() != StudySearchDateType.any)
			query
				.setParameter("studyDateFrom", studyDateFrom.getTime())
				.setParameter("studyDateTo", studyDateTo.getTime());
		
		
		
		if (filter.isSetInstitutionID())
			query.setParameter("institutionID", filter.getInstitutionID());
			
		List<Study> resultList = query.getResultList();
		return resultList;
	}

	public Study getStudy(User user, String studyID) {
		
		String jpql = "SELECT s " + 
				  "FROM Study s JOIN s.legacyStudy ls " +
				  "WHERE ls.studyID = :studyID";
		
		// Limitar el dominio de estudios a aquellos que correspondan segun las instituciones y/o
		// estudios asignados puntualmente para el usuario consultante. 
		jpql += " AND (" + 
				"      (EXISTS (SELECT 'x' FROM UserInstitution ui " + 
				"      WHERE ui.id IN :allowedInstitutionsIDs AND ui.user.id = :userID AND ui.institution.relatedAET = ls.custom3 AND (ui.user.administrator = TRUE OR ui.institution.administrativelyEnabled = TRUE))) OR " +
				"      ( s.id IN (SELECT us.study.id FROM UserStudy us WHERE us.user.id = :userID) ) " +
				")";
		
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		jpql += " AND LOWER(TRIM(BOTH FROM ls.modalities)) IN :userModalities ";

		TypedQuery<Study> query = entityManager.createQuery(jpql, Study.class);		
		query.setParameter("studyID", studyID);		
		query.setParameter("userID", user.getId());

		final Collection<Long> allowedInstitutionsIDs = getAllowedInstitutionsForStudiesAccess(user);
		query.setParameter("allowedInstitutionsIDs", allowedInstitutionsIDs);
				
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		Set<String> userModalities = new HashSet<String>();
		for(Modality m : user.getModalities())
			userModalities.add(m.getName().toLowerCase());				
		query.setParameter("userModalities", userModalities.isEmpty() ? 
				Collections.singleton("<<null>>") : userModalities);
		
		return query.getSingleResult();
	}
	
	private Collection<Long> getAllowedInstitutionsForStudiesAccess(User user) {
		// Para el filtro de dominio de estudios, necesitamos saber sobre que instituciones tenemos un rol que nos permita
		// "ver" los estudios de dicha institucion.
		final Collection<Long> allowedInstitutionsIDs = new ArrayList<Long>();
		allowedInstitutionsIDs.add(Long.MIN_VALUE); // para evitar problemas en el "IN", si no tenemos ninguna institucion habilitada. 
		for(UserInstitution ui : user.getInstitutions())
			if(user.getPermissions().isCanViewAllStudies())
			//if (ui.getRole().allowsTo(Action.ACCESS_STUDIES_FROM_INSTITUION))
				allowedInstitutionsIDs.add(ui.getId());
		return allowedInstitutionsIDs;
	}
	
	public Study getIndividualStudy( String studyID ) {
		
		return entityManager.createQuery("SELECT s FROM Study s INNER JOIN FETCH s.legacyStudy ls "
				+ " WHERE ls.studyID = :studyID ", Study.class)
		.setParameter("studyID", studyID)
		.getResultList()
		.get(0);
	}
	
	public LegacyFileSystem getStudyonfs(Long studyID){
		
		return entityManager.createQuery("SELECT fs FROM LegacyFileSystem fs "
				+ " WHERE fs.id = (SELECT sfs.fsID FROM LegacyStudyOnFS sfs"
								+ " WHERE sfs.studyID = :studyPKparam ) ", LegacyFileSystem.class)
			.setParameter("studyPKparam", studyID)
			.getResultList()
			.get(0);
	}
	
	public String getFileOfStudy(Long studyID){
		
		return entityManager.createQuery(" SELECT min(f.filepath) FROM LegacyFile f "
				+ " INNER JOIN f.legacyInstance i INNER JOIN i.legacySerie se "
				+ " INNER JOIN se.legacyStudy s WHERE s.id = :studyPKparam ")
			.setParameter("studyPKparam", studyID)
			.getResultList()
			.get(0).toString();
	}
	
	public Collection<Study> getStudiesByPatID(String patID){
		
		List<Study> result =
				entityManager.createQuery(" SELECT s FROM Study s "
										+ " INNER JOIN FETCH s.legacyStudy ls "
										+ " INNER JOIN FETCH ls.legacyPatient lp "
										+ " WHERE lp.patientID = :patientIDparam ", Study.class)
				.setParameter("patientIDparam", patID)
				.getResultList();
		
		return result.isEmpty() ? null : result;
	}
	
}