package app.dcm.dao;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.tool.dcmqr.DcmQR;

import app.dcm.dto.SeriesListDTO;
import app.dcm.qr.DcmQRconn;
import app.dcm.qr.DcmQRdatapresent;
import app.pgsql.DataBaseConn;

public class SeriesListDAO {

	private static DcmQRconn qrConn = new DcmQRconn();
	private static DcmQRdatapresent qrData = new DcmQRdatapresent();
	private static InstanceListDAO instDAO = new InstanceListDAO();
	private DataBaseConn conn = new DataBaseConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	
	
	public List<SeriesListDTO> getSeriesList(String studyID) throws Exception {
		List<SeriesListDTO> seriesList = new ArrayList<SeriesListDTO>();
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT DISTINCT(se.series_iuid), se.series_desc, se.series_no, "
				+ " i.inst_custom3, i.inst_custom1 "
				+ " FROM series se "
				+ " JOIN study s ON s.pk = se.study_fk "
				+ " JOIN instance i ON se.pk = i.series_fk "
				+ " WHERE s.study_iuid = :studyID "
				//+ " ORDER BY se.series_no ASC "
				.replace(":studyID", String.format("'%s'", studyID));
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			seriesList.add(
				new SeriesListDTO(
						rs.getString(1), 
						rs.getString(2), 
						rs.getInt(3),
						rs.getString(4) != null ?
							rs.getInt(4) : 0,
						rs.getString(5) != null ? 
							(1000/rs.getInt(5)) : 0,
						instDAO.getInstanceList(rs.getString(1))
			));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return seriesList;
	}
	
	public List<SeriesListDTO> getSeriesList(String studyID, int uno) throws IOException{
		
		List<SeriesListDTO> seriesList = new ArrayList<SeriesListDTO>();
		List<DicomObject> dcmResult = null;
		
		DcmQR dcmqr = qrConn.getDcmQR();
		qrData.setDataToRetrieve(dcmqr);
		
		
		
		try{
			dcmqr.addMatchingKey(new int[]{Tag.StudyInstanceUID}, studyID);
			dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.SERIES);
			
	        dcmqr.start();
	        dcmqr.open();
	        dcmResult = dcmqr.query();
	        dcmqr.get(dcmResult);
	        dcmqr.stop();
	        dcmqr.close();
	        
	        for(DicomObject dcmobj : dcmResult){
	        	seriesList.add(new SeriesListDTO(
		        			dcmobj.getString(Tag.SeriesInstanceUID), 
		        			dcmobj.getString(Tag.SeriesDescription), 
		        			dcmobj.getInt(Tag.SeriesNumber),
		        			dcmobj.getInt(Tag.NumberOfFrames),
		        			dcmobj.getInt(Tag.RecommendedDisplayFrameRate),
		        			instDAO.getInstanceList(dcmobj.getString(Tag.SeriesInstanceUID))
				));
	        }
	        
		} catch(Exception ex) { ex.printStackTrace(); }
	        
		return seriesList;
	}
}
