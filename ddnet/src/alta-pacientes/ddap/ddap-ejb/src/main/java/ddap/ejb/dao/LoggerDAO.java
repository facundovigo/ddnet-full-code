package ddap.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddap.ejb.entities.Logger;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class LoggerDAO extends AbstractDAO<Long, Logger> {

	public LoggerDAO() {
		super(Logger.class);
	}


	@Override
	public void persist(Logger l) {
		super.persist(l);
	}
	
	@Override
	public void remove(Logger l) {
		super.remove(l);
	}
	
	@Override
	public Collection<Logger> getAll() {
		
		return entityManager.createQuery("SELECT l FROM Logger l ORDER BY l.date DESC", Logger.class)
				.getResultList();
	}
	
}
