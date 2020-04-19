package pgsql_bd.DAO;

import java.sql.*;
import java.util.ArrayList;

import pgsql_bd.ConexionBD;
import pgsql_bd.DTO.StudyInfoDTO;

public class StudyInfoDAO {
	
	ConexionBD conex = new ConexionBD();
	
	String query = "";
	
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	
	
	public ArrayList<StudyInfoDTO> getDataByAccNumb(String accno) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<StudyInfoDTO> dto = new ArrayList<StudyInfoDTO>();
		
		query = " SELECT p.pat_name, s.study_datetime, s.mods_in_study, " +
				" s.pk, std.is_reported, std.report_body " +
				" FROM patient p " +
				" JOIN study s ON s.patient_fk = p.pk " +
				" JOIN dd_study std ON std.id = s.pk " +
				" WHERE s.accession_no ilike 'accnoParam' "
				.replace("accnoParam", accno);
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new StudyInfoDTO(
					rs.getLong(4), 
					rs.getString(1), 
					rs.getString(2), 
					rs.getString(3), 
					rs.getBoolean(5), 
					rs.getString(6)));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	public void ClearStudyReport(String pk) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		st.execute(" UPDATE dd_study SET is_reported = FALSE WHERE id = pkParam "
				.replace("pkParam", pk));
		st.execute(" UPDATE dd_study SET is_send_report = FALSE WHERE id = pkParam "
				.replace("pkParam", pk));
		
		st.close();
		conn.close();
	}
	
}
