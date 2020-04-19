package ddap.ejb;


public class PracticasSearchFilter {
	private String modality;
	private String name;
	private String capability;
	private int region;
	private String needReport;
	
	
	public PracticasSearchFilter(String modality, String name,
			String capability, int region, String needReport) {
		super();
		this.modality = modality;
		this.name = name;
		this.capability = capability;
		this.region = region;
		this.needReport = needReport;
	}

	

	public String getModality() {
		return modality;
	}
	public String getName() {
		return name;
	}
	public String getCapability() {
		return capability;
	}
	public int getRegion() {
		return region;
	}
	public String getNeedReport() {
		return needReport;
	}



	public boolean isSetModality(){
		return modality != null && !"".equals(modality.trim());
	}
	public boolean isSetName(){
		return name != null && !"".equals(name.trim());
	}
	public boolean isSetCapability(){
		return capability != null && !"".equals(capability.trim());
	}
	public boolean isSetRegion(){
		return region > -1;
	}
	public boolean isSetNeedReport(){
		return needReport != null && !"".equals(needReport.trim());
	}

}