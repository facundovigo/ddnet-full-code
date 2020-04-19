package ddap.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.PrivilegiosDAO;
import ddap.ejb.entities.Privilegio;

/**
 * Session Bean implementation class UsuariosManager
 */
@Stateless(mappedName= "ejb/ddap/PrivilegiosManager")
@LocalBean
public class PrivilegiosManager implements PrivilegiosManagerLocal {

	@EJB private PrivilegiosDAO pDAO;
	
	@Override public void persist(Privilegio p){ pDAO.persist(p); }
	@Override public void remove(Privilegio p){ pDAO.remove(p); }
}
