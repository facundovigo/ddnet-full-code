package ddnet.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import ddnet.ejb.entities.*;

@Local
public interface abmManagerLocal {
	
	void persist(User user);
	void remove(User user);
	void persist(UserInstitution userInst);
	void remove(UserInstitution userInst);
	void persist(Institution inst);
	void persist(Modality mod);
	void remove(LegacyInstance instance);
	void persist(UserPermissions up);
	List<String> getNotCreatedInstitution();
	Collection<Institution> getByUser(User user);
	UserInstitution getUI(Institution inst, User user);
	List<String> getNotCreatedModality();
}
