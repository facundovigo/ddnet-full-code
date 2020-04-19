package ddap.ejb;

import javax.ejb.Local;

import ddap.ejb.entities.Privilegio;

@Local
public interface PrivilegiosManagerLocal {
	void persist(Privilegio p);
	void remove(Privilegio p);
}
