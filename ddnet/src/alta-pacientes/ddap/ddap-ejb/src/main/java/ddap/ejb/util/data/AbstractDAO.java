package ddap.ejb.util.data;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class AbstractDAO<K, E> implements DAO<K, E> {
		protected Class<E> entityClass;
	 
		@PersistenceContext(unitName="ddap")
		protected EntityManager entityManager;
	 
		public AbstractDAO(Class<E> entityClass) {
			this.entityClass = entityClass;
		}
	 
		public void persist(E entity) { entityManager.persist(entity); }
		
		public void merge(E entity) { entityManager.merge(entity); }
				
		public void remove(E entity) { entityManager.remove(entity); }
	 
		public E findById(K id) { return entityManager.find(entityClass, id); }
		
		public Collection<E> getAll() { 
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<E> cq = cb.createQuery(entityClass);
			Root<E> rootEntry = cq.from(entityClass);
			CriteriaQuery<E> all = cq.select(rootEntry);
			TypedQuery<E> allQuery = entityManager.createQuery(all);
			return allQuery.getResultList();			
		}
	}