package ddnet.ejb.dao;

import javax.ejb.Stateless;

import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.util.data.AbstractDAO;

@Stateless
public class LegacyStudyDAO extends AbstractDAO<Long, LegacyStudy> {

	public LegacyStudyDAO() {
		super(LegacyStudy.class);
	}
	
	@Override
	public void remove(LegacyStudy entity) {
		// TODO Auto-generated method stub
		super.remove(entity);
	}
	
}
