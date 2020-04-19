package pgsql_bd.DAO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pgsql_bd.ConexionBD;
import pgsql_bd.DTO.MailDTO;

public class MailDAO {
	
	ConexionBD conex = new ConexionBD();
	
	String query = "";
	
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
							 R2 = ResultSet.CONCUR_READ_ONLY;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	
	
	public ArrayList<MailDTO> getNoSentMails() throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<MailDTO> dto = new ArrayList<MailDTO>();
		
		query = " SELECT * FROM dd_envio_correo "
				+ " WHERE mail_envio = false AND mail_llego = false "
				+ " ORDER BY mail_fecha_registro ";
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new MailDTO(
					rs.getLong("id"),
					rs.getLong("study_id"),
					rs.getLong("user_id"),
					rs.getInt("mail_asunto"),
					rs.getBoolean("mail_envio"),
					rs.getBoolean("mail_llego"),
					rs.getString("mail_destino"),
					rs.getString("mail_fecha_registro"),
					rs.getString("mail_fecha_envio"),
					rs.getString("mail_detalle_error"),
					rs.getString("restore_uuid")));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	public ArrayList<MailDTO> getReceivedMails() throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<MailDTO> dto = new ArrayList<MailDTO>();
		
		query = " SELECT * FROM dd_envio_correo "
				+ " WHERE mail_envio = true AND mail_llego = true "
				+ " ORDER BY mail_fecha_registro ";
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new MailDTO(
					rs.getLong("id"),
					rs.getLong("study_id"),
					rs.getLong("user_id"),
					rs.getInt("mail_asunto"),
					rs.getBoolean("mail_envio"),
					rs.getBoolean("mail_llego"),
					rs.getString("mail_destino"),
					rs.getString("mail_fecha_registro"),
					rs.getString("mail_fecha_envio"),
					rs.getString("mail_detalle_error"),
					rs.getString("restore_uuid")));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	public ArrayList<MailDTO> getNotReceivedMails() throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		ArrayList<MailDTO> dto = new ArrayList<MailDTO>();
		
		query = " SELECT * FROM dd_envio_correo "
				+ " WHERE mail_envio = true AND mail_llego = false "
				+ " ORDER BY mail_fecha_registro ";
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto.add(new MailDTO(
					rs.getLong("id"),
					rs.getLong("study_id"),
					rs.getLong("user_id"),
					rs.getInt("mail_asunto"),
					rs.getBoolean("mail_envio"),
					rs.getBoolean("mail_llego"),
					rs.getString("mail_destino"),
					rs.getString("mail_fecha_registro"),
					rs.getString("mail_fecha_envio"),
					rs.getString("mail_detalle_error"),
					rs.getString("restore_uuid")));
		}
		
		rs.close();
		st.close();
		conn.close();
		return dto;
	}
	
	public void setMailAsSent(Long id) throws SQLException {
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		st.execute(
				" UPDATE dd_envio_correo SET mail_envio = true "
				+ " WHERE id = SQLid "
						.replace("SQLid", id.toString()));
		
		st.close();
		conn.close();
	}
	
	public void setMailAsReceived(Long id) throws SQLException {
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		st.execute(
				" UPDATE dd_envio_correo SET mail_llego = true "
				+ " WHERE id = SQLid "
						.replace("SQLid", id.toString()));
		
		st.close();
		conn.close();
	}
	
	public void setMailErrorDetail(Long id, String error) throws SQLException {
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		query = " UPDATE dd_envio_correo SET mail_detalle_error = ':error' WHERE id = SQLid ";
		
		query = query.replace(":error", error);
		
		st.execute(query.replace("SQLid", id.toString()));
		
		st.close();
		conn.close();
	}
	
	public void setDateMailSent(Long id) throws SQLException{
		
		Connection conn = conex.conn();
		Statement st = conn.createStatement(R1,R2);
		
		query = " UPDATE dd_envio_correo SET mail_fecha_envio = ':fecha' WHERE id = SQLid ";
				
		query = query.replace(":fecha", DATE_FORMAT.format(new Date()));
						
		st.execute(query.replace("SQLid", id.toString()));
		
		st.close();
		conn.close();
	}
	
}
