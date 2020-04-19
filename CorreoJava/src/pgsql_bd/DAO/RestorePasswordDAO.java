package pgsql_bd.DAO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pgsql_bd.ConexionBD;
import pgsql_bd.DTO.RestorePasswordDTO;

public class RestorePasswordDAO {
	
	ConexionBD conex = new ConexionBD();
	
	String query = "";
	
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
							 R2 = ResultSet.CONCUR_READ_ONLY;
	
	//private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	public ArrayList<RestorePasswordDTO> getRestorePasswordByMailID(Long mailID) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<RestorePasswordDTO> dto = new ArrayList<RestorePasswordDTO>();
		
		query = " SELECT * FROM dd_password_restore "
				+ " WHERE restore_key = "
				+ " (SELECT restore_uuid from dd_envio_correo "
				+ " WHERE id = SQLmailID) "
				.replace("SQLmailID", mailID.toString());
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new RestorePasswordDTO(
					rs.getLong("id"),
					rs.getLong("user_id"),
					rs.getString("restore_key"),
					rs.getBoolean("restore_done"),
					rs.getInt("restore_times"),
					rs.getString("restore_init_date"),
					rs.getString("restore_limit_date"),
					rs.getString("restore_host_requested")));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	
}
