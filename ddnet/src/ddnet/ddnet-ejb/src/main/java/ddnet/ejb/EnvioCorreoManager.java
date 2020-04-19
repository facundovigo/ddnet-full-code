package ddnet.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.*;
import ddnet.ejb.entities.*;


/**
 * Session Bean implementation class EnvioCorreoManager
 */
@Stateless(mappedName = "ejb/ddnet/EnvioCorreoManager")
@LocalBean
public class EnvioCorreoManager implements EnvioCorreoManagerLocal {
	
	@EJB
	private EnvioCorreoDAO dao;
	
	@Override
	public void persist(EnvioCorreo c) {
		
		dao.persist(c);
	}
}
