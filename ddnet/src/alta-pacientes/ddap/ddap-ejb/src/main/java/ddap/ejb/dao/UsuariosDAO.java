package ddap.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddap.ejb.entities.Usuario;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class UsuariosDAO extends AbstractDAO<Long, Usuario> {

	public UsuariosDAO() {
		super(Usuario.class);
	}


	@Override
	public void persist(Usuario user) {
		super.persist(user);
	}
	
	@Override
	public void remove(Usuario entity) {
		super.remove(entity);
	}
	
	@Override
	public Collection<Usuario> getAll() {
		
		return super.getAll();
	}
	
	
	public Usuario getByLogin(String login) {
		List<Usuario> result = 
				entityManager.createQuery("SELECT u FROM Usuario u WHERE u.login = :loginParam", Usuario.class)
				.setParameter("loginParam", login)
				.getResultList();
		
		if (result != null && !result.isEmpty())
			return result.get(0);
		
		return null;
	}
}
