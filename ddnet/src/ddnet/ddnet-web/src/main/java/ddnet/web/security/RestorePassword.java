package ddnet.web.security;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.codec.digest.DigestUtils;

import ddnet.ejb.EnvioCorreoManagerLocal;
import ddnet.ejb.PasswordRestoreManagerLocal;
import ddnet.ejb.UserManagerLocal;
import ddnet.ejb.entities.EnvioCorreo;
import ddnet.ejb.entities.PasswordRestore;
import ddnet.ejb.entities.User;


@Stateless
@LocalBean
public class RestorePassword {
	
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	@EJB
	private UserManagerLocal userManager;
	@EJB
	private PasswordRestoreManagerLocal restoreManager;
	@EJB
	private EnvioCorreoManagerLocal correoManager;
	
/**
 * Funciones en Java para recuperar contraseñas
 * 	
 */
	
// Levantar un flag si el dato ingresado por el cliente es correcto
	public int flagDato(String dato, int op) {
	
	int flag = 0;												// declaro el flag
	Collection<User> us = userManager.getAll();					// tomo todos los usuarios
	
	if(op==1){													// si pidió recuperar por nombre de usuario
	
		for(User u : us)										// recorro los usuarios
			if(u.getLogin().equals(dato))						// si coincide el dato con el login de un usuario
				flag = 1;										// levanta el flag
	}
	else if(op==2){												// si pidió recuperar por dirección de correo
		
		for(User u : us){
			if(u.getPerfil() != null && !u.getPerfil().getEmail().isEmpty()){
				
				if(u.getPerfil().getEmail().equals(dato))			// si coinciden los mails levanta el flag
					flag = 1;										// levanta el flag
			}
		}
	}
		
	return flag;
	}
	
	
// Generar los datos para restaurar contraseña op1
	
	public boolean generatePasswordRestore1(String dato, String host){
		
		User user = userManager.getByLogin(dato);					// tomar usuario por dato ya comprobado
		
		if(user.getPerfil() == null)								// si no tiene un perfil
				return false;										// no llevar a cabo la acción
		
		else{
			PasswordRestore restore = new PasswordRestore();
			String mail = user.getPerfil().getEmail();
		
			if(mail.isEmpty()) return false;						// si no hay e-mail no ejecutar acción
			else{													
				// cargar los datos para la tabla dd_restore_password
				restore.setUserID(user.getId());					
				
				String uuid = java.util.UUID.randomUUID().toString();
				restore.setKey(uuid);
				
				// cargar la fecha de pedido y 24hs de plazo
				Calendar calendar = Calendar.getInstance();
				restore.setInitDate(DATE_LOG_FORMAT.format(calendar.getTime()));
				calendar.add(Calendar.DATE, 1);
				restore.setLimitDate(DATE_LOG_FORMAT.format(calendar.getTime()));
				
				restore.setHost(host);
				
				// insert en la tabla
				restoreManager.persist(restore);
				
				// parámetros para el correo
				EnvioCorreo correo = new EnvioCorreo();
				correo.setRestoreUUID(uuid);
				correo.setUserID(user.getId());
				correo.setSubject(4);
				correo.setReceiver(mail);
				correo.setDateRecord(DATE_LOG_FORMAT.format(new Date()));
				
				correoManager.persist(correo);
			}
		
				return true;
		}
	}

// Generar los datos para restaurar contraseña op2
	public boolean generatePasswordRestore2(String dato, String host){
		
		String mail = dato;										// dirección de correo ingresada
		User user = null;
		Collection<User> us = userManager.getAll();				
		PasswordRestore restore = new PasswordRestore();
		
		for(User u : us){											//recorrer todos los usuarios
			
			if(u.getPerfil() != null && !u.getPerfil().getEmail().isEmpty()){
				
				if(u.getPerfil().getEmail().equals(dato))			// si coinciden los mails
					user = u;										// toma el usuario
			}
		}
		
		if(user != null){										// si encotró un usuario
			
			// cargar los datos para la tabla dd_restore_password
			restore.setUserID(user.getId());					
			
			String uuid = java.util.UUID.randomUUID().toString();
			restore.setKey(uuid);
			
			// cargar la fecha de pedido y 24hs de plazo
			Calendar calendar = Calendar.getInstance();
			restore.setInitDate(DATE_LOG_FORMAT.format(calendar.getTime()));
			calendar.add(Calendar.DATE, 1);
			restore.setLimitDate(DATE_LOG_FORMAT.format(calendar.getTime()));
			
			restore.setHost(host);
			
			// insert en la tabla
			restoreManager.persist(restore);
			
			// parámetros para el correo
			EnvioCorreo correo = new EnvioCorreo();
			correo.setRestoreUUID(uuid);
			correo.setUserID(user.getId());
			correo.setSubject(4);
			correo.setReceiver(mail);
			correo.setDateRecord(DATE_LOG_FORMAT.format(new Date()));
			
			correoManager.persist(correo);

			return true;
		}
		
		else return false;										// por las dudas que haya fallado
	}
	
	public Collection<PasswordRestoreDTO> getPRData(String key){
		
		Collection<PasswordRestoreDTO> cpr = new ArrayList<PasswordRestoreDTO>();
		PasswordRestore pr = restoreManager.getByKey(key);
		
		if(pr != null) {
			
			pr.setTimes(pr.getTimes() + 1);
			
			cpr.add(new PasswordRestoreDTO(
							pr.getUserID(), 
							pr.getKey(), 
							pr.isDone(), 
							pr.getTimes(), 
							pr.getLimitDate()));
		}
		
		return cpr;
	}
	
	public void changeThePassword(String key, String userID, String newPassword){
		
		User user = userManager.getByID(Long.parseLong(userID));
		
		user.setPassword(DigestUtils.md5Hex(newPassword));
		
		PasswordRestore pr = restoreManager.getByKey(key);
		
		pr.setDone(true);
		pr.setTimes(5);
	}

	public class PasswordRestoreDTO{
		
		private long userID;
		private String key;
		private boolean done;
		private int times;
		private String limitDate;
		
		public PasswordRestoreDTO(long userID, String key, boolean done,
									int times, String limitDate){
			this.userID = userID;
			this.key = key;
			this.done = done;
			this.times = times;
			this.limitDate = limitDate;
		}

		public long getUserID() {
			return userID;
		}

		public void setUserID(long userID) {
			this.userID = userID;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public boolean isDone() {
			return done;
		}

		public void setDone(boolean done) {
			this.done = done;
		}

		public int getTimes() {
			return times;
		}

		public void setTimes(int times) {
			this.times = times;
		}

		public String getLimitDate() {
			return limitDate;
		}

		public void setLimitDate(String limitDate) {
			this.limitDate = limitDate;
		}
		
	}
}
