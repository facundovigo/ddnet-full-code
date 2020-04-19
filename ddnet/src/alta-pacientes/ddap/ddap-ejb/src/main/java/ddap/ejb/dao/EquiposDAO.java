package ddap.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddap.ejb.entities.Equipo;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class EquiposDAO extends AbstractDAO<Long, Equipo> {

	public EquiposDAO() {
		super(Equipo.class);
	}


	@Override
	public void persist(Equipo e) {
		super.persist(e);
	}
	
	@Override
	public void remove(Equipo e) {
		super.remove(e);
	}
	
	public String getLastCode(){
		
		String code = entityManager.createQuery(" SELECT max(e.id) FROM Equipo e ")
						.getResultList().toString();
		
		return code == null || code.isEmpty() ? "0" : code;
		
	}
	
	@Override
	public Collection<Equipo> getAll() {
		
		return entityManager.createQuery("SELECT e FROM Equipo e ORDER BY code", Equipo.class)
				.getResultList();
	}
	
	public List<String> getModalities(){
		
		@SuppressWarnings("unchecked")
		List<String> result =
				entityManager.createQuery(" SELECT DISTINCT e.mod FROM Equipo e ORDER BY e.mod ")
					.getResultList();
		
		return result.isEmpty() ? null : result;
	}
	
	public Collection<Equipo> getByModality(String mod){
		
		return entityManager.createQuery("SELECT e FROM Equipo e "
								+ " WHERE LOWER(TRIM(BOTH FROM e.mod)) = :modParam", Equipo.class)
					.setParameter("modParam", mod.trim().toLowerCase())
					.getResultList();
	}
	
	public Equipo getByCode(String code){
		
		List<Equipo> result =
				entityManager.createQuery(" SELECT e FROM Equipo e WHERE e.code = :code ",Equipo.class)
					.setParameter("code", code)
					.getResultList();
		
		return result.isEmpty() ? null : result.get(0);
	}
}
