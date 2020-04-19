package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.Incidencia;

@Local
public interface IncidenciaManagerLocal {
	
	Collection<Incidencia> getAll(long studyID);
	void persist(Incidencia data);
}
