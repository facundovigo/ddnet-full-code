package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.Provincia;

@Local
public interface ProvinciaManagerLocal {
	Collection<Provincia> getAll();
	void persist(Provincia entity);
	void remove(Provincia entity);
}
