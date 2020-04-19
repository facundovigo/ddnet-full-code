package ddnet.useragent.downloads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import ddnet.useragent.App;
import ddnet.useragent.commands.Command;
import ddnet.useragent.commands.CommandExecutionException;
import ddnet.useragent.commands.CommandExecutor;
import ddnet.useragent.commands.NoOpCommand;

public class DownloadManager {
	private static final Logger log = Logger.getLogger(DownloadManager.class);
	private ExecutorService downloadsExecutor;
	private final Object downloadsExecutorSyncObject = new Object();
	private final Map<UUID, DownloadEntry> downloads = new ConcurrentHashMap<UUID, DownloadEntry>();
	private final CommandExecutor commandExecutor;
	private final Collection<DownloadEventHandler> eventHandlers = new ArrayList<DownloadEventHandler>();
		
	public DownloadManager(CommandExecutor commandExecutor) {
		if (commandExecutor == null)
			throw new IllegalArgumentException("commandExecutor");

		this.commandExecutor = commandExecutor;
	}
	
	public void addEventHandler(DownloadEventHandler handler) {
		if (handler == null || eventHandlers.contains(handler))
			return;		
		eventHandlers.add(handler);
	}

	public void removeEventHandler(DownloadEventHandler handler) {
		if (handler == null || !eventHandlers.contains(handler))
			return;		
		eventHandlers.remove(handler);
	}
		
	public Download createDownload(URL url, String description, String type) {
		File tempDownloadFile = new File(App.getInstance().getDownloadsCacheFolder(), "dd-download-temp" + Long.toString(System.nanoTime()));
		tempDownloadFile.deleteOnExit();
		return new Download(url, tempDownloadFile, description, type);
	}
	
	public void addDownload(Download download) {
		addDownload(download, null);
	}
	
	public void addDownload(Download download, Command onCompletedCommand) {
		if (download == null)
			return;
		
		if (onCompletedCommand == null)
			onCompletedCommand = NoOpCommand.getInstance();
		
		downloads.put(download.getID(), new DownloadEntry(download, onCompletedCommand));
		notifyOnNewDownload(download);
		startDownload(download);
	}
	
	public Download getDownload(UUID downloadID) {
		DownloadEntry downloadEntry = downloads.get(downloadID);
		return downloadEntry != null ? downloadEntry.getDownload() : null;
	}
	
	public void removeDownload(UUID downloadID) {
		DownloadEntry downloadEntry = downloads.get(downloadID);
		if (downloadEntry == null)
			return;
		
		if (!downloadEntry.getDownload().isFinalized())
			throw new RuntimeException(String.format("La descarga '%s' no ha finalizado todavía, y por lo tanto no puede ser removida.", downloadID));
		
		downloads.remove(downloadID);
	}
	
