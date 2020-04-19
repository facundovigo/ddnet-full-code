package ddap.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import ddap.ejb.PracticasSearchFilter;
import ddap.ejb.entities.Practica;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class PracticasDAO extends AbstractDAO<Long, Practica> {

	public PracticasDAO() {
		super(Practica.class);
	}


	@Override
	public void persist(Practica p) {
		super.persist(p);
	}
	
	@Override
	public void remove(Practica p) {
		super.remove(p);
	}
	
	public String getLastCode(){
		
		String code = entityManager.createQuery(" SELECT max(p.id) FROM Practica p ")
						.getResultList().toString();
		
		return code == null || code.isEmpty() ? "0" : code;
		
	}
	
	@Override
	public Collection<Practica> getAll() {
		
		return entityManager.createQuery("SELECT p FROM Practica p ORDER BY p.prestacion", Practica.class)
				.getResultList();
	}
	
	public Collection<Practica> getByModality(String mod){
		
		return entityManager.createQuery("SELECT p FROM Practica p "
								+ " WHERE LOWER(TRIM(BOTH FROM p.modalidad)) = :modParam "
								+ " ORDER BY p.prestacion ", Practica.class)
					.setParameter("modParam", mod.trim().toLowerCase())
					.getResultList();
	}
	
	public List<String> getModalities(){
		
		@SuppressWarnings("unchecked")
		List<String> result =
				entityManager.createQuery(" SELECT DISTINCT p.modalidad FROM Practica p ORDER BY p.modalidad ")
					.getResultList();
		
		return result.isEmpty() ? null : result;
	}
	
	@Override
	public Practica findById(Long id) {
		
		return super.findById(id);
	}
	
	
	
	
	
	
	public List<Practica> findPracticas( PracticasSearchFilter filter ){
		
		if( filter == null ) throw new IllegalArgumentException("'filter' can not be null");
		
		String jpql = " SELECT p FROM Practica p WHERE p.id > 0 ";
		
		if( filter.isSetModality() ) jpql += " AND LOWER(TRIM(BOTH FROM p.modalidad)) LIKE :modality ";
		if( filter.isSetName() ) jpql += " AND LOWER(TRIM(BOTH FROM p.nombre)) LIKE :name "
									  +  " OR LOWER(TRIM(BOTH FROM p.interno)) LIKE :name "
									  +  " OR LOWER(TRIM(BOTH FROM p.abreviado)) LIKE :name ";
		if( filter.isSetCapability() ) jpql += " AND LOWER(TRIM(BOTH FROM p.prestacion)) LIKE :capability ";
		if( filter.isSetRegion() ) jpql += " AND p.region = :region ";
		if( filter.isSetNeedReport() ) {
			if(filter.getNeedReport().equals("si")) jpql += " AND p.requiereInforme = TRUE ";
			else if(filter.getNeedReport().equals("no")) jpql += " AND p.requiereInforme = FALSE ";
		}
		jpql += " ORDER BY p.prestacion ASC ";
		
		TypedQuery<Practica> query = entityManager.createQuery(jpql, Practica.class);
		
		if( filter.isSetModality())
			query.setParameter("modality", "%" + filter.getModality().trim().toLowerCase() + "%");
		if( filter.isSetName())
			query.setParameter("name", "%" + filter.getName().trim().toLowerCase() + "%");
		if( filter.isSetCapability())
			query.setParameter("capability", "%" + filter.getCapability().trim().toLowerCase() + "%");
		if( filter.isSetRegion())
			query.setParameter("region", filter.getRegion());
		
		List<Practica> resultList = query.getResultList();
		return resultList;
	}
	
	
	
}
