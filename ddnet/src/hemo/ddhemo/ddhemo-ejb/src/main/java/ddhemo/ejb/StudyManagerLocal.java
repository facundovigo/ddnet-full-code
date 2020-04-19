package ddhemo.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddhemo.ejb.entities.Study;
import ddhemo.ejb.entities.User;

@Local
public interface StudyManagerLocal {
	Collection<Study> findStudies(User user, StudySearchFilter filter);

	Study getStudy(User user, String studyID);
	
	/**
	 * Retorna un array de bytes correspondiente a un archivo .ZIP
	 * conteniendo todos los archivos DICOM de un estudio + un 
	 * archivo DICOMDIR indexando los mismos.
	 */
	byte[] getStudyMedia(User user, String studyID);
}
