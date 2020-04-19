package app.dcm.qr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.dcm4che2.tool.dcmqr.*;

public class DcmQRconn {
	
	private static String PACS_AE_TITLE = "";
    private static String PACS_HOST = "";
    private static String PACS_PORT = "";
    
    public DcmQR getDcmQR() throws IOException{
    	
    	DcmQR dcmqr = new DcmQR("ddnet");
    	Properties prop = new Properties();
		
		String fullPath = new File(".").getCanonicalPath();
	    fullPath = fullPath.replace('\\', '/');
	    fullPath = fullPath.substring(0,fullPath.lastIndexOf('/'));
	    fullPath += "/server/default/conf/dd-viewer-config.properties";
	    
	    try {
	    	prop.load(new FileInputStream(fullPath));
	    	PACS_AE_TITLE = prop.getProperty("pacs.aet");
	    	PACS_HOST = prop.getProperty("pacs.host");
	    	PACS_PORT = prop.getProperty("pacs.port");
	    	
	    } catch(IOException ex) {
	    	PACS_AE_TITLE = "DCM4CHEE";
	    	PACS_HOST = "localhost";
	    	PACS_PORT = "11112";
	    	ex.printStackTrace();
	    
	    } finally {
			dcmqr.setCalledAET(PACS_AE_TITLE, true);
			dcmqr.setRemoteHost(PACS_HOST);
			dcmqr.setRemotePort(Integer.parseInt(PACS_PORT));
	    }
		
        dcmqr.setCGet(true);
		dcmqr.configureTransferCapability(true);
	    
		return dcmqr;
    }
}
