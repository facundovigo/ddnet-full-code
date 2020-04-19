package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.*;
import ddnet.ejb.entities.*;


/**
 * Session Bean implementation class EstudiosEliminadosManager
 */
@Stateless(mappedName = "ejb/ddnet/EstudiosEliminadosManager")
@LocalBean
public class EstudiosEliminadosManager implements EstudiosEliminadosManagerLocal {
	
	@EJB private EstudiosEliminadosDAO dao;
	
	@Override
	public void persist(EstudiosEliminados deleted) {	
		dao.persist(deleted);
	}
	@Override
	public void remove(EstudiosEliminados deleted) {
		dao.remove(deleted);
	}
	@Override
	public Collection<EstudiosEliminados> getAll() {
		return dao.getAll();
	}
	@Override
	public EstudiosEliminados findById(Long id) {
		return dao.findById(id);
	}
	@Override
	public EstudiosEliminados getByStudyId(Long studyId) {
		return dao.getByStudyId(studyId);
	}
}
