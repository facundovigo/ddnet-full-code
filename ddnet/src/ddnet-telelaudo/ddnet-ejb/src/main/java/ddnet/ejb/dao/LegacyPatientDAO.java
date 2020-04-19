package ddnet.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class LegacyPatientDAO extends AbstractDAO<Long, LegacyPatient> {

	public LegacyPatientDAO() {
		super(LegacyPatient.class);
	}
	
	@Override
	public void remove(LegacyPatient entity) {
		// TODO Auto-generated method stub
		super.remove(entity);
	}
	
	public Collection<LegacyStudy> getByPatId(Long studyID, Long patID){
		
		return entityManager.createQuery("SELECT s FROM LegacyStudy s "
						+ " INNER JOIN FETCH s.legacyPatient p "
						+ " WHERE s.id <> :studyID AND p.id = :patID ", LegacyStudy.class)
				.setParameter("studyID", studyID)
				.setParameter("patID", patID)
				.getResultList();
	}
	
	public Collection<LegacyStudy> getByPatId(Long studyID, String patID){
		
		return entityManager.createQuery("SELECT s FROM LegacyStudy s "
						+ " INNER JOIN FETCH s.legacyPatient p WHERE s.id <> :studyID "
						+ " AND LOWER(TRIM(BOTH FROM p.patientID)) LIKE :patientID ", LegacyStudy.class)	
				.setParameter("studyID", studyID)
				.setParameter("patientID", "%" + patID.trim().toLowerCase() + "%")
				.getResultList();
	}
}
