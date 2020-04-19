package ddap.web.api.rest;


import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ddap.ejb.EquiposManager;
import ddap.ejb.LoggerManager;
import ddap.ejb.UsuariosManager;
import ddap.ejb.entities.Equipo;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.Usuario;
import ddap.web.security.SecurityHelper;

@Stateless
@Path("/equipo")
public class Equipment {
	
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UsuariosManager userManager;
	@EJB
	private EquiposManager eManager;
	@EJB
	private LoggerManager logManager;
	
	@POST
	@Path("/new/{flag}")
	public void addNewEquipment(@Context HttpServletRequest request,
								@FormParam("modalidad") String mod,
								@FormParam("codigo") String code,
								@FormParam("marca") String trademark,
								@FormParam("modelo") String model,
								@FormParam("horas") String h,
								@FormParam("minutos") String m,
								@FormParam("segundos") String s,
								@PathParam("flag") int flag){
		Usuario u = securityHelper.getUser(request.getSession());
		
		Equipo e = new Equipo();
		
		boolean isNew = flag == 1;
		
		if(isNew){
			if(code != null && !code.isEmpty())
				e.setCode(code);
		
		} else {
			e = eManager.getByCode(code);
		}
		
		if(mod != null && !mod.isEmpty())
			e.setMod(mod);
		if(trademark != null && !trademark.isEmpty())
			e.setTrademark(trademark);
		if(model != null && !model.isEmpty())
			e.setModel(model);
		
		String time = "";
		
		if(h != null && !h.isEmpty()) {
			
			if(Integer.parseInt(h) < 10) {
				h = String.format("%01d", Integer.parseInt(h));
				time += "0"+h ;
			}
			else time += h ;
			
		}	else time += "00";
		
		time += ":";
		
		if(m != null && !m.isEmpty()) {
			
			if(Integer.parseInt(m) < 10){
				m = String.format("%01d", Integer.parseInt(m));
				time += "0"+m ;
			}
			else time += m ;
			
		}	else time += "00";
		
		time += ":";
		
		if(s != null && !s.isEmpty()) {
			
			if(Integer.parseInt(s) < 10){
				s = String.format("%01d", Integer.parseInt(s));
				time += "0"+s ;
			}
			else time += s ;
			
		}	else time += "00";
		
		e.setTime(time);
		
		if(isNew){
			eManager.persist(e);
			recordInLog(u.getLogin(), "ALTA DE EQUIPO", model);
		
		} else recordInLog(u.getLogin(), "MODIFICACIÓN EQUIPO", model);
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Equipo> getAll( @Context HttpServletRequest request){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(!eManager.getAll().isEmpty()) return eManager.getAll();
		
		else return null;
	}
	
	@GET
	@Path("/mods")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getModalities( @Context HttpServletRequest request){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(eManager.getModalities() != null && !eManager.getModalities().isEmpty()) 
			return eManager.getModalities();
		
		else return null;
	}
	
	@GET
	@Path("/{mod-name}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Equipo> getByModality( @Context HttpServletRequest request,
												@PathParam("mod-name") String mod){
		
		@SuppressWarnings("unused")
		Usuario u = securityHelper.getUser(request.getSession());
		
		if(!eManager.getByModality(mod).isEmpty()) return eManager.getByModality(mod);
		
		else return null;
	}
	
	
	@GET
	@Path("/last-code")
	public String getLastCode(){
		String str = eManager.getLastCode().replace("[","").replace("]","");
		str = str.equals("null") ? "0" : str;
		
		return String.format("%03d", Integer.parseInt(str) + 1);
	}
	
	
	@POST
	@Path("/delete/{code}")
	public Response deleteEq(@Context HttpServletRequest request,
							 @PathParam("code") String code){
		Usuario u = securityHelper.getUser(request.getSession());
		
		Equipo e = eManager.getByCode(code);
		eManager.remove(e);
		
		recordInLog(u.getLogin(), "ELIMINACIÓN EQUIPO", e.getModel());
		
		return Response.ok().build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void recordInLog(String login, String accion, String detalle){
		
		Logger log = new Logger();
		
			log.setDate(DATE_LOG_FORMAT.format(new Date()));
			log.setUser(login);
			log.setAction(accion);
			log.setDetails(detalle);
			
		logManager.persist(log);
	}
}

























