package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.ObraSocial;

@Local
public interface ObrasSocialesManagerLocal {
	
	void persist(ObraSocial os);
	void remove(ObraSocial os);
	
	String getLastCode();
	
	Collection<ObraSocial> getAll();
}
