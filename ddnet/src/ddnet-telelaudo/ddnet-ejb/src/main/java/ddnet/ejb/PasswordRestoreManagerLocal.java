package ddnet.ejb;

import javax.ejb.Local;

import ddnet.ejb.entities.PasswordRestore;

@Local
public interface PasswordRestoreManagerLocal {
	
	void persist(PasswordRestore data);
	PasswordRestore getByKey(String key);
}