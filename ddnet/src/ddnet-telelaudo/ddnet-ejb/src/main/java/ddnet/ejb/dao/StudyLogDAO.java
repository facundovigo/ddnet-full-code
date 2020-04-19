package ddnet.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddnet.ejb.entities.StudyLog;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class StudyLogDAO extends AbstractDAO<Long, StudyLog> {

	public StudyLogDAO() {
		super(StudyLog.class);
	}
	
	public Collection<StudyLog> getStudyLogbyId(Long studyID) {
		
		return entityManager.createQuery("SELECT log FROM StudyLog log WHERE log.studyID = :studyID" + 
					" ORDER BY log.date ASC", StudyLog.class)
					.setParameter("studyID", studyID)
					.getResultList();
	}
	
	@Override
	public void persist(StudyLog entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
}
