package ddap.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.UsuariosDAO;
import ddap.ejb.entities.Usuario;

/**
 * Session Bean implementation class UsuariosManager
 */
@Stateless(mappedName = "ejb/ddap/UsuariosManager")
@LocalBean
public class UsuariosManager implements UsuariosManagerLocal {

	@EJB
	private UsuariosDAO uDAO;
	
    
	@Override
	public void persist(Usuario u) {
		uDAO.persist(u);	
	}
	@Override
	public void remove(Usuario u) {
		uDAO.remove(u);
	}
	@Override
	public Collection<Usuario> getAll() {
		
		return uDAO.getAll();
	}
	
	@Override
	public Usuario getByLogin(String login) {		
		return login != null ? uDAO.getByLogin(login) : null;
	}
}
