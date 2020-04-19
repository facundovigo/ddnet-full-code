package ddhemo.report;

import java.util.Set;

import ddhemo.ejb.entities.ReportTemplate;

public interface ReportTemplateProvider {
	Set<String> getTemplateNames();
	Set<String> getTemplateNames(String modality);	
	ReportTemplate getTemplate(String templateName);
	ReportTemplate getTemplate(String modality, String templateName);
}
