package ddnet.report.providers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import ddnet.ejb.entities.ReportTemplate;
import ddnet.report.ReportTemplateProvider;

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
	public Set<String> getMethods() {
		return getMethodsonDirectory(sourcePath);
	}
	
	@Override
	public Set<String> getMethods(String name) {
		try {
			return getMethodsonDirectory(new File(sourcePath, name).getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Set<String> getMethods(String name, String name1) {
		try {
			File f = new File(sourcePath, name);
			return getMethodsonDirectory(new File(f, name1).getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Set<String> getMethods(String name, String name1, String name2) {
		
		try {
			File f = new File(sourcePath, name);
			File f1 = new File(f, name1);
			return getMethodsonDirectory(new File(f1, name2).getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ReportTemplate setMethods(String name, String name1, String name2) {
		
		return getTemplateByFile(FileUtils.getFile(this.sourcePath, name, name1, name2));
	}
	
	@Override
	public ReportTemplate setMethods(String name, String name1, String name2,
			String name3) {
		return getTemplateByFile(FileUtils.getFile(this.sourcePath, name, name1, name2, name3)); 
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
	
	
	private Set<String> getMethodsonDirectory(String directory) {
		
		Set<String> methodNames = new HashSet<String>();
		File source = new File(directory);
		
		//listarArchivos(source, methodNames);
		
		if (source.exists() && source.isDirectory()) {
			
			File[] f = source.listFiles();
			for( int i = 0 ; i < f.length ; i++ ) methodNames.add(f[i].getName());
		}
		
		return methodNames;
	}
	
	public void listarArchivos(File arch, Set<String> str){
		
		File[] archs = arch.listFiles();
		
		for(File f : archs){
			
			if(f.isDirectory()) listarArchivos(f, str);
			
			String s = f.getParent().replace('\\', '/');
			str.add(s.substring(s.lastIndexOf("/")+1) + ") " + f.getName());
		}
	}
	
	
	
	
}
