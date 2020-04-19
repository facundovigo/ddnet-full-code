package ddnet.centeragent.dao;

import java.util.Collection;

import ddnet.centeragent.entities.Study;

public class StudyDAO extends AbstractDAO {
	
	public Collection<Study> getStudiesUnsentData() {		
		return getEntityManager().createQuery("SELECT s FROM Study s " +
				" WHERE s.isReported = true ", 
			Study.class).getResultList();		
	}
	
}
