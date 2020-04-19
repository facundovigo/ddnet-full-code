package ddnet.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.UserProfileDAO;
import ddnet.ejb.entities.UserProfile;

/**
 * Session Bean implementation class UserProfileManager
 */
@Stateless(mappedName = "ejb/ddnet/UserProfileManager")
@LocalBean
public class UserProfileManager implements UserProfileManagerLocal {

	@EJB
	private UserProfileDAO UserProfileDAO;
	
	@Override
	public void persist(UserProfile up) {
		
		UserProfileDAO.persist(up);
	}
	
	@Override
	public void remove(UserProfile up) {
		
		UserProfileDAO.remove(up);
	}
	
	@Override
	public UserProfile getByUserID(Long userID) {
		
		return UserProfileDAO.getByUserID(userID);
	}
}
