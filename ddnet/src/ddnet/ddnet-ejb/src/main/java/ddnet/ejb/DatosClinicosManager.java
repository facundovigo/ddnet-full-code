package ddnet.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.DatosClinicosDAO;
import ddnet.ejb.entities.DatosClinicos;

/**
 * Session Bean implementation class DatosClinicosManager
 */
@Stateless(mappedName = "ejb/ddnet/DatosClinicosManager")
@LocalBean
public class DatosClinicosManager implements DatosClinicosManagerLocal {

	@EJB
	private DatosClinicosDAO dcDAO;
	
	@Override
	public void persist(DatosClinicos data) {
		// TODO Auto-generated method stub
		dcDAO.persist(data); 
	}
}
