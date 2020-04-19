package ddhemo.web.api.rest;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ddhemo.ejb.UserManager;
import ddhemo.web.security.SecurityHelper;

@Stateless
@Path("/user")
public class User {

	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UserManager usermanager;
	
	@Path("info")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ddhemo.ejb.entities.User getCurrentUser(@Context HttpServletRequest request) {
		return securityHelper.getUser(request.getSession());
	}
	
	@Path("all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ddhemo.ejb.entities.User> getAll() {
		
		return usermanager.getAll();
	}
	
}
