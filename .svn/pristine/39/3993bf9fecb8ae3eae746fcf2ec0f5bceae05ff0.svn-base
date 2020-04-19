package ddnet.report;

import java.util.Set;

import ddnet.ejb.entities.ReportTemplate;

public interface ReportTemplateProvider {
	Set<String> getTemplateNames();
	Set<String> getTemplateNames(String modality);	
	ReportTemplate getTemplate(String templateName);
	ReportTemplate getTemplate(String modality, String templateName);
}
