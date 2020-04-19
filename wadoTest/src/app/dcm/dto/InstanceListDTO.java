package app.dcm.dto;

public class InstanceListDTO {
	private String imageId;
	
	public InstanceListDTO(String imageId){
		this.imageId = imageId;
	}

	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
