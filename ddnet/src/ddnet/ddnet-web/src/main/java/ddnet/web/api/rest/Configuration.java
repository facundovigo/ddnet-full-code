package ddnet.web.api.rest;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ddnet.ejb.ConfigurationManager;

@Stateless
@Path("/config")
public class Configuration {

	@EJB
	private ConfigurationManager configurationManager;
	
	@Path("all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ddnet.ejb.entities.Configuration> getConfiguration() {
		return configurationManager.getConfiguration();
	}
	
}
