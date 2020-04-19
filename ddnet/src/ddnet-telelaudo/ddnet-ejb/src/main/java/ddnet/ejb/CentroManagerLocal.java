package ddnet.ejb;

import javax.ejb.Local;

import ddnet.ejb.entities.Centro;

@Local
public interface CentroManagerLocal {
	
	void persist(Centro data);
	void remove(Centro data);
}