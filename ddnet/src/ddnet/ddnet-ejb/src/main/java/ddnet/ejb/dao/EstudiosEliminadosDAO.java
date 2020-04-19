package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;

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
	@Override
	public void remove(EstudiosEliminados entity) {
		super.remove(entity);
	}
	
	public Collection<EstudiosEliminados> getAll(){
		List<EstudiosEliminados> result= 
			entityManager.createQuery("SELECT e FROM EstudiosEliminados e ORDER BY e.deletedDate DESC",EstudiosEliminados.class)
			.getResultList();
		return result.isEmpty()? null:result;
	}
	@Override
	public EstudiosEliminados findById(Long id) {
		return super.findById(id);
	}
	public EstudiosEliminados getByStudyId(Long studyId){
		List<EstudiosEliminados> result=
			entityManager.createQuery("SELECT del FROM EstudiosEliminados del WHERE del.studyID= :paramStudyId",EstudiosEliminados.class)
			.setParameter("paramStudyId", studyId)
			.getResultList();
		return result.isEmpty()? null:result.get(0);
	}
}
