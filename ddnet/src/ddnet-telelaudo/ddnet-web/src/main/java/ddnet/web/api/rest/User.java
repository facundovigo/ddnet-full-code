package ddnet.web.api.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	public List<String> getUserProperties(@Context HttpServletRequest request) {
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		
		List<String> userProp = new ArrayList<String>();	
	
	if(user.getProp() != null){
		
		userProp.add(user.getProp().getHostname());
		userProp.add(user.getProp().getAet());
		userProp.add(user.getProp().getPort());
		userProp.add(user.getProp().getCallingAet());
	
	}else{
		
		userProp.add("");
		userProp.add("");
		userProp.add("");
		userProp.add("");
	}
		
		return userProp;
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
/*
	@Path("/user/info/properties/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProperties(@Context HttpServletRequest request) {
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		if (user == null)
			return Response.status(Status.NOT_FOUND).build();
		return Response.ok(user.getProperties()).build();
	}
	
	@Path("/user/info/properties/{property-name}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUserProperty(@Context HttpServletRequest request, 
			@PathParam("property-name") String propertyName) {
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		if (user == null)
			return Response.status(Status.NOT_FOUND).build();
		
		final String value = user.getProperty(propertyName);
		if (value == null)
			return Response.status(Status.NOT_FOUND).build();
		
		return Response.ok(value).build();
	}
	
	@Path("/user/info/properties/{property-name}")
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
	public Response setUserProperty(@Context HttpServletRequest request, 
			@PathParam("property-name") String propertyName, 
			@FormParam("property-value")String propertyValue) {
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		if (user == null)
			return Response.status(Status.NOT_FOUND).build();
		
		user.setProperty(propertyName, propertyValue);

		return Response.ok().build();
	}

	@Path("/user/info/properties")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setUserProperties(@Context HttpServletRequest request, 
			Map<String, String> properties) {
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		if (user == null)
			return Response.status(Status.NOT_FOUND).build();
		
		for(String propertyName : properties.keySet())
			user.setProperty(propertyName, properties.get(propertyName));

		return Response.ok().build();
	}
	

	@Path("/user/info/properties/{property-name}")
	@DELETE
	public Response deleteUserProperty(@Context HttpServletRequest request, 
			@PathParam("property-name") String propertyName) {
		final ddnet.ejb.entities.User user = securityHelper.getUser(request.getSession());
		if (user == null)
			return Response.noContent().build();
		
		if (!user.hasProperty(propertyName))
			return Response.noContent().build();
		
		user.deleteProperty(propertyName);
		
		return Response.ok().build();
	}*/	
	
}