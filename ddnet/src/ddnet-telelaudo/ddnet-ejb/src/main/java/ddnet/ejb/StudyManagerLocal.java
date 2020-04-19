package ddnet.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import ddnet.ejb.entities.LegacyFileSystem;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.StudyMedia;
import ddnet.ejb.entities.User;

@Local
public interface StudyManagerLocal {
	Collection<Study> findStudies(User user, StudySearchFilter filter);

	Study getStudy(User user, String studyID);	
	
	StudyMedia getStudyMedia(User user, String studyID, int quality, List<String> seriesIDs, List<String> options);
	
	int assignStudies(User requestingUser, User physician, String[] studiesIDs);
	
	Study getIndividualStudy(String studyID);
	
	LegacyFileSystem getStudyonfs(Long studyID);
	
	String getFileOfStudy(Long studyID);
	
	void removeAllStudyData(LegacyPatient pat);
	
	Collection<LegacyStudy> getByPatId(Long studyID, Long patID);
	Collection<LegacyStudy> getByPatId(Long studyID, String patID);
}
