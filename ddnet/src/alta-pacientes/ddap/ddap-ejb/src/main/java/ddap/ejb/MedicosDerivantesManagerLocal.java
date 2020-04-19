package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.MedicoDerivante;

@Local
public interface MedicosDerivantesManagerLocal {
	
	void persist(MedicoDerivante l);
	void remove(MedicoDerivante l);
	Collection<MedicoDerivante> getAll();
}
