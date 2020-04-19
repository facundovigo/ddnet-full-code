package ddnet.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddnet.ejb.entities.Informe;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class InformeDAO extends AbstractDAO<Long, Informe> {

	public InformeDAO() {
		super(Informe.class);
	}
	
	public Collection<Informe> getInformebyStudy(Long studyID) {
		
		return entityManager.createQuery("SELECT i FROM Informe i WHERE i.studyID = :studyID ", Informe.class)
					.setParameter("studyID", studyID)
					.getResultList();
	}
	
	@Override
	public void persist(Informe entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
}
