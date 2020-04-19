package ddhemo.web.api.rest;

import java.io.File;
import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;

import ddhemo.ejb.ConfigurationManager;

@Stateless
@Path("/institutions")
public class Institution {

	@EJB
	private ConfigurationManager configurationManager;
		
	@GET
	@Path("{institution-id}/logo")
	@Produces("image/jpeg")	
	public Response getInstitutionLogo(@PathParam("institution-id") long institutionID) throws IOException {
		File logoFile = new File(getInstitutionLogoFile(configurationManager, institutionID));
		return logoFile.exists() ? Response.ok(FileUtils.readFileToByteArray(logoFile)).build() : Response.status(Status.NOT_FOUND).build();
	}
	
	public static String getInstitutionLogoFile(ConfigurationManager configurationManager, long institutionID) {
		try {
			String logosFolder = configurationManager
					.getConfigurationItem("report.logos.source-path").getValue();			
			return FileUtils.getFile(logosFolder, "INST" + institutionID + ".jpg").getCanonicalPath();
		} catch (Throwable t) {
			return "";
		}
	}
}
