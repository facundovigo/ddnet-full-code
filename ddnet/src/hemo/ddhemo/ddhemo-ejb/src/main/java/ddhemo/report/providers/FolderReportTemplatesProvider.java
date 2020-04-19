package ddhemo.report.providers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import ddhemo.ejb.entities.ReportTemplate;
import ddhemo.report.ReportTemplateProvider;

public class FolderReportTemplatesProvider implements ReportTemplateProvider {
	
	private static final String TEMPLATES_EXTENSION = ".txt";
	private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");
	
	private final String sourcePath;
	
	public FolderReportTemplatesProvider(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	@Override
	public Set<String> getTemplateNames() {
		return getTemplatesOnDirectory(sourcePath);
	}

	@Override
	public Set<String> getTemplateNames(String modality) {
		try {
			return getTemplatesOnDirectory(new File(sourcePath, modality).getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ReportTemplate getTemplate(String templateName) {
		return getTemplateByFile(FileUtils.getFile(this.sourcePath, templateName + TEMPLATES_EXTENSION));
	}

	public ReportTemplate getTemplate(String modality, String templateName) {		
		return getTemplateByFile(FileUtils.getFile(this.sourcePath, modality, templateName + TEMPLATES_EXTENSION));
	}

	private ReportTemplate getTemplateByFile(File templateFile) {
		try {
			if (!templateFile.getCanonicalPath().startsWith(new File(this.sourcePath).getCanonicalPath()))
				throw new RuntimeException("Invalid templateName: " + templateFile);
			
			ReportTemplate template = new ReportTemplate();
			template.setName(templateFile.getName());		
			template.setBody(FileUtils.readFileToString(templateFile, DEFAULT_ENCODING));
			
			return template;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	private Set<String> getTemplatesOnDirectory(String directory) {
		Set<String> templateNames = new HashSet<String>();
		File source = new File(directory);

		if (source.exists() && source.isDirectory())
			for(String filename : source.list(new SuffixFileFilter(TEMPLATES_EXTENSION)))
				templateNames.add(FilenameUtils.getBaseName(filename));
		
		return templateNames;
	}	
}
