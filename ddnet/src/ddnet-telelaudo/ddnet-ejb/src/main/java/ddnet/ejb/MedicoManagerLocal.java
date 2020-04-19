package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.Medico;

@Local
public interface MedicoManagerLocal{
	Collection<Medico> getAll();
	Collection<Medico> getOnCall();
	Medico getByName(String name);
	void persist(Medico dr);
	void remove(Medico dr);
}

