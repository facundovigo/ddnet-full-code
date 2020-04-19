package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.Modality;

@Local
public interface ModalityManagerLocal {
	Collection<Modality> getAll();
	Modality findByName(String name);
}
