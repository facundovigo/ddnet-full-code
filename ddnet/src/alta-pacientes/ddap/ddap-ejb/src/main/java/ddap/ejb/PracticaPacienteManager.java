package ddap.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.PracticaPacienteDAO;
import ddap.ejb.entities.PracticaxPaciente;

/**
 * Session Bean implementation class PracticasManager
 */
@Stateless(mappedName = "ejb/ddap/PracticaPacienteManager")
@LocalBean
public class PracticaPacienteManager implements PracticaPacienteManagerLocal {

	@EJB
	private PracticaPacienteDAO pDAO;
	
    
	@Override
	public void persist(PracticaxPaciente p) {
		pDAO.persist(p);	
	}
	@Override
	public void remove(PracticaxPaciente p) {
		pDAO.remove(p);
	}
	
	@Override
	public Collection<PracticaxPaciente> getAll() {
		
		return pDAO.getAll();
	}
	@Override
	public PracticaxPaciente getByCode(String code) {
		
		return pDAO.getByCode(code);
	}
	@Override
	public Collection<PracticaxPaciente> findPracticaPaciente(
			PracticaPacienteSearchFilter filter) {
		
		return pDAO.findPracticaPaciente(filter);
	}
	
}
