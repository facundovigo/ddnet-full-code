package ddnet.useragent.commands.local;

import org.json.simple.JSONObject;

import ddnet.useragent.App;
import ddnet.useragent.commands.AbstractCommand;

public class RecordStudyAudioReportCommand extends AbstractCommand {	
	public static final String COMMAND_NAME = "record-study-audio-report";
	
	private final String studyID;
	private final String patientName;
	private final String studyDescription;
	private final String host;
	
	public RecordStudyAudioReportCommand(Object parameters) {
		super(parameters);
		
		JSONObject retrieveImagesData = (JSONObject)parameters;
		this.studyID = (String)retrieveImagesData.get("studyID");
		this.patientName = (String)retrieveImagesData.get("patientName");
		this.studyDescription = (String)retrieveImagesData.get("studyDescription");
		this.host= (String)retrieveImagesData.get("host");
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
		
	@Override
	public void doExecute() {
		App.getInstance().recordStudyAudioReport(studyID, patientName, studyDescription, host);
	}	
}

