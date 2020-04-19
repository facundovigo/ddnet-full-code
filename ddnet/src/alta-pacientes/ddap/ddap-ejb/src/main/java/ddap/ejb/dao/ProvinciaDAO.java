package ddap.ejb.dao;

import java.util.Collection;

import javax.ejb.Stateless;

import ddap.ejb.entities.Provincia;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class ProvinciaDAO extends AbstractDAO<Long, Provincia> {
	public ProvinciaDAO() {
		super(Provincia.class);
	}
	
	@Override
	public Collection<Provincia> getAll() {
		// TODO Auto-generated method stub
		return super.getAll();
	}
	
	@Override
	public void persist(Provincia entity) {
		// TODO Auto-generated method stub
		super.persist(entity);
	}
	
	@Override
	public void remove(Provincia entity) {
		// TODO Auto-generated method stub
		super.remove(entity);
	}
}
