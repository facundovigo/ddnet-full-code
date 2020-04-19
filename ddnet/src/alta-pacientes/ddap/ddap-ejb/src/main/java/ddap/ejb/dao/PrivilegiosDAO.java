package ddap.ejb.dao;

import javax.ejb.Stateless;

import ddap.ejb.entities.Privilegio;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class PrivilegiosDAO extends AbstractDAO<Long, Privilegio> {

	public PrivilegiosDAO() {
		super(Privilegio.class);
	}
	@Override public void persist(Privilegio p){ super.persist(p); }
	@Override public void remove(Privilegio p){ super.remove(p); }
}
