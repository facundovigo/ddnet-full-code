package ddnet.ejb;

import java.util.Collection;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.MedicoUserDAO;
import ddnet.ejb.entities.User;

/**
 * Session Bean implementation class MedicoUserManager
 */
@Stateless(mappedName = "ejb/ddnet/MedicoUserManager")
@LocalBean
public class MedicoUserManager implements MedicoUserManagerLocal {
	
	@EJB
	private MedicoUserDAO medicouserDAO;
	

	public Collection<User> getAll(long id) {		
		
		return medicouserDAO.getMedicoUser(id);
	}
	
	@Override
	public Collection<User> getUserbyModality(Set<String> userMod) {
		// TODO Auto-generated method stub
		return medicouserDAO.getUserbyModality(userMod); 
	}
}
