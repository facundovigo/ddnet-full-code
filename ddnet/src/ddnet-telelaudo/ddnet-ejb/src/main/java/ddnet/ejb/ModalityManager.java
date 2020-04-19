package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.ModalityDAO;
import ddnet.ejb.entities.Modality;

/**
 * Session Bean implementation class ModalityManager
 */
@Stateless(mappedName = "ejb/ddnet/ModalityManager")
@LocalBean
public class ModalityManager implements ModalityManagerLocal {

	@EJB
	private ModalityDAO moddao;
	
    @Override
    public Collection<Modality> getAll() {
    	return moddao.getAll();
    }

    @Override
    public Modality findByName(String name) {
    	// TODO Auto-generated method stub
    	return moddao.findByName(name); 
    }

}