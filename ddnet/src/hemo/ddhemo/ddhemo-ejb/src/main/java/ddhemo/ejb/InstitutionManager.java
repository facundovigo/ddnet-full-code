package ddhemo.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddhemo.ejb.dao.InstitutionDAO;
import ddhemo.ejb.entities.Institution;

/**
 * Session Bean implementation class InstitutionManager
 */
@Stateless(mappedName = "ejb/ddhemo/InstitutionManager")
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
