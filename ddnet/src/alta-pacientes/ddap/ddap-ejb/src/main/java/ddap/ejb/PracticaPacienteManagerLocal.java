package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.PracticaxPaciente;

@Local
public interface PracticaPacienteManagerLocal {
	
	void persist(PracticaxPaciente p);
	void remove(PracticaxPaciente p);
	
	Collection<PracticaxPaciente> getAll();
	Collection<PracticaxPaciente> findPracticaPaciente(PracticaPacienteSearchFilter filter);
	PracticaxPaciente getByCode(String code);
}
