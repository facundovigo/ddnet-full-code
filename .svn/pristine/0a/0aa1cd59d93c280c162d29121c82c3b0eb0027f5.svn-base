package ddnet.ejb;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Local;

import ddnet.ejb.entities.User;

@Local
public interface MedicoUserManagerLocal {
	Collection<User> getAll(long id);
	Collection<User> getUserbyModality(Set<String> userMod);
}
