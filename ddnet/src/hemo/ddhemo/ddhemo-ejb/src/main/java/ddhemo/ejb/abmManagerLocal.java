package ddhemo.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import ddhemo.ejb.entities.*;

@Local
public interface abmManagerLocal {
	
	void persist(User user);
	void remove(User user);
	void persist(UserInstitution userInst);
	void remove(UserInstitution userInst);
	void persist(Institution inst);
	List<String> getNotCreatedInstitution();
	Collection<Institution> getByUser(User user);
	UserInstitution getUI(Institution inst, User user);
}
