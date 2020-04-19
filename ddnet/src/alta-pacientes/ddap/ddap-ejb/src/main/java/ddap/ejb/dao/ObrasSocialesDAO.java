package ddap.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddap.ejb.entities.ObraSocial;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class ObrasSocialesDAO extends AbstractDAO<Long, ObraSocial> {

	public ObrasSocialesDAO() {
		super(ObraSocial.class);
	}


	@Override
	public void persist(ObraSocial os) {
		super.persist(os);
	}
	
	@Override
	public void remove(ObraSocial os) {
		super.remove(os);
	}
	
	public String getLastCode(){
		
		String code = entityManager.createQuery(" SELECT max(os.id) FROM ObraSocial os ")
						.getResultList().toString();
		
		return code == null || code.isEmpty() ? "0" : code;
		
	}
	
	@Override
	public Collection<ObraSocial> getAll() {
		
		return entityManager.createQuery("SELECT os FROM ObraSocial os ORDER BY code", ObraSocial.class)
				.getResultList();
	}
	
}
