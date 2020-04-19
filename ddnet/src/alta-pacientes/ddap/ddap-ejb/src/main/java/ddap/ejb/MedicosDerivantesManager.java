package ddap.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.MedicosDerivantesDAO;
import ddap.ejb.entities.MedicoDerivante;

/**
 * Session Bean implementation class MedicosDerivantesManager
 */
@Stateless(mappedName= "ejb/ddap/MedicosDerivantesManager")
@LocalBean
public class MedicosDerivantesManager implements MedicosDerivantesManagerLocal {

	@EJB private MedicosDerivantesDAO mDAO;
	
    @Override public void persist(MedicoDerivante m) { mDAO.persist(m);	}
	@Override public void remove(MedicoDerivante m) { mDAO.remove(m); }
	@Override public Collection<MedicoDerivante> getAll() { return mDAO.getAll(); }
}
