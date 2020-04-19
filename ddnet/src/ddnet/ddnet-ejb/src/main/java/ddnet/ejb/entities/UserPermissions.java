package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="dd_user_permissions")
@Entity
public class UserPermissions implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="user_id") private long userID;
	
	@Column(name="can_visual_studies") boolean canVisualStudies;
	@Column(name="can_use_agent") boolean canUseAgent;
	@Column(name="can_download_dicomdir") boolean canDownloadDicomdir;
	@Column(name="can_simple_view") boolean canSimpleView;
	@Column(name="can_intermed_view") boolean canIntermedView;
	@Column(name="can_advanced_view") boolean canAdvancedView;
	@Column(name="can_assign_study") boolean canAssignStudy;
	@Column(name="can_open_study_page") boolean canOpenStudyView;
	@Column(name="can_do_reports") boolean canDoReports;
	@Column(name="can_save_report") boolean canSaveReports;
	@Column(name="can_finish_report") boolean canFinishReport;
	@Column(name="can_record_audio") boolean canRecordAudio;
	@Column(name="can_play_audio") boolean canPlayAudio;
	@Column(name="can_do_messages") boolean canDoMessages;
	@Column(name="can_write_message") boolean canWriteMessage;
	@Column(name="can_reassign_study") boolean canReassignStudy;
	@Column(name="can_assign_patient") boolean canAssignPatient;
	@Column(name="can_declare_cd") boolean canDeclareCD;
	@Column(name="can_do_files") boolean canDoFiles;
	@Column(name="can_manage_file") boolean canManageFile;
	@Column(name="can_manage_om") boolean canManageOM;
	@Column(name="can_manage_audio") boolean canManageAudio;
	@Column(name="can_view_qr") boolean canViewQR;
	@Column(name="can_do_log") boolean canDoLog;
	@Column(name="can_access_abm") boolean canAccessABM;
	@Column(name="can_manage_users") boolean canManageUsers;
	@Column(name="can_manage_studies") boolean canManageStudies;
	@Column(name="can_delete_studies") boolean canDeleteStudies;
	@Column(name="can_get_statistics") boolean canGetStatistics;
	@Column(name="can_manage_mails") boolean canManageMails;
	@Column(name="can_view_all_studies") boolean canViewAllStudies;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public boolean isCanVisualStudies() {
		return canVisualStudies;
	}

	public void setCanVisualStudies(boolean canVisualStudies) {
		this.canVisualStudies = canVisualStudies;
	}

	public boolean isCanUseAgent() {
		return canUseAgent;
	}

	public void setCanUseAgent(boolean canUseAgent) {
		this.canUseAgent = canUseAgent;
	}

	public boolean isCanDownloadDicomdir() {
		return canDownloadDicomdir;
	}

	public void setCanDownloadDicomdir(boolean canDownloadDicomdir) {
		this.canDownloadDicomdir = canDownloadDicomdir;
	}

	public boolean isCanSimpleView() {
		return canSimpleView;
	}

	public void setCanSimpleView(boolean canSimpleView) {
		this.canSimpleView = canSimpleView;
	}

	public boolean isCanIntermedView() {
		return canIntermedView;
	}

	public void setCanIntermedView(boolean canIntermedView) {
		this.canIntermedView = canIntermedView;
	}

	public boolean isCanAdvancedView() {
		return canAdvancedView;
	}

	public void setCanAdvancedView(boolean canAdvancedView) {
		this.canAdvancedView = canAdvancedView;
	}

	public boolean isCanAssignStudy() {
		return canAssignStudy;
	}

	public void setCanAssignStudy(boolean canAssignStudy) {
		this.canAssignStudy = canAssignStudy;
	}

	public boolean isCanOpenStudyView() {
		return canOpenStudyView;
	}

	public void setCanOpenStudyView(boolean canOpenStudyView) {
		this.canOpenStudyView = canOpenStudyView;
	}

	public boolean isCanDoReports() {
		return canDoReports;
	}

	public void setCanDoReports(boolean canDoReports) {
		this.canDoReports = canDoReports;
	}

	public boolean isCanSaveReports() {
		return canSaveReports;
	}

	public void setCanSaveReports(boolean canSaveReports) {
		this.canSaveReports = canSaveReports;
	}

	public boolean isCanFinishReport() {
		return canFinishReport;
	}

	public void setCanFinishReport(boolean canFinishReport) {
		this.canFinishReport = canFinishReport;
	}

	public boolean isCanRecordAudio() {
		return canRecordAudio;
	}

	public void setCanRecordAudio(boolean canRecordAudio) {
		this.canRecordAudio = canRecordAudio;
	}

	public boolean isCanPlayAudio() {
		return canPlayAudio;
	}

	public void setCanPlayAudio(boolean canPlayAudio) {
		this.canPlayAudio = canPlayAudio;
	}

	public boolean isCanDoMessages() {
		return canDoMessages;
	}

	public void setCanDoMessages(boolean canDoMessages) {
		this.canDoMessages = canDoMessages;
	}

	public boolean isCanWriteMessage() {
		return canWriteMessage;
	}

	public void setCanWriteMessage(boolean canWriteMessage) {
		this.canWriteMessage = canWriteMessage;
	}

	public boolean isCanReassignStudy() {
		return canReassignStudy;
	}

	public void setCanReassignStudy(boolean canReassignStudy) {
		this.canReassignStudy = canReassignStudy;
	}

	public boolean isCanAssignPatient() {
		return canAssignPatient;
	}

	public void setCanAssignPatient(boolean canAssignPatient) {
		this.canAssignPatient = canAssignPatient;
	}

	public boolean isCanDeclareCD() {
		return canDeclareCD;
	}

	public void setCanDeclareCD(boolean canDeclareCD) {
		this.canDeclareCD = canDeclareCD;
	}

	public boolean isCanDoFiles() {
		return canDoFiles;
	}

	public void setCanDoFiles(boolean canDoFiles) {
		this.canDoFiles = canDoFiles;
	}

	public boolean isCanManageFile() {
		return canManageFile;
	}

	public void setCanManageFile(boolean canManageFile) {
		this.canManageFile = canManageFile;
	}

	public boolean isCanManageOM() {
		return canManageOM;
	}

	public void setCanManageOM(boolean canManageOM) {
		this.canManageOM = canManageOM;
	}

	public boolean isCanManageAudio() {
		return canManageAudio;
	}

	public void setCanManageAudio(boolean canManageAudio) {
		this.canManageAudio = canManageAudio;
	}

	public boolean isCanViewQR() {
		return canViewQR;
	}

	public void setCanViewQR(boolean canViewQR) {
		this.canViewQR = canViewQR;
	}

	public boolean isCanDoLog() {
		return canDoLog;
	}

	public void setCanDoLog(boolean canDoLog) {
		this.canDoLog = canDoLog;
	}

	public boolean isCanAccessABM() {
		return canAccessABM;
	}

	public void setCanAccessABM(boolean canAccessABM) {
		this.canAccessABM = canAccessABM;
	}

	public boolean isCanManageUsers() {
		return canManageUsers;
	}

	public void setCanManageUsers(boolean canManageUsers) {
		this.canManageUsers = canManageUsers;
	}

	public boolean isCanManageStudies() {
		return canManageStudies;
	}

	public void setCanManageStudies(boolean canManageStudies) {
		this.canManageStudies = canManageStudies;
	}

	public boolean isCanDeleteStudies() {
		return canDeleteStudies;
	}

	public void setCanDeleteStudies(boolean canDeleteStudies) {
		this.canDeleteStudies = canDeleteStudies;
	}

	public boolean isCanGetStatistics() {
		return canGetStatistics;
	}

	public void setCanGetStatistics(boolean canGetStatistics) {
		this.canGetStatistics = canGetStatistics;
	}

	public boolean isCanManageMails() {
		return canManageMails;
	}

	public void setCanManageMails(boolean canManageMails) {
		this.canManageMails = canManageMails;
	}

	public boolean isCanViewAllStudies() {
		return canViewAllStudies;
	}

	public void setCanViewAllStudies(boolean canViewAllStudies) {
		this.canViewAllStudies = canViewAllStudies;
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
		UserPermissions other = (UserPermissions) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%d", userID);
	}
	
}
