package app.dcm.dao;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.tool.dcmqr.DcmQR;

import app.dcm.dto.StudyListDTO;
import app.dcm.qr.DcmQRconn;
import app.dcm.qr.DcmQRdatapresent;
import app.pgsql.DataBaseConn;

public class StudyListDAO {
	
	private static DcmQRconn qrConn = new DcmQRconn();
	private static DcmQRdatapresent qrData = new DcmQRdatapresent();
	private DataBaseConn conn = new DataBaseConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	public List<StudyListDTO> getStudyList(String studyID) throws Exception{
		List<StudyListDTO> studyList = new ArrayList<StudyListDTO>();
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT s.study_iuid, p.pat_name, p.pat_id, s.study_datetime, "
				+ " s.mods_in_study, s.study_desc, s.num_instances "
				+ " FROM study s "
				+ " JOIN patient p ON p.pk = s.patient_fk "
				+ " WHERE s.study_iuid = :studyID "
				.replace(":studyID", String.format("'%s'", studyID));
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			studyList.add(
				new StudyListDTO(
						rs.getString(1), 
						rs.getString(2), 
						rs.getString(3), 
						DATE_FORMAT.format(rs.getDate(4)), 
						rs.getString(5), 
						rs.getString(6),
						rs.getInt(7)
			));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return studyList;
	}
	
	public List<StudyListDTO> getStudyList() throws IOException{
		
		List<StudyListDTO> studyList = new ArrayList<StudyListDTO>();
		List<DicomObject> dcmResult = null;
		
		DcmQR dcmqr = qrConn.getDcmQR();
		qrData.setDataToRetrieve(dcmqr);
		
		try{
			dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.STUDY);
			
	        dcmqr.start();
	        dcmqr.open();
	        dcmResult = dcmqr.query();
	        dcmqr.get(dcmResult);
	        dcmqr.stop();
	        dcmqr.close();
	        
	        for(DicomObject dcmobj : dcmResult){
	        	studyList.add(
	        		new StudyListDTO(dcmobj.getString(Tag.StudyInstanceUID),
	        						 dcmobj.getString(Tag.PatientName),
	        						 dcmobj.getString(Tag.PatientID), 
	        						 dcmobj.getString(Tag.StudyDate), 
	        						 dcmobj.getString(Tag.ModalitiesInStudy), 
	        						 dcmobj.getString(Tag.StudyDescription),
	        						 dcmobj.getInt(Tag.NumberOfStudyRelatedInstances)
	        	));
	        }
		} catch(Exception ex) { ex.printStackTrace(); }
		
		return studyList;
	}
	
}
