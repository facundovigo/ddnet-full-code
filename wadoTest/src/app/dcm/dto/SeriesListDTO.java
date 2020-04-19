package app.dcm.dto;

import java.util.List;

public class SeriesListDTO {
	
	private String seriesUid;
	private String seriesDescription;
	private int seriesNumber;
	private int numberOfFrames;
	private int frameRate;
	private List<InstanceListDTO> instanceList;
	
	
	public SeriesListDTO(String seriesUid, String seriesDescription,
			int seriesNumber, List<InstanceListDTO> instanceList) {
		super();
		this.seriesUid = seriesUid;
		this.seriesDescription = seriesDescription;
		this.seriesNumber = seriesNumber;
		this.instanceList = instanceList;
	}
	public SeriesListDTO(String seriesUid, String seriesDescription,
			int seriesNumber, int numberOfFrames,
			int frameRate, List<InstanceListDTO> instanceList) {
		super();
		this.seriesUid = seriesUid;
		this.seriesDescription = seriesDescription;
		this.seriesNumber = seriesNumber;
		this.numberOfFrames = numberOfFrames;
		this.frameRate = frameRate;
		this.instanceList = instanceList;
	}

	public String getSeriesUid() {
		return seriesUid;
	}
	public void setSeriesUid(String seriesUid) {
		this.seriesUid = seriesUid;
	}
	public String getSeriesDescription() {
		return seriesDescription;
	}
	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}
	public int getSeriesNumber() {
		return seriesNumber;
	}
	public void setSeriesNumber(int seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	public int getNumberOfFrames() {
		return numberOfFrames;
	}
	public void setNumberOfFrames(int numberOfFrames) {
		this.numberOfFrames = numberOfFrames;
	}
	public int getFrameRate() {
		return frameRate;
	}
	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}
	public List<InstanceListDTO> getInstanceList() {
		return instanceList;
	}
	public void setInstanceList(List<InstanceListDTO> instanceList) {
		this.instanceList = instanceList;
	}
}

