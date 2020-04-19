package ddap.ejb.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import ddap.ejb.entities.Worklist;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class WorklistDAO extends AbstractDAO<Long, Worklist> {

	public WorklistDAO() {
		super(Worklist.class);
	}


	@Override
	public void persist(Worklist w) {
		super.persist(w);
	}
	
	@Override
	public void remove(Worklist w) {
		super.remove(w);
	}
	
	public String getLastAccessionNumber(){
		
		String code = entityManager.createQuery(" SELECT max(wl.accNumb) FROM Worklist wl ")
				.getResultList().toString();
		
		if(code != null && !code.equals("")) return code.replace("[","").replace("]","");
		else return null;
	}
	
	@Override
	public Collection<Worklist> getAll(){ return super.getAll(); }
	
	public Worklist getByAccessionNumber(String accNumber){
		List<Worklist> result=
			entityManager.createQuery("SELECT wl FROM Worklist wl WHERE wl.accNumb= :accNumber",Worklist.class)
			.setParameter("accNumber", accNumber)
			.getResultList();
		return result.isEmpty()? null: result.get(0);
	}
	
}
