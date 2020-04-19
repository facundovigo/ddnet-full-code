package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.User;

@Local
public interface UserManagerLocal {
	Collection<User> getAll();
	User getByLogin(String login);
}
