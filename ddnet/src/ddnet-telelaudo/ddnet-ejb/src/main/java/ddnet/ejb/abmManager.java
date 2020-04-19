package ddnet.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.*;
import ddnet.ejb.entities.*;


/**
 * Session Bean implementation class abmManager
 */
@Stateless(mappedName = "ejb/ddnet/abmManager")
@LocalBean
public class abmManager implements abmManagerLocal {
	
	@EJB
	private UserDAO userdao;
	@EJB
	private UserInstitutionDAO userInstdao;
	@EJB
	private InstitutionDAO instdao;
	@EJB
	private ModalityDAO moddao;
	
	@Override
	public void persist(User user) {
		
		userdao.persist(user);
	}
	
	@Override
	public void remove(User user) {
		
		userdao.remove(user);
	}
	
	@Override
	public void persist(UserInstitution userInst) {
		
		userInstdao.persist(userInst); 
	}
	
	@Override
	public void remove(UserInstitution userInst) {
		
		userInstdao.remove(userInst);
	}
	
	@Override
	public UserInstitution getUI(Institution inst, User user) {
		
		return userInstdao.getUI(inst, user); 
	}
	
	@Override
	public void persist(Institution inst) {
		
		instdao.persist(inst);
	}
	
	@Override
	public List<String> getNotCreatedInstitution() {
		
		return instdao.getNotCreatedInstitution();
	}
	
	@Override
	public Collection<Institution> getByUser(User user) {
		
		return userInstdao.getByUser(user);
	}
	
	@Override
	public List<String> getNotCreatedModality() {
		
		return moddao.getNotCreatedModality();
	}
	
	@Override
	public void persist(Modality mod) {
		
		moddao.persist(mod);
	}
}
