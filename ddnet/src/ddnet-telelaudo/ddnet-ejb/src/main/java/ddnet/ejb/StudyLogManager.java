package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.StudyLogDAO;
import ddnet.ejb.entities.StudyLog;

/**
 * Session Bean implementation class StudyLogManager
 */
@Stateless(mappedName = "ejb/ddnet/StudyLogManager")
@LocalBean
public class StudyLogManager implements StudyLogManagerLocal{
	
	@EJB
	private StudyLogDAO studylogDAO;
	
	@Override
	public Collection<StudyLog> getStudyLogbyId(Long studyID) {
		
		return studylogDAO.getStudyLogbyId(studyID);
	}
	
	@Override
	public void persist(StudyLog data) {
		
		studylogDAO.persist(data);
	}
}
