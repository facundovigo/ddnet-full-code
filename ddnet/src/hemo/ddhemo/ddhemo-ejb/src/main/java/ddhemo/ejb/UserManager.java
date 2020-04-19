package ddhemo.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddhemo.ejb.dao.UserDAO;
import ddhemo.ejb.entities.User;

/**
 * Session Bean implementation class UserManager
 */
@Stateless(mappedName = "ejb/ddhemo/UserManager")
@LocalBean
public class UserManager implements UserManagerLocal {

	@EJB
	private UserDAO userDAO;
	
    @Override
    public Collection<User> getAll() {
    	return userDAO.getAll();
    }

	@Override
	public User getByLogin(String login) {		
		return login != null ? userDAO.getByLogin(login) : null;
	}
}
