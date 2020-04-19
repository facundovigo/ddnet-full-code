package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.ProvinciaDAO;
import ddnet.ejb.entities.Provincia;

/**
 * Session Bean implementation class ProvinciaManager
 */
@Stateless(mappedName = "ejb/ddap/ProvinciaManager")
@LocalBean
public class ProvinciaManager implements ProvinciaManagerLocal {

	@EJB
	private ProvinciaDAO provDAO;

	@Override
	public Collection<Provincia> getAll() {
		// TODO Auto-generated method stub
		return provDAO.getAll();
	}
	@Override
	public void persist(Provincia entity) {
		// TODO Auto-generated method stub
		provDAO.persist(entity);
	}
	@Override
	public void remove(Provincia entity) {
		// TODO Auto-generated method stub
		provDAO.remove(entity);
	}
}
