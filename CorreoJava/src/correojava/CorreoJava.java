package correojava;

import java.sql.SQLException;

import pgsql_bd.DAO.MailDAO;
import app_objetos.*;

public class CorreoJava /*extends JFrame implements ActionListener*/{
	
	Correo c = new Correo();
	MailDAO dao = new MailDAO();
	
	public CorreoJava(){
		
	}
	
	public static void main(String[] args) {
		
		new app_Frame();
	}

	
	public void EnviarCorreo(String asunto, String mensaje, String destino, Long mailID) throws SQLException{
		
		//c.setPassword("2deabril");
		//c.setUsuarioCorreo("sistematelelaudo@gmail.com");
		c.setAsunto(asunto);
		c.setMensaje(mensaje);
		c.setDestino(destino.trim());
		
		Controlador contr = new Controlador();
		if(contr.enviarCorreo(c, mailID)) dao.setMailAsReceived(mailID);
	}

}
