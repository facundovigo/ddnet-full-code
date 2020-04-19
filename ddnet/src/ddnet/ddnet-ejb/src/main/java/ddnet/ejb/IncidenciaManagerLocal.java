package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.Incidencia;

@Local
public interface IncidenciaManagerLocal {
	
	Collection<Incidencia> getByStudyId(long studyId);
	void persist(Incidencia data);
	Incidencia getById(Long id);
}
