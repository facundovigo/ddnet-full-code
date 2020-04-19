package ddap.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.ObrasSocialesDAO;
import ddap.ejb.entities.ObraSocial;

/**
 * Session Bean implementation class ObrasSocialesManager
 */
@Stateless(mappedName = "ejb/ddap/ObrasSocialesManager")
@LocalBean
public class ObrasSocialesManager implements ObrasSocialesManagerLocal {

	@EJB
	private ObrasSocialesDAO osDAO;
	
    
	@Override
	public void persist(ObraSocial os) {
		osDAO.persist(os);	
	}
	@Override
	public void remove(ObraSocial os) {
		osDAO.remove(os);
	}
	
	@Override
	public String getLastCode() {
		
		return osDAO.getLastCode();
	}
	
	@Override
	public Collection<ObraSocial> getAll() {
		
		return osDAO.getAll();
	}
}
