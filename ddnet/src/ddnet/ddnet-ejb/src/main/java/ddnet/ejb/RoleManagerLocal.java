package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.Role;

@Local
public interface RoleManagerLocal {
	Collection<Role> getAll();
	Role findById(Long id);
	Role findByName(String name);
	Collection<Role> getNewRoles();
}
