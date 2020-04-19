package app.dcm.dao;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.tool.dcmqr.DcmQR;

import app.dcm.dto.StudySeriesListDTO;
import app.dcm.qr.DcmQRconn;
import app.dcm.qr.DcmQRdatapresent;
import app.pgsql.DataBaseConn;

public class StudySeriesListDAO {
	
	private static DcmQRconn qrConn = new DcmQRconn();
	private static DcmQRdatapresent qrData = new DcmQRdatapresent();
	private static SeriesListDAO seriesList = new SeriesListDAO();
	private DataBaseConn conn = new DataBaseConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public List<StudySeriesListDTO> getStudySeriesList(String studyID) throws Exception {
		List<StudySeriesListDTO> studySeriesList = new ArrayList<StudySeriesListDTO>();
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT p.pat_name, p.pat_id, s.study_datetime, "
				+ " s.mods_in_study, s.study_desc, s.num_instances, s.study_iuid "
				+ " FROM study s "
				+ " JOIN patient p ON p.pk = s.patient_fk "
				+ " WHERE s.study_iuid = :studyID "
				.replace(":studyID", String.format("'%s'", studyID));
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			studySeriesList.add(
				new StudySeriesListDTO(
						rs.getString(1), 
						rs.getString(2), 
						DATE_FORMAT.format(rs.getDate(3)), 
						rs.getString(4),
						rs.getString(5),
						rs.getInt(6),
						rs.getString(7),
						seriesList.getSeriesList(rs.getString(7))
			));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return studySeriesList;
	}
	
	public List<StudySeriesListDTO> getStudySeriesList(String studyID, int uno) throws IOException{
		
		List<StudySeriesListDTO> studySeriesList = new ArrayList<StudySeriesListDTO>();
		List<DicomObject> dcmResult = null;
		
		DcmQR dcmqr = qrConn.getDcmQR();
		qrData.setDataToRetrieve(dcmqr);
		
		try{
			dcmqr.addMatchingKey(new int[]{Tag.StudyInstanceUID}, studyID);
			dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.STUDY);
			
	        dcmqr.start();
	        dcmqr.open();
	        dcmResult = dcmqr.query();
	        dcmqr.get(dcmResult);
	        dcmqr.stop();
	        dcmqr.close();
	        
	        for(DicomObject dcmobj : dcmResult){
	        	studySeriesList.add(
	        		new StudySeriesListDTO(	dcmobj.getString(Tag.PatientName),
											dcmobj.getString(Tag.PatientID),
	        								dcmobj.getString(Tag.StudyDate), 
	        								dcmobj.getString(Tag.ModalitiesInStudy), 
	        								dcmobj.getString(Tag.StudyDescription),
	        								dcmobj.getInt(Tag.NumberOfStudyRelatedInstances),
	        								dcmobj.getString(Tag.StudyInstanceUID), 
	        								seriesList.getSeriesList(dcmobj.getString(Tag.StudyInstanceUID))
	        		));
	        }
		} catch(Exception ex) { ex.printStackTrace(); }
		
		return studySeriesList;
	}
}
