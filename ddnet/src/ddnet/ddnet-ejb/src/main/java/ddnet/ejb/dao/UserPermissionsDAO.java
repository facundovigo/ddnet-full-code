package ddnet.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.UserPermissions;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class UserPermissionsDAO extends AbstractDAO<Long, UserPermissions> {
	public UserPermissionsDAO() {
		super(UserPermissions.class);
	}
	
	@Override
	public void persist(UserPermissions up) {
		// TODO Auto-generated method stub
		super.persist(up);
	}
	
	@Override
	public void remove(UserPermissions up) {
		// TODO Auto-generated method stub
		super.remove(up);
	}
	
	public UserPermissions getByUserID(Long userID){
		
		List<UserPermissions> result = 
				entityManager.createQuery(" SELECT up FROM UserPermissions up "
										+ " WHERE up.userID = :userIDparam", UserPermissions.class)
				.setParameter("userIDparam", userID.toString())
				.getResultList();
		
		return result.isEmpty() ? null : result.get(0);
	}
}
