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
	List<String> getMethods();
	List<String> getMethods(String name);
	List<String> getMethods(String name, String name1);
	List<String> getMethods(String name, String name1, String name2);
	ReportTemplate setMethods(String name, String name1, String name2);
	ReportTemplate setMethods(String name, String name1, String name2, String name3);
}
