package app.bd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import app.bd.conn.PgSQLConn;
import app.bd.dto.SeriesDTO;
import app.bd.dao.InstanceDAO;

public class SeriesDAO {

	private final PgSQLConn conn = new PgSQLConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	private static final InstanceDAO instDAO = new InstanceDAO();
	
	public List<SeriesDTO> getSeriesList(String studyID) throws Exception {
		List<SeriesDTO> seriesList = new ArrayList<SeriesDTO>();
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT DISTINCT(se.series_iuid), se.series_desc, "
				+ " se.series_no, se.num_instances "
				//+ " i.inst_custom3, i.inst_custom1 "
				+ " FROM series se "
				+ " JOIN study s ON s.pk = se.study_fk "
				//+ " JOIN instance i ON se.pk = i.series_fk "
				+ " WHERE s.study_iuid = :studyID "
				.replace(":studyID", String.format("'%s'", studyID));
		
		query += " ORDER BY se.series_iuid ASC ";
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			seriesList.add(
				new SeriesDTO(
						rs.getString(1), 
						rs.getString(2), 
						rs.getInt(3),
						rs.getInt(4),
						instDAO.getInstanceList(rs.getString(1))
			));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return seriesList;
	}
}
