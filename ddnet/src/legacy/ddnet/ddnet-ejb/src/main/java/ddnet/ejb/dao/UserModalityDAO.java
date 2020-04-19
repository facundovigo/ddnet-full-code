package ddnet.ejb.dao;

import javax.ejb.Stateless;

import ddnet.ejb.entities.UserModality;
import ddnet.ejb.util.data.AbstractDAO;


@Stateless
public class UserModalityDAO extends AbstractDAO<Long, UserModality> {
	
	public UserModalityDAO() {
		super(UserModality.class);
	}
	
	@Override
	public void persist(UserModality userMod) {
		// TODO Auto-generated method stub
		super.persist(userMod);
	}
}