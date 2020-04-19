package ddnet.web.api.rest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;

import ddnet.ejb.ConfigurationManager;
import ddnet.ejb.InstitutionManager;

@Stateless
@Path("/institutions")
public class Institution {

	@EJB
	private ConfigurationManager configurationManager;

	@EJB
	private InstitutionManager institutionManager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<InstitutionDTO> getInstitutions() {
		List<InstitutionDTO> institutions = new ArrayList<InstitutionDTO>();
		
		for(ddnet.ejb.entities.Institution institution : institutionManager.getAll())
			institutions.add(new InstitutionDTO(institution.getId(), institution.getName()));
		
		return institutions;
	}
	
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
	
	public class InstitutionDTO {
		private long id;
		private String name;
		
		public InstitutionDTO() {			
			
		}
		public InstitutionDTO(long id, String name) {			
			this.id = id;
			this.name = name;
		}
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}		
	}
}
