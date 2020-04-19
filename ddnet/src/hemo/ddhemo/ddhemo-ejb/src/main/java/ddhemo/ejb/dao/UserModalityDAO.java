package ddhemo.ejb.dao;

import javax.ejb.Stateless;

import ddhemo.ejb.entities.UserModality;
import ddhemo.ejb.util.data.AbstractDAO;


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