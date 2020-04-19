package ddap.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import ddap.ejb.entities.Practica;

@Local
public interface PracticasManagerLocal {
	
	void persist(Practica p);
	void remove(Practica p);
	
	String getLastCode();
	List<String> getModalities();
	
	Collection<Practica> getAll();
	Collection<Practica> getByModality(String mod);
	Collection<Practica> findPracticas(PracticasSearchFilter filter);
	
	Practica findById(Long id);
}
