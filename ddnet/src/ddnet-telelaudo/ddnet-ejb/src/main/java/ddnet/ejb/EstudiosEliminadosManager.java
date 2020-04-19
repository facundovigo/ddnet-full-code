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
	
	@EJB
	private EstudiosEliminadosDAO dao;
	
	@Override
	public void persist(EstudiosEliminados c) {
		
		dao.persist(c);
	}
	
	@Override
	public Collection<EstudiosEliminados> getAll() {
		// TODO Auto-generated method stub
		return dao.getAll();
	}
}
