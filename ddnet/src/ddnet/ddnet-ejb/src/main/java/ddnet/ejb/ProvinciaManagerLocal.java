package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.Provincia;

@Local
public interface ProvinciaManagerLocal {
	Collection<Provincia> getAll();
	void persist(Provincia entity);
	void remove(Provincia entity);
}
