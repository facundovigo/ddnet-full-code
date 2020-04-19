package ddnet.ejb.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import ddnet.ejb.StudySearchFilter;
import ddnet.ejb.entities.Modality;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.User;
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

		Calendar studyDateFrom = null;
		Calendar studyDateTo = null;
		if (filter.areSetDates()) {
			studyDateFrom = Calendar.getInstance();
			studyDateTo = Calendar.getInstance();
			studyDateFrom.setTime(filter.getStudyDateFrom());
			studyDateTo.setTime(filter.getStudyDateTo());
			studyDateTo.add(Calendar.DATE, 1); 
		}
		
		String jpql = "SELECT s " + 
					  "FROM Study s INNER JOIN FETCH s.legacyStudy ls INNER JOIN FETCH ls.legacyPatient lp ";

		// Limitar el dominio de estudios a aquellos que correspondan segun las instituciones y/o
		// estudios asignados puntualmente para el usuario consultante. 
		jpql += " WHERE (" + 
				"      (EXISTS (SELECT 'x' FROM UserInstitution ui " + 
				"      WHERE ui.user.id = :userID AND ui.institution.relatedAET = ls.custom3 AND (ui.user.administrator = TRUE OR ui.institution.administrativelyEnabled = TRUE))) OR " +
				"      ( s.id IN (SELECT us.study.id FROM UserStudy us WHERE us.user.id = :userID) ) " +
				")";
		
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		jpql += " AND LOWER(TRIM(BOTH FROM ls.modalities)) IN :userModalities ";
		
		if (filter.isSetPatientID())
			jpql += " AND LOWER(TRIM(BOTH FROM lp.patientID)) LIKE :patientID "; 
		if (filter.isSetPatientName())
			jpql += " AND LOWER(TRIM(BOTH FROM lp.name)) LIKE :patientName "; 
		if (filter.isSetModality())
			jpql += " AND LOWER(TRIM(BOTH FROM ls.modalities)) LIKE :modality ";			
		if (filter.isSetAccessionNumber())
			jpql += " AND LOWER(TRIM(BOTH FROM ls.accessionNumber)) LIKE :accessionNumber "; 
		if (filter.isSetPatientDOB())
			jpql += " AND TRIM(BOTH FROM lp.dob) = :patientDOB ";
		if (filter.areSetDates())
			jpql += " AND ls.date BETWEEN :studyDateFrom AND :studyDateTo ";
		
		TypedQuery<Study> query = entityManager.createQuery(jpql, Study.class);

		query.setParameter("userID", user.getId());
		
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		Set<String> userModalities = new HashSet<String>();
		for(Modality m : user.getModalities())
			userModalities.add(m.getName().toLowerCase());				
		query.setParameter("userModalities", userModalities.isEmpty() ? 
				Collections.singleton("<<null>>") : userModalities);

		if (filter.isSetPatientID())
			query.setParameter("patientID", "%" + filter.getPatientID().trim().toLowerCase() + "%");
		if (filter.isSetPatientName())
			query.setParameter("patientName", "%" + filter.getPatientName().trim().toLowerCase() + "%");
		if (filter.isSetModality())
			query.setParameter("modality", "%" + filter.getModality().trim().toLowerCase() + "%");
		if (filter.isSetAccessionNumber())
			query.setParameter("accessionNumber", "%" + filter.getAccessionNumber().trim().toLowerCase() + "%");
		if (filter.isSetPatientDOB())
			query.setParameter("patientDOB", DATE_FORMAT.format(filter.getPatientDOB()));
		if (filter.areSetDates())
			query
				.setParameter("studyDateFrom", studyDateFrom.getTime())
				.setParameter("studyDateTo", studyDateTo.getTime());
				
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
				"      WHERE ui.user.id = :userID AND ui.institution.relatedAET = ls.custom3 AND (ui.user.administrator = TRUE OR ui.institution.administrativelyEnabled = TRUE))) OR " +
				"      ( s.id IN (SELECT us.study.id FROM UserStudy us WHERE us.user.id = :userID) ) " +
				")";
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		jpql += " AND LOWER(TRIM(BOTH FROM ls.modalities)) IN :userModalities ";

		TypedQuery<Study> query = entityManager.createQuery(jpql, Study.class);		
		query.setParameter("studyID", studyID);		
		query.setParameter("userID", user.getId());
				
		// Siempre limitar los estudios a las modalidades que el usuario tenga habilitadas.
		Set<String> userModalities = new HashSet<String>();
		for(Modality m : user.getModalities())
			userModalities.add(m.getName().toLowerCase());				
		query.setParameter("userModalities", userModalities.isEmpty() ? 
				Collections.singleton("<<null>>") : userModalities);
		
		return query.getSingleResult();
	}
}
