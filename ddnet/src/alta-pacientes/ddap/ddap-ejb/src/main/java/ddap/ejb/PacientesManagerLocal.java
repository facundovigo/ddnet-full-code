package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.Paciente;

@Local
public interface PacientesManagerLocal {
	
	void persist(Paciente p);
	void remove(Paciente p);
	
	Paciente getByPatID(String docNumber);
	Collection<Paciente> getAll();
	Collection<Paciente> getByName(String name);
	Collection<Paciente> searchPat(String s);
	Collection<Paciente> findPacientes(PacientesSearchFilter filter);
}
