package ddap.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.EquiposDAO;
import ddap.ejb.entities.Equipo;

/**
 * Session Bean implementation class EquiposManager
 */
@Stateless(mappedName = "ejb/ddap/EquiposManager")
@LocalBean
public class EquiposManager implements EquiposManagerLocal {

	@EJB
	private EquiposDAO eDAO;
	
    
	@Override
	public void persist(Equipo e) {
		eDAO.persist(e);	
	}
	@Override
	public void remove(Equipo e) {
		eDAO.remove(e);
	}
	
	@Override
	public String getLastCode() {
		
		return eDAO.getLastCode();
	}
	
	@Override
	public Collection<Equipo> getAll() {
		
		return eDAO.getAll();
	}
	
	@Override
	public List<String> getModalities() {
		
		return eDAO.getModalities();
	}
	
	@Override
	public Collection<Equipo> getByModality(String mod) {
		
		return eDAO.getByModality(mod);
	}
	
	@Override
	public Equipo getByCode(String code) {
		
		return eDAO.getByCode(code);
	}
}
