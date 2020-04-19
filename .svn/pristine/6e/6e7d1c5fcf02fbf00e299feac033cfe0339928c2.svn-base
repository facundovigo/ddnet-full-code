package wsreceiver;

import java.io.File;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;

@Path("posts")
public class Posts {
	
	@Context
	private ServletContext context;	
	
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void Post(String message) {
		try {
			String rootFolder = context.getInitParameter("ddreceiver.receive.path");
			String encoding = context.getInitParameter("ddreceiver.receive.encoding");
			File outputFile = new File(rootFolder, UUID.randomUUID().toString() + ".txt");
			FileUtils.writeStringToFile(outputFile, message, encoding);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}
