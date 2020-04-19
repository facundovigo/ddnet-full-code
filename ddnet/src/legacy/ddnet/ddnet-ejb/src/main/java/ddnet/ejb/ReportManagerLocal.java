package ddnet.ejb;

import java.util.List;

import javax.ejb.Local;

import ddnet.ejb.entities.ReportTemplate;

@Local
public interface ReportManagerLocal {
	List<String> getTemplateNames();
	List<String> getTemplateNames(String modality);
	ReportTemplate getTemplate(String templateName);	
	ReportTemplate getTemplate(String modality, String templateName);
}
