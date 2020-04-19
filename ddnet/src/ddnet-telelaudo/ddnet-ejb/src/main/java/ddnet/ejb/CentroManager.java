package ddnet.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.CentroDAO;
import ddnet.ejb.entities.Centro;

/**
 * Session Bean implementation class CentroManager
 */
@Stateless(mappedName = "ejb/ddnet/CentroManager")
@LocalBean
public class CentroManager implements CentroManagerLocal {

	@EJB
	private CentroDAO centroDAO;
	
	@Override
	public void persist(Centro data) {
		
		centroDAO.persist(data);
	}
	
	@Override
	public void remove(Centro data) {
		
		centroDAO.remove(data);
	}
}
