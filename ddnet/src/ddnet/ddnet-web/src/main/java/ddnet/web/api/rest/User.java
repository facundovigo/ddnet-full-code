package ddnet.web.api.rest;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ddnet.ejb.UserManager;
import ddnet.ejb.entities.UserProperty;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("")
public class User {

	@EJB
	private SecurityHelper securityHelper;

	@EJB
	private UserManager userManager;
	
	@Path("/users/assign-targets")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getStudyAssignTargets(@Context HttpServletRequest request) {
		Collection<String> result = new ArrayList<String>();
		
		for(ddnet.ejb.entities.User user : userManager.getAll())
			result.add(user.getLogin());
		
		return result;
	}
	
	@Path("/user/info")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ddnet.ejb.entities.User getCurrentUser(@Context HttpServletRequest request) {
		return securityHelper.getUser(request.getSession());
	}
	
	@Path("/user/info/properties")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserProperty getUserProperties(@Context HttpServletRequest request) {
		
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		return user.getProp()!=null?user.getProp():null;
	}
	
	@Path("/user/info/properties")
	@POST
	public void setUserProperties(@Context HttpServletRequest request,
									@FormParam("host") String hostname,
									@FormParam("port") String port,
									@FormParam("aet") String aet,
									@FormParam("caet") String calling_aet){
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		
		if(user.getProp() != null){
			
			user.getProp().setHostname(hostname);
			user.getProp().setPort(port);
			user.getProp().setAet(aet);
			user.getProp().setCallingAet(calling_aet);
			
		}else{
			
			UserProperty prop = new UserProperty();
			
			prop.setUserID(user.getId());
			prop.setHostname(hostname);
			prop.setPort(port);
			prop.setAet(aet);
			prop.setCallingAet(calling_aet);
			
			userManager.persist(prop);
		}
		
	}
	
	@GET
	@Path("/user/medicos")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ddnet.ejb.entities.User> getMedicos(@Context HttpServletRequest request){
		
		@SuppressWarnings("unused")
		ddnet.ejb.entities.User u = securityHelper.getUser(request.getSession());
		
		if(userManager.getMedicos() != null && !userManager.getMedicos().isEmpty())
			return userManager.getMedicos();
		
		else return null;
	}
	
}
