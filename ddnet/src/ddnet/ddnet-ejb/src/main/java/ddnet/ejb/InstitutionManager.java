package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.InstitutionDAO;
import ddnet.ejb.entities.Institution;

/**
 * Session Bean implementation class InstitutionManager
 */
@Stateless(mappedName = "ejb/ddnet/InstitutionManager")
@LocalBean
public class InstitutionManager implements InstitutionManagerLocal {

	@EJB
	private InstitutionDAO institutionDAO;

	public Collection<Institution> getAll() {
		return institutionDAO.getAll();
	}
	
	@Override
	public Institution findById(Long id) {
		// TODO Auto-generated method stub
		return institutionDAO.findById(id); 
	}
	
}
