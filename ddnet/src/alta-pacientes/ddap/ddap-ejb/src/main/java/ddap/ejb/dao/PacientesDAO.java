package ddap.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import ddap.ejb.PacientesSearchFilter;
import ddap.ejb.entities.Paciente;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class PacientesDAO extends AbstractDAO<Long, Paciente> {

	public PacientesDAO() {
		super(Paciente.class);
	}


	@Override
	public void persist(Paciente pat) {
		super.persist(pat);
	}
	
	@Override
	public void remove(Paciente entity) {
		super.remove(entity);
	}
	
	@Override
	public Collection<Paciente> getAll() {
		// TODO Auto-generated method stub
		return super.getAll();
	}
	
	public Paciente getByPatID(String docNumber){
		
		List<Paciente> result =
				entityManager.createQuery(" SELECT p FROM Paciente p WHERE p.docNumber = :patIDparam ", Paciente.class)
			    			.setParameter("patIDparam", docNumber)
			    			.getResultList();
		
		return !result.isEmpty() ? result.get(0) : null;
	}
	
	public Collection<Paciente> getByName(String name){
		
		Collection<Paciente> result =
				entityManager.createQuery(" SELECT p FROM Paciente p "
									+ 	  " WHERE LOWER(TRIM(BOTH FROM p.lastName)) LIKE :nameParam "
									+ 	  " OR LOWER(TRIM(BOTH FROM p.firstName)) LIKE :nameParam ", Paciente.class)
				
				.setParameter("nameParam", "%"+name.trim().toLowerCase()+"%")
				.getResultList();
		
		return !result.isEmpty() ? result : null ;
	}
	
	public Collection<Paciente> searchPat(String s){
		
		Collection<Paciente> result =
				entityManager.createQuery(" SELECT p FROM Paciente p "
									+ 	  " WHERE LOWER(TRIM(BOTH FROM p.lastName)) LIKE :nameParam "
									+ 	  " OR LOWER(TRIM(BOTH FROM p.firstName)) LIKE :nameParam "
									+ 	  " OR LOWER(TRIM(BOTH FROM p.docNumber)) LIKE :nameParam ", Paciente.class)
				
				.setParameter("nameParam", "%"+s.trim().toLowerCase()+"%")
				.getResultList();
		
		return !result.isEmpty() ? result : null ;
	}
	
	
	public Collection<Paciente> findPacientes( PacientesSearchFilter filter ){
		if( filter == null ) throw new IllegalArgumentException("'filter' can not be null");
		
		String jpql = " SELECT pat FROM Paciente pat WHERE pat.id > 0 ";
		
		if(filter.isSetdocType()) jpql += " AND LOWER(TRIM(BOTH FROM pat.docType)) LIKE :docType ";
		if(filter.isSetDocNumber()) jpql += " AND LOWER(TRIM(BOTH FROM pat.docNumber)) LIKE :docNumber ";
		if(filter.isSetName()) jpql += " AND LOWER(TRIM(BOTH FROM pat.lastName)) LIKE :name "
									+  " OR LOWER(TRIM(BOTH FROM pat.firstName)) LIKE :name ";
		if(filter.isSetPatSex()) jpql += " AND LOWER(TRIM(BOTH FROM pat.patSex)) LIKE :patSex ";
		if(filter.isSetInsertDate()) jpql += " AND LOWER(TRIM(BOTH FROM pat.insertDate)) LIKE :insertDate ";
		
		jpql += " ORDER BY pat.docNumber ASC ";
		
		TypedQuery<Paciente> query = entityManager.createQuery(jpql, Paciente.class);
		
		if(filter.isSetdocType())
			query.setParameter("docType", "%" + filter.getDocType().trim().toLowerCase() + "%");
		if(filter.isSetDocNumber())
			query.setParameter("docNumber", "%" + filter.getDocNumber().trim().toLowerCase() + "%");
		if(filter.isSetName())
			query.setParameter("name", "%" + filter.getName().trim().toLowerCase() + "%");
		if(filter.isSetPatSex())
			query.setParameter("patSex", "%" + filter.getPatSex().trim().toLowerCase() + "%");
		if(filter.isSetInsertDate())
			query.setParameter("insertDate", "%" + filter.getInsertDate().trim().toLowerCase() + "%");
		
		List<Paciente> resultList = query.getResultList();
		return resultList;
	}
}
