package ddnet.ejb;

import javax.ejb.Local;

import ddnet.ejb.entities.*;

@Local
public interface EnvioCorreoManagerLocal {
	
	void persist(EnvioCorreo c);
}
