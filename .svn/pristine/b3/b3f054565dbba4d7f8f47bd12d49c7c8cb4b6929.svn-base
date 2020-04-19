package ddnet.ejb;

import javax.ejb.Local;

import ddnet.ejb.entities.UserProfile;

@Local
public interface UserProfileManagerLocal {
	
	void persist(UserProfile up);
	void remove(UserProfile up);
	
	UserProfile getByUserID(Long userID);
}
