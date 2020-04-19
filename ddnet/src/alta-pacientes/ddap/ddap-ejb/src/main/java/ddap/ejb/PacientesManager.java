package ddap.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.PacientesDAO;
import ddap.ejb.entities.Paciente;

/**
 * Session Bean implementation class PacientesManager
 */
@Stateless(mappedName = "ejb/ddap/PacientesManager")
@LocalBean
public class PacientesManager implements PacientesManagerLocal {

	@EJB
	private PacientesDAO pDAO;
	
    
	@Override
	public void persist(Paciente p) {
		pDAO.persist(p);	
	}
	@Override
	public void remove(Paciente p) {
		pDAO.remove(p);
	}
	@Override
	public Collection<Paciente> getAll() {
		// TODO Auto-generated method stub
		return pDAO.getAll();
	}
	
	@Override
	public Paciente getByPatID(String docNumber) {
		
		return pDAO.getByPatID(docNumber);
	}
	
	@Override
	public Collection<Paciente> getByName(String name) {
		
		return pDAO.getByName(name);
	}
	
	@Override
	public Collection<Paciente> searchPat(String s) {
		
		return pDAO.searchPat(s);
	}
	
	@Override
	public Collection<Paciente> findPacientes(PacientesSearchFilter filter) {
		
		return pDAO.findPacientes(filter);
	}
}
