package ddnet.useragent.commands.local;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;

import ddnet.useragent.App;
import ddnet.useragent.CommandHandler;
import ddnet.useragent.commands.AbstractCommand;
import ddnet.useragent.commands.Command;
import ddnet.useragent.commands.CommandFactory;
import ddnet.useragent.downloads.Download;

public class RetrieveImagesCommand extends AbstractCommand {	
	public static final String COMMAND_NAME = "retrieve-images";
	
	private final URL url;
	private final String patientName;
	private final String description;
	private final String callingAET;
	private final String viewerHostname;
	private final int viewerPort;
	private final String viewerAET;
	
	public RetrieveImagesCommand(Object parameters) {
		super(parameters);
		
		JSONObject retrieveImagesData = (JSONObject)parameters;
		try {
			this.patientName = (String)retrieveImagesData.get("patientName");
			this.description = (String)retrieveImagesData.get("description");
			this.url = new URL((String)retrieveImagesData.get("url"));
			this.callingAET = (String)retrieveImagesData.get("callingAET");
			this.viewerHostname = (String)retrieveImagesData.get("viewerHostname");
			this.viewerPort = ((Long)retrieveImagesData.get("viewerPort")).intValue();
			this.viewerAET = (String)retrieveImagesData.get("viewerAET");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
		
	@Override
	public void doExecute() {
		App.getInstance().showMainWindow();
		
		Download download = App.getInstance().getUserAgent().getDownloadManager().createDownload(url, patientName + " - " + description, Download.STUDY);
		download.getProperties().put("patientName", patientName);
		download.getProperties().put("studyDescription", description);
		Command onDownloadCompletedCommand = CommandFactory.getInstance().create(SendImagesToViewerCommand.COMMAND_NAME, 
				new SendImagesToViewerCommand.CommandParameters(download, callingAET, viewerHostname, viewerPort, viewerAET), getEventHandlers());
		onDownloadCompletedCommand.getProperties().put(CommandHandler.DOWNLOAD_PROPERTY, download);
		
		App.getInstance().getUserAgent().getDownloadManager().addDownload(download, onDownloadCompletedCommand);
	}	
}

