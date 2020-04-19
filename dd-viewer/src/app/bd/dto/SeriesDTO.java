package app.bd.dto;

import java.util.List;

import app.bd.dto.InstanceDTO;

public class SeriesDTO {
	private String seriesUid;
	private String seriesDescription;
	private int seriesNumber;
	private int cantImages;
	private int numberOfFrames;
	private int frameRate;
	private List<InstanceDTO> instanceList;
	
	
	public SeriesDTO(String seriesUid, String seriesDescription, int seriesNumber,
			int cantImages, List<InstanceDTO> instanceList) {
		super();
		this.seriesUid = seriesUid;
		this.seriesDescription = seriesDescription;
		this.seriesNumber = seriesNumber;
		this.cantImages = cantImages;
		this.instanceList = instanceList;
	}
	public SeriesDTO(String seriesUid, String seriesDescription,
			int seriesNumber, int numberOfFrames,
			int frameRate, List<InstanceDTO> instanceList) {
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
	public int getCantImages() {
		return cantImages;
	}
	public void setCantImages(int cantImages) {
		this.cantImages = cantImages;
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
	public List<InstanceDTO> getInstanceList() {
		return instanceList;
	}
	public void setInstanceList(List<InstanceDTO> instanceList) {
		this.instanceList = instanceList;
	}
}
