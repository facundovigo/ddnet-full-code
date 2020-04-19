package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserProperty;

@Local
public interface UserManagerLocal {
	Collection<User> getAll();
	User getByLogin(String login);
	Collection<User> getUserbyModality(String userMod);
	Collection<User> getAdministratorUsers();
	User getByID(Long userID);
	void persist(UserProperty prop);
}
