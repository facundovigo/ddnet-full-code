package ddap.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddap.ejb.dao.LoggerDAO;
import ddap.ejb.entities.Logger;

/**
 * Session Bean implementation class LoggerManager
 */
@Stateless(mappedName = "ejb/ddap/LoggerManager")
@LocalBean
public class LoggerManager implements LoggerManagerLocal {

	@EJB
	private LoggerDAO lDAO;
	
    
	@Override
	public void persist(Logger l) {
		lDAO.persist(l);	
	}
	@Override
	public void remove(Logger l) {
		lDAO.remove(l);
	}
	
	@Override
	public Collection<Logger> getAll() {
		
		return lDAO.getAll();
	}
}
