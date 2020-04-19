package ddnet.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.UserProfile;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class UserProfileDAO extends AbstractDAO<Long, UserProfile> {
	public UserProfileDAO() {
		super(UserProfile.class);
	}
	
	@Override
	public void persist(UserProfile up) {
		// TODO Auto-generated method stub
		super.persist(up);
	}
	
	@Override
	public void remove(UserProfile up) {
		// TODO Auto-generated method stub
		super.remove(up);
	}
	
	public UserProfile getByUserID(Long userID){
		
		List<UserProfile> result = 
				entityManager.createQuery(" SELECT up FROM UserProfile up "
										+ " WHERE up.userID = :userIDparam", UserProfile.class)
				.setParameter("userIDparam", userID.toString())
				.getResultList();
		
		return result.isEmpty() ? null : result.get(0);
	}
}
