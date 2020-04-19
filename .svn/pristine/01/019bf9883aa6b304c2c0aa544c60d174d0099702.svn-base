package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="dd_action")
@Entity
public class Action implements Serializable {
	private static final long serialVersionUID = 1L;

	public final static String SIMPLE_STUDY_VISUALIZATION = "simple-study-visualization";
	public final static String ADVANCED_STUDY_VISUALIZATION = "advanced-study-visualization";
	public final static String VIEW_STUDY_REPORT = "view-study-report";
	public final static String EDIT_STUDY_REPORT = "edit-study-report";
	public final static String VIEW_STUDY_FILES = "view-study-files";
	public final static String UPLOAD_STUDY_FILE = "upload-study-file";
	public final static String DOWNLOAD_STUDY_FILE = "download-study-file";
	public final static String DELETE_STUDY_FILE = "delete-study-file";
	public final static String ACCESS_STUDY_DICOMDIR = "access-study-dicomdir";
	public final static String VIEW_STUDY_NOTES = "view-study-notes";
	public final static String EDIT_STUDY_NOTES = "edit-study-notes";
	public final static String ASSIGN_STUDY_TO = "edit-study-notes";
	public final static String ACCESS_STUDIES_FROM_INSTITUION = "access-studies-from-institution";
		
	@Column(name="id")
	@Id
	private long id;
	
	@Column(name="name")
	private String name;	

	@Column(name="description")
	private String description;	
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Action other = (Action) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
