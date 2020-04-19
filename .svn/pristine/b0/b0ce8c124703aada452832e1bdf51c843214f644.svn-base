package ddnet.web.api.rest;

import java.io.File;
import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ddnet.ejb.*;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("tools")
public class Tools {
	
	@EJB
	private StudyManager studyManager;
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UserManager usermanager;
	@EJB
	private ConfigurationManager configurationManager;
	
	@Path("download/{folder}")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDownloadFile(@Context HttpServletRequest request,
									@Context HttpServletResponse response,
									@PathParam("folder") String ubicacion) throws IOException {		
		
		final String dcm4cheUtilityFolder = configurationManager.getConfigurationItem("download.tools.path").getValue();
		File source = new File(dcm4cheUtilityFolder, ubicacion);
		
		if (source.exists() && source.isDirectory()){ 
			
			File[] f = source.listFiles();
			
			response.setContentType("application/zip");
			
			response.addHeader("Content-Disposition", "attachment; filename=\"ddnet-useragent.zip\"");
			
			response.setHeader("Content-Length", String.valueOf(f[0].length()));
			
			return Response.ok(f[0]).build();	}
		
		else return Response.status(Status.NO_CONTENT).build();
		
	}
	
}
