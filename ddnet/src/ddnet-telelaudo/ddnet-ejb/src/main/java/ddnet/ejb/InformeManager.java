package ddnet.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.ComprobarCasoDAO;
import ddnet.ejb.dao.InformeDAO;
import ddnet.ejb.dao.SegundaLecturaDAO;
import ddnet.ejb.entities.ComprobarCaso;
import ddnet.ejb.entities.Informe;
import ddnet.ejb.entities.SegundaLectura;

/**
 * Session Bean implementation class StudyLogManager
 */
@Stateless(mappedName = "ejb/ddnet/InformeManager")
@LocalBean
public class InformeManager implements InformeManagerLocal{
	
	@EJB
	private InformeDAO infDAO;
	@EJB
	private SegundaLecturaDAO secReadDAO;
	@EJB
	private ComprobarCasoDAO checkDAO;
	
	@Override
	public Collection<Informe> getInformebyStudy(Long studyID) {
		
		return infDAO.getInformebyStudy(studyID);
	}
	
	@Override
	public void persist(Informe data) {
		
		infDAO.persist(data);
	}
	@Override
	public void persist(SegundaLectura data) {
		
		secReadDAO.persist(data);
	}
	@Override
	public void persist(ComprobarCaso data) {
		
		checkDAO.persist(data);
	}
	@Override
	public Collection<ComprobarCaso> getCCbyReport(Long informeID) {
		
		return checkDAO.getCCbyReport(informeID);
	}
}
