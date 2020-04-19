package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.RoleDAO;
import ddnet.ejb.entities.Role;

/**
 * Session Bean implementation class RoleManager
 */
@Stateless(mappedName = "ejb/ddnet/RoleManager")
@LocalBean
public class RoleManager implements RoleManagerLocal {

	@EJB
	private RoleDAO rolDAO;
	
    @Override
    public Collection<Role> getAll() {
    	return rolDAO.getAll();
    }

     @Override
     public Role findById(Long id) {
     	// TODO Auto-generated method stub
     	return rolDAO.findById(id);
     }
     
     @Override
     public Role findByName(String name) {
    	// TODO Auto-generated method stub
    	return rolDAO.findByName(name);
     }

}
