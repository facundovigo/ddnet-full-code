package ddnet.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddnet.ejb.entities.EstudiosEliminados;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class EstudiosEliminadosDAO extends AbstractDAO<Long, EstudiosEliminados> {

	public EstudiosEliminadosDAO() {
		super(EstudiosEliminados.class);
	}
	
	@Override
	public void persist(EstudiosEliminados entity) {
		
		super.persist(entity);
	}
	
	public Collection<EstudiosEliminados> getAll(){
		
		return entityManager.createQuery("SELECT e FROM EstudiosEliminados e ORDER BY e.deletedDate DESC"
				,EstudiosEliminados.class)
				.getResultList();
	}
}
