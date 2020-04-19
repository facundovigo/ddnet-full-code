package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.ComprobarCaso;
import ddnet.ejb.entities.Informe;
import ddnet.ejb.entities.SegundaLectura;

@Local
public interface InformeManagerLocal {
	Collection<Informe> getInformebyStudy(Long studyID);
	Collection<ComprobarCaso> getCCbyReport(Long informeID);
	void persist(Informe data);
	void persist(SegundaLectura data);
	void persist(ComprobarCaso data);
}
