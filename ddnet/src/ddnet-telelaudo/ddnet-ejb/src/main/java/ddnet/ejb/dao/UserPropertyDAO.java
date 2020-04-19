package ddnet.ejb.dao;

import javax.ejb.Stateless;

import ddnet.ejb.entities.UserProperty;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class UserPropertyDAO extends AbstractDAO<Long, UserProperty> {

	public UserPropertyDAO() {
		super(UserProperty.class);
	}
	
	@Override
	public void persist(UserProperty entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
}
