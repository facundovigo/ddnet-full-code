package ddnet.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddnet.ejb.entities.ComprobarCaso;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class ComprobarCasoDAO extends AbstractDAO<Long, ComprobarCaso> {

	public ComprobarCasoDAO() {
		super(ComprobarCaso.class);
	}
	
	@Override
	public void persist(ComprobarCaso entity) {
		
		super.persist(entity);
	}
	
	public Collection<ComprobarCaso> getCCbyReport(Long informeID){
		
		return entityManager.createQuery(" SELECT cc FROM ComprobarCaso cc WHERE cc.informeID = :informeID "
				+	" ORDER BY cc.date ASC ", ComprobarCaso.class)
					.setParameter("informeID", informeID)
					.getResultList();
	}
}
