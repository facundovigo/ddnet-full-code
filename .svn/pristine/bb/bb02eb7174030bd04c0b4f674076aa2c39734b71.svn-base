package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.Incidencia;
import ddnet.ejb.util.data.AbstractDAO;


@Stateless
public class IncidenciaDAO extends AbstractDAO<Long, Incidencia> {
	
	public IncidenciaDAO() {
		super(Incidencia.class);
	}
	
	public Collection<Incidencia> getAll(long studyID) {
		
		List<Incidencia> result = entityManager.createQuery("SELECT i FROM Incidencia i WHERE i.studyID = :studyID", Incidencia.class)
				.setParameter("studyID", studyID)
				.getResultList();
				
				return result;
	}
	
	@Override
	public void persist(Incidencia entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
}
