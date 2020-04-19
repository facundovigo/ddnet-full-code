package ddnet.ejb.dao;

import javax.ejb.Stateless;

import ddnet.ejb.entities.EnvioCorreo;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class EnvioCorreoDAO extends AbstractDAO<Long, EnvioCorreo> {

	public EnvioCorreoDAO() {
		super(EnvioCorreo.class);
	}
	
	@Override
	public void persist(EnvioCorreo entity) {
		
		super.persist(entity);
	}
	
}
