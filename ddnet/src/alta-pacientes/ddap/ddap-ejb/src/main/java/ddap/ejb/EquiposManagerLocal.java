package ddap.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import ddap.ejb.entities.Equipo;

@Local
public interface EquiposManagerLocal {
	
	void persist(Equipo e);
	void remove(Equipo e);
	
	String getLastCode();
	List<String> getModalities();
	
	Collection<Equipo> getAll();
	Collection<Equipo> getByModality(String mod);
	
	Equipo getByCode(String code);
}
