package ddnet.useragent;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;


import javax.swing.JOptionPane;

/*
import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.UnavailableServiceException;
*/
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import ddnet.useragent.audio.Recording;
import ddnet.useragent.ui.DownloadImagesForm;
import ddnet.useragent.ui.DownloadImagesModel;
import ddnet.useragent.ui.Form;
import ddnet.useragent.ui.RecordingForm;
import ddnet.useragent.ui.RecordingModel;
import ddnet.useragent.util.DebugUserAgentWebSocketHandler;

public class App /* implements SingleInstanceListener */ {
	public static final int VERSION_MAJOR = 1;
	public static final int VERSION_MINOR = 0;
	public static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR;
	
	private static final Logger log = Logger.getLogger(App.class);
	private static final App app = new App();

	private final File rootAppFolder = new File(System.getProperty("user.home"), ".ddnet-useragent");
	private final File logFile = new File(rootAppFolder, "log");
	private final File propertiesFile = new File(rootAppFolder, "config");
	private final File downloadsCacheFolder = new File(rootAppFolder, "cache");
	
	private UserAgent userAgent;
	private DownloadImagesForm downloadsForm;
	private Properties properties;

	/**
	 * Uso este flag para evitar configurar JNLP ({@link #configureJNLP()}).
	 * Útil para correr la app directo desde Eclipse, por ejemplo. 
	 */
	private final boolean avoidJNLP = System.getProperty("avoidJNLP") != null;
	
    public static void main(String[] args) throws IOException  {
    	app.start();
    	
    }

    public static App getInstance() {
    	return app;
    }
   
    public void start() {
    	configureSecurity();
    
//    	if (!avoidJNLP)
//    		configureJNLP();
    	
    	ensureAppFolders();    	
    	configureLogging();
    	loadProperties();
		
    	createUserAgentInstance();

    	app.getUserAgent().start();
		configureDownloadsWindow();
    	
		// PRUEBA GRABADORA
		showDownloadsWindow();
		
		//JOptionPane.showMessageDialog(null, "Espere que la señal se ponga verde y oprima \"Aceptar\"", "Inicio", JOptionPane.INFORMATION_MESSAGE);
		
		// PRUEBA GRABADORA
    }
    
	private void configureSecurity() {
		System.setSecurityManager(new ddnet.useragent.util.SecurityManager());
	}

//	private void configureJNLP() {
//		try {
//            SingleInstanceService singleInstanceService = 
//            		(SingleInstanceService)ServiceManager.lookup("javax.jnlp.SingleInstanceService");
//            singleInstanceService.addSingleInstanceListener(this);
//        } catch(UnavailableServiceException use) {
//            log.error("Error configurando JNLP/SingleInstanceService", use);
//            System.exit(-1);
//        }
//	}
	
	private void ensureAppFolders() {
		if (!rootAppFolder.exists())
			rootAppFolder.mkdirs();
		
		if (!downloadsCacheFolder.exists())
			downloadsCacheFolder.mkdirs();
	}

    private void configureLogging() {
    	RollingFileAppender fileAppender = new RollingFileAppender();
		fileAppender.setName("FileLogger");		
		fileAppender.setFile(logFile.getAbsolutePath());
		fileAppender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5p %c %x - %m%n"));
		fileAppender.setThreshold(Level.ALL);
		fileAppender.setAppend(true);
		fileAppender.setMaxFileSize("20MB");
		fileAppender.setMaxBackupIndex(5);
		fileAppender.activateOptions();
		Logger.getRootLogger().addAppender(fileAppender);
    }

	private void loadProperties() {
    	properties = new Properties();    	
    	
		try {
			log.info("Cargando propiedades de configuración desde -> " + propertiesFile.getAbsolutePath());
			properties.load(new FileInputStream(propertiesFile));
		} catch (Throwable t) {
			loadDefaultProperties();
		}
	}

	private void loadDefaultProperties() {
    	properties = new Properties();
    	properties.put("debug.enabled", "true");
    	properties.put("downloads.maximum-simultaneous", "1");
	}

	private void createUserAgentInstance() {
		userAgent = new UserAgent();		
		if (app.isDebugEnabled()) {
			logDebuggingInfo();
			app.getUserAgent().addEventHandler(DebugUserAgentWebSocketHandler.getInstance());
		}
	}
	
	private static void logDebuggingInfo() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n");
		sb.append("================================= INFO DEBUG =================================\n");
		sb.append("DDNET User Agent v").append(VERSION);
		sb.append("\n");
		sb.append("Java properties:\n");
		for(Object property : System.getProperties().keySet())
			try { sb.append(String.format("%s = %s\n", property, 
					System.getProperty((String)property))); } catch (Throwable t) { }
		sb.append("\n");
		
		sb.append("Environment vars:\n");
		Map<String, String> environment = System.getenv();
		for(Object key : environment.keySet())
			try { sb.append(String.format("%s = %s\n", key, 
					environment.get(key))); } catch (Throwable t) { }				
		sb.append("================================= INFO DEBUG =================================\n");
		
		log.debug(sb.toString());
	}

	public File getLogFile() {
		return logFile;
	}

	public File getDownloadsCacheFolder() {
		return downloadsCacheFolder;
	}
	
	public boolean isDebugEnabled() {
		try {
			return Boolean.parseBoolean(properties.getProperty("debug.enabled", "false"));
		} catch(Throwable t) {
			return false;
		}
	}
	
	public String getProperty(String propertyName) {
		return getProperty(propertyName, null);
	}
	
	public String getProperty(String propertyName, String defaultValue) {
		return properties.getProperty(propertyName, defaultValue);
	}

	public int getProperty(String propertyName, int defaultValue) {
		if (!properties.containsKey(propertyName))
			return defaultValue;

		try {
			return Integer.parseInt(properties.getProperty(propertyName));
		} catch(Throwable t) {
			return defaultValue;
		}		
	}
	
	public UserAgent getUserAgent() {
    	return userAgent;
    }
    
    public void showDownloadsWindow() {
    	showForm(downloadsForm);    
    }

    public void recordStudyAudioReport(String studyID, String patientName, String studyDescription, String host) {
    	final RecordingForm recordingForm = new RecordingForm(new RecordingModel(studyID, patientName, studyDescription, new Recording(), host));
    	showForm(recordingForm);
    }
    
	public void shutdown() {
		getUserAgent().stop();
		System.exit(0);
	}

//	@Override
//	public void newActivation(String[] params) {
//		App.getInstance().showDownloadsWindow();
//	}
	
	private void showForm(final Form form) {
		if (form == null)
    		return;

    	EventQueue.invokeLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	form.setVisible(true);
    	    	form.toFront();
    	    	form.repaint();
    	    }
    	});
	}
	
	private void configureDownloadsWindow() {
		downloadsForm = new DownloadImagesForm(new DownloadImagesModel());
	}	
}
