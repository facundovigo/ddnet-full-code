package ddnet.ejb.dao;

import javax.ejb.Stateless;

import ddnet.ejb.entities.DatosClinicos;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class DatosClinicosDAO extends AbstractDAO<Long, DatosClinicos> {
	
	public DatosClinicosDAO() {
		super(DatosClinicos.class);
	}
	
	@Override
	public void persist(DatosClinicos entity) {
		// TODO Auto-generated method stub
		
		super.persist(entity);
	}
	
}
