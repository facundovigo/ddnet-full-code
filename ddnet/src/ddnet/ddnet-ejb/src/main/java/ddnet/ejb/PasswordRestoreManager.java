package ddnet.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.PasswordRestoreDAO;
import ddnet.ejb.entities.PasswordRestore;

/**
 * Session Bean implementation class PasswordRestoreManager
 */
@Stateless(mappedName = "ejb/ddnet/PasswordRestoreManager")
@LocalBean
public class PasswordRestoreManager implements PasswordRestoreManagerLocal {

	@EJB
	private PasswordRestoreDAO restoreDAO;
	
	@Override
	public void persist(PasswordRestore data) {
		
		restoreDAO.persist(data);
	}
	
	@Override
	public PasswordRestore getByKey(String key) {
		
		return restoreDAO.getByKey(key);
	}
}
