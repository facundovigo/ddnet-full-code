package app.bd.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.bd.conn.PgSQLConn;

public class ModalityDAO {

	private final PgSQLConn conn = new PgSQLConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	
	public List<String> getModalities() throws Exception {
		List<String> result = new ArrayList<String>();
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT DISTINCT(s.mods_in_study) "
				+ " FROM study s "
				+ " ORDER BY s.mods_in_study ";
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			result.add(rs.getString(1));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return result;
	}
}
