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
	public Collection<Incidencia> getByStudyId(long studyId) {
		// TODO Auto-generated method stub
		return incidenciaDAO.getByStudyId(studyId);
	}
	
	@Override
	public void persist(Incidencia data) {
		// TODO Auto-generated method stub
		incidenciaDAO.persist(data);
	}
	
	@Override
	public Incidencia getById(Long id) {
		
		return incidenciaDAO.getById(id);
	}
}
