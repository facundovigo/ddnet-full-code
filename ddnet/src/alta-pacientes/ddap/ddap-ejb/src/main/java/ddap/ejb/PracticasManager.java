package ddap.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.PracticasDAO;
import ddap.ejb.entities.Practica;

/**
 * Session Bean implementation class PracticasManager
 */
@Stateless(mappedName = "ejb/ddap/PracticasManager")
@LocalBean
public class PracticasManager implements PracticasManagerLocal {

	@EJB
	private PracticasDAO pDAO;
	
    
	@Override
	public void persist(Practica p) {
		pDAO.persist(p);	
	}
	@Override
	public void remove(Practica p) {
		pDAO.remove(p);
	}
	
	@Override
	public String getLastCode() {
		
		return pDAO.getLastCode();
	}
	
	@Override
	public Collection<Practica> getAll() {
		
		return pDAO.getAll();
	}
	
	@Override
	public Collection<Practica> getByModality(String mod) {
		
		return pDAO.getByModality(mod);
	}
	
	@Override
	public List<String> getModalities() {
		
		return pDAO.getModalities();
	}
	
	@Override
	public Practica findById(Long id) {
		
		return pDAO.findById(id);
	}
	
	@Override
	public Collection<Practica> findPracticas(PracticasSearchFilter filter) {
		
		return pDAO.findPracticas(filter);
	}
}
