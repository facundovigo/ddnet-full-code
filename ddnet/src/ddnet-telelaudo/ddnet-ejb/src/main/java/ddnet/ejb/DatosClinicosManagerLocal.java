package ddnet.ejb;

import javax.ejb.Local;

import ddnet.ejb.entities.DatosClinicos;

@Local
public interface DatosClinicosManagerLocal {
	
	void persist(DatosClinicos data);
}
