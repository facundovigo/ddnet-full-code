package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.UserDAO;
import ddnet.ejb.dao.UserPropertyDAO;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserProperty;

/**
 * Session Bean implementation class UserManager
 */
@Stateless(mappedName = "ejb/ddnet/UserManager")
@LocalBean
public class UserManager implements UserManagerLocal {

	@EJB
	private UserDAO userDAO;
	@EJB
	private UserPropertyDAO userpropDAO;
	
    @Override
    public Collection<User> getAll() {
    	return userDAO.getAll();
    }

	@Override
	public User getByLogin(String login) {		
		return login != null ? userDAO.getByLogin(login) : null;
	}
	
	@Override
	public Collection<User> getUserbyModality(String userMod) {
		return userDAO.getUserbyModality(userMod);
	}
	
	@Override
	public Collection<User> getAdministratorUsers() {
		
		return userDAO.getAdministratorUsers();
	}
	
	public User getByID(Long userID) {
		
		return userDAO.getByID(userID);
	};
	
	@Override
	public void persist(UserProperty prop) {
		
		userpropDAO.persist(prop);
	}
}
