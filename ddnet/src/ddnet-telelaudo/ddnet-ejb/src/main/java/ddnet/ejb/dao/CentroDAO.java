package ddnet.ejb.dao;

import javax.ejb.Stateless;

import ddnet.ejb.entities.Centro;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class CentroDAO extends AbstractDAO<Long, Centro> {
	public CentroDAO() {
		super(Centro.class);
	}
	
	@Override
	public void persist(Centro entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
	
	@Override
	public void remove(Centro entity) {
		// TODO Auto-generated method stub
		super.remove(entity);
	}
}
