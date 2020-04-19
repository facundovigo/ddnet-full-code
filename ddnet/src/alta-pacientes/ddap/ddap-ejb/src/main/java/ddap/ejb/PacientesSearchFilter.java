package ddap.ejb;


public class PacientesSearchFilter {
	private String docType;
	private String docNumber;
	private String name;
	private String patSex;
	private String insertDate;
	
	
	public PacientesSearchFilter(String docType, String docNumber, String name,
			String patSex, String insertDate) {
		super();
		this.docType = docType;
		this.docNumber = docNumber;
		this.name = name;
		this.patSex = patSex;
		this.insertDate = insertDate;
	}
	
	
	
	public String getDocType() {
		return docType;
	}
	public String getDocNumber() {
		return docNumber;
	}
	public String getName() {
		return name;
	}
	public String getPatSex() {
		return patSex;
	}
	public String getInsertDate() {
		return insertDate;
	}



	public boolean isSetdocType(){
		return docType != null && !"".equals(docType.trim());
	}
	public boolean isSetDocNumber(){
		return docNumber != null && !"".equals(docNumber.trim());
	}
	public boolean isSetName(){
		return name != null && !"".equals(name.trim());
	}
	public boolean isSetPatSex(){
		return patSex != null && !"".equals(patSex.trim());
	}
	public boolean isSetInsertDate(){
		return insertDate != null && !"".equals(insertDate.trim());
	}

}