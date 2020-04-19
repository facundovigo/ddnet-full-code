package ddnet.useragent.downloads;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Download {
	public static String STUDY = "Estudio";
	public static String REPORT = "Informe";
	
	private final UUID id;
	private final URL url;
	private final File file;
	private final String description;
	private final String type;
	private int totalSize;
	private int downloaded;
	private boolean started;
	private boolean finishedWithErrors;
	private boolean canceled;
	private final Map<String, String> properties = new ConcurrentHashMap<String, String>();

	public Download(URL url, File file, String type) {
		this(url, file, type, 0);
	}

	public Download(URL url, File file, String description, String type) {
		this(url, file, description, type, 0);
	}

	public Download(URL url, File file, String type, int totalSize) {
		this(url, file, "", type, totalSize);
	}
	
	public Download(URL url, File file, String description, String type, int totalSize) {
		if (url == null)
			throw new IllegalArgumentException("url");
		if (file == null)
			throw new IllegalArgumentException("file");
		if (!STUDY.equalsIgnoreCase(type) && !REPORT.equalsIgnoreCase(type))
			throw new IllegalArgumentException("type");
		if (totalSize < 0)
			throw new IllegalArgumentException("totalSize");
		
		this.id = UUID.randomUUID();
		this.url = url;
		this.file = file;
		this.description = description != null ? description : "";
		this.type = type;
		this.totalSize = totalSize;
	}
	
	public UUID getID() {
		return id;
	}
	public URL getURL() {
		return url;
	}
	public File getFile() {
		return file;
	}
	public String getDescription() {
		return description;
	}
	public String getType() {
		return type;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		if (this.totalSize == 0) {
			if (totalSize < 0)
				throw new IllegalArgumentException("totalSize");
			
			this.totalSize = totalSize;
		} else { 
			throw new IllegalStateException("Ya se había establecido el tamaño para la descarga: " + this);
		}
	}
	public int getDownloaded() {
		return downloaded;
	}
	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}
	public boolean finishedWithErrors() {
		return finishedWithErrors;
	}
	public void setFinishedWithErrors(boolean finishedWithErrors) {
		this.finishedWithErrors = finishedWithErrors;
	}
	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
	}	
	public boolean isCanceled() {
		return canceled;
	}	
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
	public void reset() {		
		if (!isFinalized())
			throw new RuntimeException(String.format("La descarga '%s' todavía se encuentra en curso.", this.id));
		
		this.totalSize = 0;
		this.downloaded = 0;
		this.started = false;
		this.finishedWithErrors = false;
		this.canceled = false;
	}
		
	/**
	 * Indica si esta descarga ya terminó de procesarse, ya sea que el resultado 
	 * haya sido la descarga correcta o haya finalizado en un error de descarga. 
	 */
	public boolean isFinalized() {
		return isFullyDownloaded() || finishedWithErrors() || isCanceled();				
	}	
	public Map<String, String> getProperties() {
		return properties;
	}	
	public boolean isFullyDownloaded() {
		return totalSize > 0 && totalSize == downloaded;
	}
	public float getPercentageDownloaded() {
		return downloaded == 0 ? 0F : ((float)downloaded*100F)/(float)totalSize;
	}
	public String getStatusText() {
		if (canceled)
			return "Cancelada";
		if (!started)
			return "No iniciada";
		if (downloaded == 0)
			return "Descarga iniciada, esperando datos...";
		if (isFullyDownloaded())
			return String.format("Descarga completa (%d KB)", totalSize/1024);
		else
			return String.format("%d KB de %d KB descargados (%,.2f%%)", downloaded/1024, totalSize/1024, getPercentageDownloaded());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Download other = (Download) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("[%s] '%s' - [%s]", id, description, getStatusText());
	}
}
