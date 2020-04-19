package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.Logger;

@Local
public interface LoggerManagerLocal {
	
	void persist(Logger l);
	void remove(Logger l);
	
	Collection<Logger> getAll();
}
