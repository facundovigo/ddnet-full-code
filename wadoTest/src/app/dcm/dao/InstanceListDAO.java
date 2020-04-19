package app.dcm.dao;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.tool.dcmqr.DcmQR;

import app.dcm.dto.InstanceListDTO;
import app.dcm.qr.DcmQRconn;
import app.dcm.qr.DcmQRdatapresent;
import app.pgsql.DataBaseConn;

public class InstanceListDAO {

	private static DcmQRconn qrConn = new DcmQRconn();
	private static DcmQRdatapresent qrData = new DcmQRdatapresent();
	private DataBaseConn conn = new DataBaseConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	
	public List<InstanceListDTO> getInstanceList(String seriesID) throws Exception {
		List<InstanceListDTO> instanceList = new ArrayList<InstanceListDTO>();
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT i.sop_iuid "
				+ " FROM instance i "
				+ " JOIN series se ON se.pk = i.series_fk "
				+ " WHERE se.series_iuid = :seriesID "
				.replace(":seriesID", String.format("'%s'", seriesID));
		
		query += " ORDER BY i.inst_no ASC ";
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			instanceList.add( new InstanceListDTO( rs.getString(1) ));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return instanceList;
	}
	
	public List<InstanceListDTO> getInstanceList(String seriesID, int uno) throws IOException{
		
		List<InstanceListDTO> instanceList = new ArrayList<InstanceListDTO>();
		List<DicomObject> dcmResult = null;
		
		DcmQR dcmqr = qrConn.getDcmQR();
		qrData.setDataToRetrieve(dcmqr);
		
		try{
			dcmqr.addMatchingKey(new int[]{Tag.SeriesInstanceUID}, seriesID);
			dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.IMAGE);
			
	        dcmqr.start();
	        dcmqr.open();
	        dcmResult = dcmqr.query();
	        dcmqr.get(dcmResult);
	        dcmqr.stop();
	        dcmqr.close();
	        
	        for(DicomObject dcmobj : dcmResult){
	        	instanceList.add(
	        		new InstanceListDTO(dcmobj.getString(Tag.SOPInstanceUID))
	        	);
	        }
	        
		} catch(Exception ex) { ex.printStackTrace(); }
		
		return instanceList;
	}
}
