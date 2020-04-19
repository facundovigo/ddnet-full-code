package pgsql_bd.DAO;

import java.sql.*;
import java.util.ArrayList;

import pgsql_bd.ConexionBD;
import pgsql_bd.DTO.UserDTO;

public class UserDAO {
	
	ConexionBD conex = new ConexionBD();
	
	String query = "";
	
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	
	public ArrayList<UserDTO> getUserByMailID(Long mailID) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<UserDTO> dto = new ArrayList<UserDTO>();
		
		query = " SELECT * FROM dd_user WHERE id = "
				+ " (SELECT user_id FROM dd_envio_correo "
					+ " WHERE id = SQLid) "
				.replace("SQLid", mailID.toString());
				
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new UserDTO(rs.getLong("id"), 
								rs.getString("first_name"), 
								rs.getString("last_name"), 
								rs.getString("login"), 
								rs.getString("password"), 
								rs.getBoolean("deleted"))); 
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	
}
