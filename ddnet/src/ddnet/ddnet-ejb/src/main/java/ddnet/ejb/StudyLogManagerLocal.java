package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.StudyLog;

@Local
public interface StudyLogManagerLocal {
	Collection<StudyLog> getStudyLogbyId(Long studyID);
	void persist(StudyLog data);
}
