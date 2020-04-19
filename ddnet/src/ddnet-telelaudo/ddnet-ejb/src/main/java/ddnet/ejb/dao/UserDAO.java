package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.User;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class UserDAO extends AbstractDAO<Long, User> {

	public UserDAO() {
		super(User.class);
	}

	public User getByLogin(String login) {
		List<User> result = 
				entityManager.createQuery("SELECT u FROM User u WHERE u.login = :loginParam", User.class)
				.setParameter("loginParam", login)
				.getResultList();
		
		if (result != null && !result.isEmpty())
			return result.get(0);
		
		return null;
	}
	
	@Override
	public void persist(User user) {
		// TODO Auto-generated method stub
		super.persist(user);
	}
	
	@Override
	public Collection<User> getAll() {
		
		return entityManager.createQuery("SELECT u FROM User u ORDER BY u.login", User.class).getResultList();
	}
	
	@Override
	public void remove(User entity) {
		// TODO Auto-generated method stub
		super.remove(entity);
	}
	
	public Collection<User> getUserbyModality(String userMod) {
		
		return entityManager.createQuery("SELECT u FROM User u INNER JOIN FETCH u.modalities m WHERE u.id > 0 " /*+ userMod*/, User.class)
				.getResultList();
	}
	
	public Collection<User> getAdministratorUsers(){
		
		return entityManager.createQuery("SELECT DISTINCT(ui.user) FROM UserInstitution ui WHERE ui.role.name LIKE '%Administrador%'", User.class)
				.getResultList();
	}
	
	public User getByID(Long userID) {
		List<User> result = 
				entityManager.createQuery("SELECT u FROM User u WHERE u.id = :userID", User.class)
				.setParameter("userID", userID)
				.getResultList();
		
		if (result != null && !result.isEmpty())
			return result.get(0);
		
		return null;
	}
}
