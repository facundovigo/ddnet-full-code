package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.UserStudy;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class UserStudyDAO extends AbstractDAO<Long, UserStudy> {

	public UserStudyDAO() {
		super(UserStudy.class);
	}
	
	public Collection<UserStudy> getUserStudies(String login) {
		List<UserStudy> result = 
				entityManager.createQuery("SELECT us FROM UserStudy us WHERE us.user.login = :loginParam", UserStudy.class)
				.setParameter("loginParam", login)
				.getResultList();
		return result;
	}
}
