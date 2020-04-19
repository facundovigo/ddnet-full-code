package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.entities.Incidencia;
import ddnet.ejb.dao.IncidenciaDAO;

/**
 * Session Bean implementation class IncidenciaManager
 */
@Stateless(mappedName = "ejb/ddnet/IncidenciaManager")
@LocalBean
public class IncidenciaManager implements IncidenciaManagerLocal{
	
	@EJB
	private IncidenciaDAO incidenciaDAO;
	
	@Override
	public Collection<Incidencia> getAll(long studyID) {
		// TODO Auto-generated method stub
		return incidenciaDAO.getAll(studyID);
	}
	
	@Override
	public void persist(Incidencia data) {
		// TODO Auto-generated method stub
		incidenciaDAO.persist(data);
	}
}
