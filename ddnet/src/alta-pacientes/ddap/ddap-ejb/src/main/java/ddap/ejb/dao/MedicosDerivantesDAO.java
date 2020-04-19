package ddap.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddap.ejb.entities.MedicoDerivante;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class MedicosDerivantesDAO extends AbstractDAO<Long, MedicoDerivante> {

	public MedicosDerivantesDAO() { super(MedicoDerivante.class); }

	@Override public void persist(MedicoDerivante m) { super.persist(m); }
	@Override public void remove(MedicoDerivante m) { super.remove(m); }
	@Override public Collection<MedicoDerivante> getAll() { return super.getAll(); }
}
