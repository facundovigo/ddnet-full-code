package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.MedicoDAO;
import ddnet.ejb.entities.Medico;

/**
 * Session Bean implementation class MedicoManager
 */
@Stateless(mappedName = "ejb/ddnet/MedicoManager")
@LocalBean
public class MedicoManager implements MedicoManagerLocal {

	@EJB
	private MedicoDAO medicoDAO;

	public Collection<Medico> getAll() {
		return medicoDAO.getAll();
	}
	
	@Override
	public Collection<Medico> getOnCall() {
		
		return medicoDAO.getOnCall();
	}
	
	@Override
	public Medico getByName(String name) {
		
		return medicoDAO.getByName(name);
	}
	
	@Override
	public void persist(Medico dr) {
		
		medicoDAO.persist(dr);
	}
	
	@Override
	public void remove(Medico dr) {
		
		medicoDAO.remove(dr);
	}
	
}