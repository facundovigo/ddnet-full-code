package app.bd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import app.bd.conn.PgSQLConn;
import app.bd.dto.InstanceDTO;

public class InstanceDAO {
	
	private final PgSQLConn conn = new PgSQLConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;

	public List<InstanceDTO> getInstanceList(String seriesID) throws Exception {
		List<InstanceDTO> instanceList = new ArrayList<InstanceDTO>();
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
			" SELECT i.sop_iuid, i.inst_custom3, i.inst_custom1 "
			+ " FROM instance i "
			+ " JOIN series se ON se.pk = i.series_fk "
			+ " WHERE se.series_iuid = :seriesID "
			.replace(":seriesID", String.format("'%s'", seriesID));
		
		query += " ORDER BY i.sop_iuid ASC ";
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			instanceList.add( 
				new InstanceDTO( 
					rs.getString(1),
					0,//rs.getString(2)==null? 0 : rs.getInt(2),
					0//rs.getString(3)==null? 0 : (1000/rs.getInt(3))
				));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return instanceList;
	}
	
	public float getPixelSpacing(String imageID) throws Exception {
		String pixelSpacingStr = "";
		float pixelSpacing = 1;
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
			" SELECT i.inst_custom2 FROM instance i "
			+ " WHERE i.sop_iuid = :imageID "
			.replace(":imageID", String.format("'%s'", imageID));
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			pixelSpacingStr = rs.getString(1);
		}
		
		rs.close();
		st.close();
		conn.close();
		
		if(pixelSpacingStr == null || pixelSpacingStr.isEmpty()) return 0;
		
		float n = Float.parseFloat(pixelSpacingStr.split("e")[0]);
		float aux = (float) Math.pow(10, Integer.parseInt(pixelSpacingStr.split("e")[1]));
		pixelSpacing = n * aux;
				
		return pixelSpacing;
	}
}
