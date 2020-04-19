package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.Usuario;

@Local
public interface UsuariosManagerLocal {
	
	void persist(Usuario u);
	void remove(Usuario u);
	
	Collection<Usuario> getAll();
	Usuario getByLogin(String login);
}
