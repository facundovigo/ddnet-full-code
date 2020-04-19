package ddnet.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.PasswordRestore;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class PasswordRestoreDAO extends AbstractDAO<Long, PasswordRestore> {

	public PasswordRestoreDAO() {
		super(PasswordRestore.class);
	}
	
	@Override
	public void persist(PasswordRestore entity) {
		
		super.persist(entity);
	}
	
	public PasswordRestore getByKey(String key){
		
		List<PasswordRestore> result = 
		
				entityManager.createQuery("SELECT pr FROM PasswordRestore pr "
										+	" WHERE pr.key = :keyParam ", PasswordRestore.class)
				.setParameter("keyParam", key)
				.getResultList();
		
		if(result != null && !result.isEmpty())
			return result.get(0);
		
		return null;
	}
}
