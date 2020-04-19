package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.Institution;
import ddnet.ejb.entities.User;
import ddnet.ejb.entities.UserInstitution;
import ddnet.ejb.util.data.AbstractDAO;


@Stateless
public class UserInstitutionDAO extends AbstractDAO<Long, UserInstitution> {
	
	public UserInstitutionDAO() {
		super(UserInstitution.class);
	}
	
	@Override
	public void persist(UserInstitution userInst) {
		
		super.persist(userInst);
	}
	
	@Override
	public void remove(UserInstitution entity) {
		
		super.remove(entity);
	}
	
	public Collection<Institution> getByUser(User user){
		
		return entityManager.createQuery("SELECT ui.institution FROM UserInstitution ui "
											+ " WHERE ui.user = :user ", Institution.class)
				.setParameter("user", user)
				.getResultList();
	}
	
	public UserInstitution getUI(Institution inst, User user){
		
		List<UserInstitution> result= 
			entityManager.createQuery(" SELECT ui FROM UserInstitution ui WHERE ui.institution = :inst "
									+ " AND ui.user = :user ", UserInstitution.class)
				.setParameter("inst", inst).setParameter("user", user).getResultList();
		return result.isEmpty()? null : result.get(0);
	}
	
	
}