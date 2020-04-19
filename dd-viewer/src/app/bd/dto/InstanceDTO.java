package app.bd.dto;

public class InstanceDTO {
	private String imageId;
	private int numberOfFrames;
	private float frameRate;
	
	public InstanceDTO(String imageId,int numberOfFrames, float frameRate){
		this.imageId = imageId;
		this.numberOfFrames = numberOfFrames;
		this.frameRate = frameRate;
	}

	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public int getNumberOfFrames() {
		return numberOfFrames;
	}
	public void setNumberOfFrames(int numberOfFrames) {
		this.numberOfFrames = numberOfFrames;
	}
	public float getFrameRate() {
		return frameRate;
	}
	public void setFrameRate(float frameRate) {
		this.frameRate = frameRate;
	}
}
