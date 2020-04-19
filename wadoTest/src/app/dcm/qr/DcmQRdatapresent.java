package app.dcm.qr;

import org.dcm4che2.data.Tag;
import org.dcm4che2.tool.dcmqr.*;

public class DcmQRdatapresent {
	
	public void setDataToRetrieve(DcmQR dcmqr){
		
		/**
		 *  TABLA study
		 */
		dcmqr.addReturnKey(new int[]{Tag.StudyInstanceUID});			// columna study_iuid
		dcmqr.addReturnKey(new int[]{Tag.StudyDate});					// columna study_datetime
		dcmqr.addReturnKey(new int[]{Tag.StudyTime});					// columna study_datetime
		dcmqr.addReturnKey(new int[]{Tag.AccessionNumber});				// columna accession_no
		dcmqr.addReturnKey(new int[]{Tag.StudyDescription});			// columna study_desc
		dcmqr.addReturnKey(new int[]{Tag.ReferringPhysicianName});		// columna ref_physician
		dcmqr.addReturnKey(new int[]{Tag.ModalitiesInStudy});			// columna mods_in_study
		dcmqr.addReturnKey(new int[]{Tag.NumberOfStudyRelatedSeries});	// columna num_series
		dcmqr.addReturnKey(new int[]{Tag.NumberOfStudyRelatedInstances});// columna num_instances
		dcmqr.addReturnKey(new int[]{Tag.InstitutionName});				// columna study_custom3
		
		/**
		 * 	TABLA patient
		 */
		dcmqr.addReturnKey(new int[]{Tag.PatientID});					// columna pat_id
		dcmqr.addReturnKey(new int[]{Tag.PatientName});					// columna pat_name
		dcmqr.addReturnKey(new int[]{Tag.PatientBirthDate});			// columna pat_birthdate
		dcmqr.addReturnKey(new int[]{Tag.PatientSex});					// columna pat_sex
		dcmqr.addReturnKey(new int[]{Tag.PatientAge});					// columna pat_custom3
		
		/**
		 * 	TABLA series
		 */
		dcmqr.addReturnKey(new int[]{Tag.SeriesInstanceUID});			// columna series_iuid
		dcmqr.addReturnKey(new int[]{Tag.SeriesNumber});				// columna series_no
		dcmqr.addReturnKey(new int[]{Tag.SeriesDescription});			// columna series_desc
		dcmqr.addReturnKey(new int[]{Tag.StationName});					// columna station_name
		dcmqr.addReturnKey(new int[]{Tag.SeriesDate});					// columna created_time
		dcmqr.addReturnKey(new int[]{Tag.SeriesTime});					// columna created_time
		
		/**
		 * 	TABLA instance
		 */
		dcmqr.addReturnKey(new int[]{Tag.SOPInstanceUID});				// columna sop_iuid
		dcmqr.addReturnKey(new int[]{Tag.SOPClassUID});					// columna sop_cuid
		dcmqr.addReturnKey(new int[]{Tag.InstanceNumber});				// columna instance_no
		
		dcmqr.addReturnKey(new int[]{Tag.NumberOfFrames});
		dcmqr.addReturnKey(new int[]{Tag.RecommendedDisplayFrameRate});
	}
}
