package ddnet.useragent.commands.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.dcm4che2.tool.dcmsnd.DcmSnd;

import ddnet.useragent.commands.AbstractCommand;
import ddnet.useragent.downloads.Download;
import ddnet.useragent.util.IOUtils;

public class SendImagesToViewerCommand extends AbstractCommand {
	public static final String COMMAND_NAME = "send-images-to-viewer";
	
	private final Download download;
	private final String callingAET;
	private final String viewerHostname;
	private final int viewerPort;
	private final String viewerAET;
	
	public SendImagesToViewerCommand(Object parameters) {
		super(parameters);
		
		CommandParameters commandParameters = (CommandParameters)parameters;
				
		if (commandParameters.getDownload() == null)
			throw new IllegalArgumentException("download");
		if (commandParameters.getViewerPort() < 0 || commandParameters.getViewerPort() > 65535)
			throw new IllegalArgumentException("viewerPort");
		
		this.download = commandParameters.getDownload();
		this.callingAET = commandParameters.getCallingAET();
		this.viewerHostname = commandParameters.getViewerHostname();
		this.viewerPort = commandParameters.getViewerPort();
		this.viewerAET = commandParameters.getViewerAET();
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
	
	@Override
	public void doExecute() {
		log.debug(String.format("Desempaquetando y enviando imágenes al visualizador para la descarga '%s'", download));
		
		// Descomprimimos el archivos recibido.
		ZipInputStream zipInputStream = null;
		ZipEntry zipEntry = null;		
		File tempFolder = null;
		
		try {
			tempFolder = IOUtils.createTempDirectory();
			zipInputStream = new ZipInputStream(new FileInputStream(download.getFile()));
	    	zipEntry = zipInputStream.getNextEntry();
	    	
		 
	    	byte[] buffer = new byte[1024];
	    	while(zipEntry!=null) {	
	    		String fileName = zipEntry.getName();
	    		File newFile = new File(tempFolder, fileName);
	    		newFile.getParentFile().mkdirs();
	    		
	    		FileOutputStream fos = new FileOutputStream(newFile);             
	    		int len;
	    		while ((len = zipInputStream.read(buffer)) > 0)
	    			fos.write(buffer, 0, len);
	
	    		fos.close();   
	    		zipEntry = zipInputStream.getNextEntry();
	    	}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
	    	if (zipInputStream != null) {
	    		try { zipInputStream.closeEntry(); } catch (Throwable ignore) {}
	    		try { zipInputStream.close(); } catch (Throwable ignore) {}
	    	}
		}
	    		
		// Y finalmente enviamos las imagenes al visualizador local.
		final DcmSnd dcmSnd = new DcmSnd();
		String target = "";
		
		if (viewerAET != null && viewerAET.trim().length() > 0) {
			dcmSnd.setCalledAET(viewerAET);
			target = viewerAET;
		}
		
		String hostname = "";
		if (viewerHostname != null && viewerHostname.trim().length() > 0) {
			dcmSnd.setRemoteHost(viewerHostname);
			hostname = viewerHostname;
		} else {
			dcmSnd.setRemoteHost("127.0.0.1");
			hostname = "127.0.0.1";
		}
		target = target.length() > 0 ? target + "@" + hostname : hostname;
		
		dcmSnd.setRemotePort(viewerPort);
		target = target + ":" + viewerPort;

		if (callingAET != null && callingAET.trim().length() > 0)
			dcmSnd.setCalling(callingAET);
		
		dcmSnd.setOfferDefaultTransferSyntaxInSeparatePresentationContext(false);
		dcmSnd.setSendFileRef(false);
        dcmSnd.setStorageCommitment(false);
        dcmSnd.setPackPDV(true);
        dcmSnd.setTcpNoDelay(true);        		
        
        File imagesFolder = new File(tempFolder, "imagenes");
        for(File f : imagesFolder.listFiles())
        	dcmSnd.addFile(f);		
		
		dcmSnd.configureTransferCapability();

		try {
			info("Iniciando DICOM Send...");
			dcmSnd.start();
			
			info("Conectando con " + target);
			dcmSnd.open();
			
			info("Conectado con " + target + ". Iniciando envío de imágenes...");
			long startTime = System.currentTimeMillis();
			Thread waiterThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						dcmSnd.send();
					} catch (Throwable t) {
						error("Ha ocurrido un error en el envío de imágenes.", t);
						log.error("Ha ocurrido un error en el envío de imágenes. Download=" + download, t);
					}
				}				
			});
			waiterThread.start();
			
			while(waiterThread.isAlive()) {
				if (dcmSnd.getNumberOfFilesSent() > 0) 
					info(String.format("Enviada imagen %d de %d", 
							dcmSnd.getNumberOfFilesSent(), dcmSnd.getNumberOfFilesToSend()));
				else
					info(String.format("Se enviarán %d imágenes", 
							dcmSnd.getNumberOfFilesToSend()));
				Thread.sleep(100);
			}
			
			long endTime = System.currentTimeMillis();
			
			notifyImagesSent(dcmSnd.getNumberOfFilesSent(), dcmSnd.getNumberOfFilesToSend(), endTime - startTime);			
		} catch (Throwable t) {
			error("Ha ocurrido un error en el envío de imágenes.", t);
			throw new RuntimeException(t);
		} finally {
			try { dcmSnd.close(); } catch (Throwable ignore) { }
			try { dcmSnd.stop(); } catch (Throwable ignore) { }
		}
		
		// Eliminamos temporales.
		if (tempFolder != null && tempFolder.exists())
			try { FileUtils.deleteDirectory(tempFolder); } catch (Throwable ignore) { }
	}

	private void notifyImagesSent(int numberOfFilesSent, int numberOfFilesToSend, long elapsedMillis) {
		long value;
		String unit;
		if (elapsedMillis >= 1000L) { 
			value = elapsedMillis/1000L;
			unit = "s";
		} else {
			value = elapsedMillis;
			unit = "ms";
		}			
		
		if (numberOfFilesSent == numberOfFilesToSend)	
			info(String.format("Se enviaron %d imágenes en %d %s",
					numberOfFilesSent, value, unit));
		else
			warn(String.format("Se enviaron %d de %d imágenes en %d %s",
					numberOfFilesSent, numberOfFilesToSend, value, unit));
	}
	
	static class CommandParameters {
		private final Download download; 
		private final String callingAET; 
		private final String viewerHostname;
		private final int viewerPort;
		private final String viewerAET;
		
		public CommandParameters(Download download, String callingAET, String viewerHostname, int viewerPort, String viewerAET) {
			this.download = download;
			this.callingAET = callingAET;
			this.viewerHostname = viewerHostname;
			this.viewerPort = viewerPort;
			this.viewerAET = viewerAET;
		}

		public Download getDownload() {
			return download;
		}

		public String getCallingAET() {
			return callingAET;
		}

		public String getViewerHostname() {
			return viewerHostname;
		}

		public int getViewerPort() {
			return viewerPort;
		}

		public String getViewerAET() {
			return viewerAET;
		}		
	}
}
