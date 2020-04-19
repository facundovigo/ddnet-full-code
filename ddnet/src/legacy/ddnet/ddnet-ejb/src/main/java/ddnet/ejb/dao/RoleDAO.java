package ddnet.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddnet.ejb.entities.Role;
import ddnet.ejb.util.data.AbstractDAO;


@Stateless
public class RoleDAO extends AbstractDAO<Long, Role> {
	
	public RoleDAO() {
		super(Role.class);
	}
	
	
	@Override
	public Collection<Role> getAll() {
		return entityManager.createQuery("SELECT rol FROM Role rol WHERE rol.name != 'Paciente' ORDER BY rol.name ASC", Role.class)
				.getResultList();
	}
	
	@Override
	public Role findById(Long id) {
		// TODO Auto-generated method stub
		return entityManager.createQuery("SELECT r FROM Role r WHERE r.id = :id ", Role.class)
				.setParameter("id", id) 
				.getResultList().get(0);
	}
	
	public Role findByName(String name){
		return entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name ", Role.class)
				.setParameter("name", name)
				.getResultList().get(0);
	}
}