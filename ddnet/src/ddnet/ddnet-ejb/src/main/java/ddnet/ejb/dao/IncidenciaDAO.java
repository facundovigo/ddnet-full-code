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

	public Collection<Incidencia> getByStudyId(long studyId) {
		List<Incidencia> result = 
			entityManager.createQuery("SELECT i FROM Incidencia i WHERE i.studyID= :studyId ORDER BY i.fecha ASC",Incidencia.class)
				.setParameter("studyId",studyId)
				.getResultList();
		return result.isEmpty()? null:result;
	}

	@Override
	public void persist(Incidencia entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}

	public Incidencia getById(Long id){

		List<Incidencia> result = entityManager.createQuery("SELECT i FROM Incidencia i WHERE i.id = :incidenciaID", Incidencia.class)
				.setParameter("incidenciaID", id)
				.getResultList();
		return result == null || result.isEmpty() ? null : result.get(0);
	}
}
