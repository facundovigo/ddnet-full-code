package ddhemo.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddhemo.ejb.entities.User;

@Local
public interface UserManagerLocal {
	Collection<User> getAll();
	User getByLogin(String login);
}
