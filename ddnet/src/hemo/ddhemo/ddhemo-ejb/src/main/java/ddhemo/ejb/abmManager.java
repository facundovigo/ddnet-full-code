package ddhemo.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddhemo.ejb.dao.*;
import ddhemo.ejb.entities.*;


/**
 * Session Bean implementation class abmManager
 */
@Stateless(mappedName = "ejb/ddhemo/abmManager")
@LocalBean
public class abmManager implements abmManagerLocal {
	
	@EJB
	private UserDAO userdao;
	@EJB
	private UserInstitutionDAO userInstdao;
	@EJB
	private InstitutionDAO instdao;
	
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
}
