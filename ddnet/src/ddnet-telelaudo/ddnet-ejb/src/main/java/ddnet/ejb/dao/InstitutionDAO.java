package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.Institution;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class InstitutionDAO extends AbstractDAO<Long, Institution> {
	public InstitutionDAO() {
		super(Institution.class);
	}
		
	@Override
	public Collection<Institution> getAll() {
		return entityManager.createQuery("SELECT i FROM Institution i " +
					" WHERE i.deleted = false AND administrativelyEnabled = true " + 
					" ORDER BY i.name", 
				Institution.class).getResultList();
	}
	
	@Override
	public Institution findById(Long id) {
		// TODO Auto-generated method stub
		return entityManager.createQuery("SELECT i FROM Institution i WHERE i.id = :id ", Institution.class)
				.setParameter("id", id) 
				.getResultList().get(0);
	}
	
	@Override
	public void persist(Institution entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getNotCreatedInstitution() {
		
		return entityManager.createQuery("SELECT DISTINCT custom3 FROM LegacyStudy WHERE custom3 NOT IN "
											+ "( SELECT relatedAET FROM Institution )")
				.getResultList();
		
	}
}
