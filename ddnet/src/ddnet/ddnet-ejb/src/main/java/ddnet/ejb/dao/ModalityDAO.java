package ddnet.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddnet.ejb.entities.Modality;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class ModalityDAO extends AbstractDAO<Long, Modality> {

	public ModalityDAO() {
		super(Modality.class);
	}

	public Modality findByName(String name) {
		List<Modality> result = 
				entityManager.createQuery("SELECT m FROM Modality m WHERE m.name = :name", Modality.class)
				.setParameter("name", name)
				.getResultList();
		
		if (result != null && !result.isEmpty())
			return result.get(0);
		
		return null;
	}
	
	@Override
	public Collection<Modality> getAll() {
		
		return entityManager.createQuery("SELECT m FROM Modality m ORDER BY m.name", Modality.class)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getNotCreatedModality() {
		
		return entityManager.createQuery("SELECT DISTINCT modalities FROM LegacyStudy WHERE modalities NOT IN "
											+ "( SELECT name FROM Modality )")
				.getResultList();
		
	}
	
	@Override
	public void persist(Modality entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
	
}