	public void reRunOnDownloadCompletedAction(UUID downloadID) {
		final DownloadEntry downloadEntry = downloads.get(downloadID);
		if (downloadEntry == null)
			return;

		if (!downloadEntry.getDownload().isFinalized())
			throw new RuntimeException(String.format("La descarga '%s' todavía se encuentra en curso. Cuando la misma finalice, "
					+ "la acción post-descarga será ejecutada automáticamente.", downloadID));
		
		if (!downloadEntry.getDownload().isFullyDownloaded())
			throw new RuntimeException(String.format("La descarga '%s' no se ha completado.", downloadID));

		final Command onCompletedAction = downloadEntry.getOnCompletedAction();
		if (onCompletedAction == null)
			throw new RuntimeException(String.format("La descarga '%s' no tiene una acción post-descarga asociada.", downloadID));
		
		log.debug(String.format("Se solicita re-ejecutar la acción post descarga para la descarga '%s'", downloadID));
		downloadsExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					doDownloadCompletedAction(downloadEntry);
				} catch (Throwable t) {
					log.error(String.format("Error re-ejecutando acción post-descarga '%s' para la descarga '%s'", 
							onCompletedAction, downloadEntry.getDownload()), t);	
				}
			}
		});		
	}
	
	public void restartDownload(UUID downloadID) {
		DownloadEntry downloadEntry = downloads.get(downloadID);
		if (downloadEntry == null)
			return;
		
		downloadEntry.getDownload().reset();
		startDownload(downloadEntry.getDownload());
	}
	
	public void start(int maximumSimultaneousDownloads) {
		if (maximumSimultaneousDownloads < 1)
			throw new IllegalArgumentException("maximumSimultaneousDownloads");
		
		synchronized (downloadsExecutorSyncObject) {			
			downloadsExecutor = Executors.newFixedThreadPool(maximumSimultaneousDownloads);		
		}
	}
	
	public void stop() {
		synchronized (downloadsExecutorSyncObject) {
			if (downloadsExecutor != null) {
				downloadsExecutor.shutdownNow();
				downloadsExecutor = null;
			}
		}
	}	
	
	private void startDownload(final Download download) {
		Runnable downloadFileRunnable = new Runnable() {
			public void run() {
				download.setStarted(true);
				notifyOnUpdate(download);
				
				BufferedInputStream input = null;
				BufferedOutputStream output = null;
				
				DownloadEntry downloadEntry = null;
				try {
					final int BUFFER_SIZE = 8192;
					
					if (download.isCanceled()) {
						notifyOnCanceled(download);
						return;
					}
					
					HttpURLConnection httpConnection = (HttpURLConnection)(download.getURL().openConnection());
					httpConnection.setRequestProperty("Cookie", "JSESSIONID=" + App.getInstance().getUserAgent().getCurrentUser().getToken());
					int completeFileSize = httpConnection.getContentLength();
					download.setTotalSize(completeFileSize);

					try  {
						input = new BufferedInputStream(httpConnection.getInputStream());
						output = new BufferedOutputStream(new FileOutputStream(download.getFile()), BUFFER_SIZE);
						
						byte[] buffer = new byte[BUFFER_SIZE];
						int downloadedFileSize = 0;
						int readCount = 0;
						while ((readCount = input.read(buffer, 0, BUFFER_SIZE)) >= 0) {
							downloadedFileSize += readCount;
							output.write(buffer, 0, readCount);
							download.setDownloaded(downloadedFileSize);
							notifyOnUpdate(download);
							
							if (download.isCanceled()) {
								notifyOnCanceled(download);
								return;
							}							
						}
					} finally {
						if (output != null)
							try { output.close(); } catch (Throwable ignore) { }
						if (input != null)
							 try { input.close(); } catch (Throwable ignore) { }
					}
					
					downloadEntry = downloads.get(download.getID());
					if (download.isFullyDownloaded()) {
						notifyOnCompleted(download);
						doDownloadCompletedAction(downloadEntry);
					} else {
						download.setFinishedWithErrors(true);
						notifyOnFinishedWithErrors(download);
						doDownloadFinishedWithErrorsAction(downloadEntry);
						log.error(String.format("Se terminaron de descargar los datos pero el download '%s' quedó incompleto!", download));
					}
				} catch (CommandExecutionException cee) {
					download.setFinishedWithErrors(true);
				} catch (Throwable t) {
					download.setFinishedWithErrors(true);
					notifyOnFinishedWithErrors(download);
					if (downloadEntry == null)
						downloadEntry = downloads.get(download.getID());
					doDownloadFinishedWithErrorsAction(downloadEntry);
					log.error("Error descargando " + download, t);
				}
			}
		};
		
		try {
			downloadsExecutor.execute(downloadFileRunnable);
		} catch (Throwable t) {
			log.error("No se pudo encolar la descarga " + download, t);
		}
    }
	
	private Collection<DownloadEventHandler> getEventHandlers() {
		return new ArrayList<DownloadEventHandler>(eventHandlers);		
	}

	private void notifyOnNewDownload(Download download) {
		for(DownloadEventHandler handler : getEventHandlers())
			try { handler.onNewDownload(download); } catch (Throwable ignore) { }
	}	
	
	private void notifyOnUpdate(Download download) {
		for(DownloadEventHandler handler : getEventHandlers())
			try { handler.onUpdate(download); } catch (Throwable ignore) { }
	}
	
	private void notifyOnCompleted(Download download) {
		for(DownloadEventHandler handler : getEventHandlers())
			try { handler.onCompleted(download); } catch (Throwable ignore) { }
	}	

	private void notifyOnFinishedWithErrors(Download download) {
		for(DownloadEventHandler handler : getEventHandlers())
			try { handler.onFinishedWithErrors(download); } catch (Throwable ignore) { }
	}
	
	private void notifyOnCanceled(Download download) {
		for(DownloadEventHandler handler : getEventHandlers())
			try { handler.onCanceled(download); } catch (Throwable ignore) { }
	}
	
	private void doDownloadCompletedAction(DownloadEntry downloadEntry) {
		if (downloadEntry == null)
			return;

		Command onCompletedAction = downloadEntry.getOnCompletedAction();
		
		if (onCompletedAction == null) {
			log.debug(String.format("No hay acción indicada al completarse la descarga '%s'", 
					downloadEntry.getDownload()));
		} else {		
			log.debug(String.format("A punto de ejecutar acción '%s' al completarse la descarga '%s'", 
					onCompletedAction, downloadEntry.getDownload()));
		
			commandExecutor.execute(onCompletedAction);
		}
	}

	private void doDownloadFinishedWithErrorsAction(DownloadEntry downloadEntry) {
		if (downloadEntry == null)
			return;
	}
	
	private class DownloadEntry {
		private final Download download;
		private final Command onCompletedAction;

		public DownloadEntry(Download download, Command onCompletedAction) {
			this.download = download;
			this.onCompletedAction = onCompletedAction;
		}

		public Download getDownload() {
			return download;
		}

		public Command getOnCompletedAction() {
			return onCompletedAction;
		}
	}
}
