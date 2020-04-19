package ddnet.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddnet.ejb.dao.ModalityDAO;
import ddnet.ejb.entities.Configuration;
import ddnet.ejb.entities.ReportTemplate;
import ddnet.report.ReportTemplateProvider;
import ddnet.report.providers.FolderReportTemplatesProvider;

/**
 * Session Bean implementation class ReportManager
 */
@Stateless(mappedName = "ejb/ddnet/ReportManager")
@LocalBean
public class ReportManager implements ReportManagerLocal {

	@EJB
	private ConfigurationManager configurationManager;
		
	@EJB
	private ModalityDAO modalityDAO;
	
	// TODO: Utilizar DI para obtener la instancia correcta de este provider.
	private ReportTemplateProvider templateProvider;

	@PostConstruct
	public void init() {
		Configuration config = configurationManager.getConfigurationItem("report.templates.source-path");
		templateProvider = new FolderReportTemplatesProvider(config.getValue());
	}
	
	public List<String> getTemplateNames() {
		List<String> templateNames = new ArrayList<String>(templateProvider.getTemplateNames());
		Collections.sort(templateNames);
		return templateNames;
	}
	
	public List<String> getMethods() {
		
		List<String> methodNames = new ArrayList<String>(templateProvider.getMethods());
		Collections.sort(methodNames);
		return methodNames;
	}
	
	public List<String> getMethods(String name){
		
		List<String> methodNames = new ArrayList<String>(templateProvider.getMethods(name));
		Collections.sort(methodNames);
		return methodNames;
	}
	
	public List<String> getMethods(String name, String name1){
		
		List<String> methodNames = new ArrayList<String>(templateProvider.getMethods(name, name1));
		Collections.sort(methodNames);
		return methodNames;
	}
	
	public List<String> getMethods(String name, String name1, String name2) {
		
		List<String> methodNames = new ArrayList<String>(templateProvider.getMethods(name, name1, name2));
		Collections.sort(methodNames);
		return methodNames;
	}
	
	public ReportTemplate setMethods(String name, String name1, String name2){
		return templateProvider.setMethods(name, name1, name2);
	}
	
	public ReportTemplate setMethods(String name, String name1, String name2, String name3){
		return templateProvider.setMethods(name, name1, name2, name3);
	}

	public List<String> getTemplateNames(String modality) {
		List<String> templateNames = new ArrayList<String>();
		// Para evitar cualquier tema de seguridad, validamos el input del usuario.
		if (modalityDAO.findByName(modality) != null) 		
			templateNames = new ArrayList<String>(templateProvider.getTemplateNames(modality));
		Collections.sort(templateNames);
		return templateNames;
	}
	
	public ReportTemplate getTemplate(String templateName) {
		return templateProvider.getTemplate(templateName);
	}
	
	public ReportTemplate getTemplate(String modality, String templateName) {
		if (modalityDAO.findByName(modality) != null) 		
			return templateProvider.getTemplate(modality, templateName);
		return null;
	}	
	
}
