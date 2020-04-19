package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import ddnet.ejb.entities.User;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class MedicoUserDAO extends AbstractDAO<Long, User> {
	
	public MedicoUserDAO() {
		super(User.class);
	}
	
	public Collection<User> getMedicoUser(long id){
		List<User> result =
				entityManager.createQuery("SELECT Mu FROM User Mu WHERE Mu.id = :idParam", User.class)
				.setParameter("idParam", id)
				.getResultList();
		
		return result;
	}
	
	public Collection<User> getUserbyModality(Set<String> userMod) {
		
		String sql = "SELECT DISTINCT u FROM User u INNER JOIN FETCH u.modalities m "
									+ " WHERE LOWER(TRIM(BOTH FROM m.name)) IN (:userMod) ";
		
		return entityManager.createQuery(sql, User.class)
				.setParameter("userMod", userMod)
				.getResultList();
	}
}
