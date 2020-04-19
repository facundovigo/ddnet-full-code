package ddap.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.WorklistDAO;
import ddap.ejb.entities.Worklist;

/**
 * Session Bean implementation class WorklistManager
 */
@Stateless(mappedName = "ejb/ddap/WorklistManager")
@LocalBean
public class WorklistManager implements WorklistManagerLocal {

	@EJB private WorklistDAO wDAO;
    
	@Override
	public void persist(Worklist w) {
		wDAO.persist(w);	
	}
	@Override
	public void remove(Worklist w) {
		wDAO.remove(w);
	}
	@Override
	public String getLastAccessionNumber() {
		
		return wDAO.getLastAccessionNumber();
	}
	@Override
	public Collection<Worklist> getAll() {
		return wDAO.getAll();
	}
	@Override
	public Worklist getByAccessionNumber(String accNumber) {
		return wDAO.getByAccessionNumber(accNumber);
	}
}
