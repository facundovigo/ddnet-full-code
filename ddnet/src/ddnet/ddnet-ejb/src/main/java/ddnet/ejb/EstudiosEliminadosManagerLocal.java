package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.*;

@Local
public interface EstudiosEliminadosManagerLocal {
	void persist(EstudiosEliminados deleted);
	void remove(EstudiosEliminados deleted);
	Collection<EstudiosEliminados> getAll();
	EstudiosEliminados findById(Long id);
	EstudiosEliminados getByStudyId(Long studyId);
}
