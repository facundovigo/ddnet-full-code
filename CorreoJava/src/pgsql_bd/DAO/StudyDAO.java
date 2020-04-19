package pgsql_bd.DAO;

import java.sql.*;
import java.util.ArrayList;

import pgsql_bd.ConexionBD;
import pgsql_bd.DTO.DatosClinicosDTO;
import pgsql_bd.DTO.LegacyPatientDTO;
import pgsql_bd.DTO.LegacyStudyDTO;

public class StudyDAO {
	
	ConexionBD conex = new ConexionBD();
	
	String query = "";
	
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	
	public Long getStudyByMail(Long mailID) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		long id = 0;
		
		query = " SELECT id FROM dd_study WHERE id = "
				+ " (SELECT study_id FROM dd_envio_correo "
					+ " WHERE id = SQLmailID) "
				.replace("SQLmailID", mailID.toString());
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()) id = rs.getLong(1);
		
		rs.close();
		st.close();
		conn.close();
		return id;
	}
	
	public ArrayList<LegacyStudyDTO> getLegacyStudyByPK(Long pk) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<LegacyStudyDTO> dto = new ArrayList<LegacyStudyDTO>();
		
		query = " SELECT * FROM study WHERE pk = SQLpk "
				.replace("SQLpk", pk.toString());
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new LegacyStudyDTO(
					rs.getLong("pk"), 
					rs.getLong("patient_fk"), 
					rs.getString("study_iuid"), 
					rs.getString("study_datetime"), 
					rs.getString("study_desc"), 
					rs.getString("mods_in_study")));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	public ArrayList<LegacyPatientDTO> getLegacyPatientByPK(Long pk) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<LegacyPatientDTO> dto = new ArrayList<LegacyPatientDTO>();
		
		query = " SELECT * FROM patient WHERE pk = SQLpk "
				.replace("SQLpk", pk.toString());
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new LegacyPatientDTO(
					rs.getLong("pk"), 
					rs.getString("pat_id"), 
					rs.getString("pat_name")
						.replace("^^^^", "")
						.replace("^^^", "")
						.replace("^^", "")
						.replace("^", " "), 
					rs.getString("pat_sex"), 
					rs.getString("pat_custom3")));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	public ArrayList<DatosClinicosDTO> getDatosClinicosByStudyID(Long studyID) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<DatosClinicosDTO> dto = new ArrayList<DatosClinicosDTO>();
		
		query = " SELECT * FROM dd_datos_clinicos "
				+ " WHERE id_study_datos_clinicos = SQLstudyID "
				.replace("SQLstudyID", studyID.toString());
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new DatosClinicosDTO(
					rs.getLong("id_datos_clinicos"), 
					rs.getLong("id_study_datos_clinicos"), 
					rs.getInt("prioridad_datos_clinicos"), 
					rs.getBoolean("cte_oral_datos_clinicos"), 
					rs.getBoolean("cte_ev_datos_clinicos")));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
}
