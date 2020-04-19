package ddhemo.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddhemo.ejb.entities.Role;

@Local
public interface RoleManagerLocal {
	Collection<Role> getAll();
	Role findById(Long id);
	Role findByName(String name);
}
