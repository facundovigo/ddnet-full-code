package correojava;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;






//import javax.activation.DataHandler;
//import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import pgsql_bd.DAO.MailDAO;

public class Controlador {
	
	
	public boolean enviarCorreo(Correo c, Long mailID){
		
		try{
			
			//Properties p = new Properties();
			Properties prop = new Properties();
			
			prop.put( "mail.smtp.host" , "smtp.gmail.com" );
			prop.load(new FileInputStream(new File ("config/config.ini")));
			//p.setProperty( "mail.smtp.starttls.enable" , "true" );
			//p.setProperty( "mail.smtp.port" , "587" );
			//p.setProperty( "mail.smtp.user" , c.getUsuarioCorreo());
			//p.setProperty( "mail.smtp.auth" , "true" );
			
			Session s = Session.getDefaultInstance(prop, null);
			BodyPart texto = new MimeBodyPart();
			texto.setText(c.getMensaje());
			//BodyPart adjunto = new MimeBodyPart();
			
			/*if(!c.getRutaArchivo().equals("")){
				adjunto.setDataHandler(new DataHandler(new FileDataSource(c.getRutaArchivo())));
				adjunto.setFileName(c.getNombreArchivo()); 
			}*/
			
			MimeMultipart m = new MimeMultipart();
			m.addBodyPart(texto);
			/*if(!c.getRutaArchivo().equals("")){
				m.addBodyPart(adjunto);
			}*/
			
			MimeMessage mensaje = new MimeMessage(s);
			mensaje.setFrom(new InternetAddress(prop.getProperty("user")));
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(c.getDestino()));
			mensaje.setSubject(c.getAsunto());
			//mensaje.setContent(m);
			mensaje.setText(c.getMensaje(),"ISO-8859-1","html");
			
			Transport t = s.getTransport("smtp");
			t.connect(prop.getProperty("user"), prop.getProperty("password"));
			t.sendMessage(mensaje, mensaje.getAllRecipients());
			t.close();
			
			return true;
			
		}catch(Exception e){
			
			try {new MailDAO().setMailErrorDetail(mailID, e.getMessage());} catch (SQLException q) {}
			return false;
		}
		
	}
}
