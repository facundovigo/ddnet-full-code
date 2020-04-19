package ddnet.report;

import java.util.Set;

import ddnet.ejb.entities.ReportTemplate;

public interface ReportTemplateProvider {
	Set<String> getTemplateNames();
	Set<String> getTemplateNames(String modality);	
	ReportTemplate getTemplate(String templateName);
	ReportTemplate getTemplate(String modality, String templateName);
	Set<String> getMethods();
	Set<String> getMethods(String name);
	Set<String> getMethods(String name, String name1);
	Set<String> getMethods(String name, String name1, String name2);
	ReportTemplate setMethods(String name, String name1, String name2);
	ReportTemplate setMethods(String name, String name1, String name2, String name3);
}
