package ddap.web.api.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ddap.ejb.LoggerManager;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/log")
public class Log {
	
	private static final SimpleDateFormat DATE_PRINT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");
	private static final SimpleDateFormat DATE_INPUT_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private LoggerManager logManager;
	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<LogDTO> getLog(@Context HttpServletRequest request,
									 @QueryParam("user") String login,
									 @QueryParam("log-date-from") String strFrom,
									 @QueryParam("log-date-to") String strTo) throws ParseException{
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		Collection<LogDTO> log = new ArrayList<Log.LogDTO>();
		for(Logger l : logManager.getAll()){
			
			if(strFrom.equals("") && strTo.equals("")){
				
				if(login != null && !"".equals(login)){
					if(login.equals(l.getUser()))
						log.add(new LogDTO(	DATE_PRINT_FORMAT.format(DATE_LOG_FORMAT.parse(l.getDate())), 
											l.getUser(), l.getAction(), l.getDetails()));
				
				} else {
					log.add(new LogDTO(	DATE_PRINT_FORMAT.format(DATE_LOG_FORMAT.parse(l.getDate())), 
										l.getUser(), l.getAction(), l.getDetails()));
				}
			} else {
				
				Date dateFrom = DATE_INPUT_FORMAT.parse(strFrom);
				Date dateTo = DATE_INPUT_FORMAT.parse(strTo);
				String strLog = DATE_INPUT_FORMAT.format(DATE_LOG_FORMAT.parse(l.getDate()));
				Date logDate = DATE_INPUT_FORMAT.parse(strLog);
				
				if(logDate.compareTo(dateFrom) >= 0 && logDate.compareTo(dateTo) <= 0){
					
					if(login != null && !"".equals(login)){
						if(login.equals(l.getUser()))
							log.add(new LogDTO(	DATE_PRINT_FORMAT.format(DATE_LOG_FORMAT.parse(l.getDate())), 
												l.getUser(), l.getAction(), l.getDetails()));
					} else {
						log.add(new LogDTO(	DATE_PRINT_FORMAT.format(DATE_LOG_FORMAT.parse(l.getDate())), 
											l.getUser(), l.getAction(), l.getDetails()));
					}
				}
			}
		}
		return log;
	}
	
	
	
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<LogDTO> getAll( @Context HttpServletRequest request) throws ParseException{
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		Collection<LogDTO> log = new ArrayList<Log.LogDTO>();
		
		if(logManager.getAll() != null && !logManager.getAll().isEmpty()){
			for(Logger l : logManager.getAll()){
				
				log.add(new LogDTO(	DATE_PRINT_FORMAT.format(DATE_LOG_FORMAT.parse(l.getDate())), 
									l.getUser(), 
									l.getAction(), 
									l.getDetails())
				);
			}
			return log;
		}
		
		else return null;
	}
	
	
	
	
	
	public class LogDTO{
		
		private String date;
		private String user;
		private String action;
		private String details;
		
		public LogDTO(String date, String user, String action, String details){
			
			this.date = date;
			this.user = user;
			this.action = action;
			this.details = details;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getDetails() {
			return details;
		}

		public void setDetails(String details) {
			this.details = details;
		}
	}
}
