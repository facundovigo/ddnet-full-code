package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.Worklist;

@Local
public interface WorklistManagerLocal {
	
	void persist(Worklist w);
	void remove(Worklist w);
	Collection<Worklist> getAll();
	Worklist getByAccessionNumber(String accNumber);
	
	String getLastAccessionNumber();
}
