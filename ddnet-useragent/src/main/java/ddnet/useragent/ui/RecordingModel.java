package ddnet.useragent.ui;

import ddnet.useragent.audio.Recording;

public class RecordingModel {
	private final String studyID; 
	private final String patientName; 
	private final String studyDescription;
	private final Recording recording;
	private final String host;
	
	public RecordingModel(String studyID, String patientName, String studyDescription,
			Recording recording, String host) {
		if (studyID == null)
			throw new IllegalArgumentException("studyID");
		if (patientName == null)
			throw new IllegalArgumentException("patientName");
		if (studyDescription == null)
			throw new IllegalArgumentException("studyDescription");
		if (recording == null)
			throw new IllegalArgumentException("recording");
		
		this.studyID = studyID;
		this.patientName = patientName;
		this.studyDescription = studyDescription;
		this.recording = recording;
		this.host= host;
	}
	
	public String getStudyID() {
		return studyID;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public String getPatientName() {
		return patientName;
	}
		
	public Recording getRecording() {
		return recording;
	}

	public String getHost() {
		return host;
	}
	
}
