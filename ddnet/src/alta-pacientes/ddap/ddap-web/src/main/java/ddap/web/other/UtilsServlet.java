package ddap.web.other;

import java.io.File;
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;

import ddap.ejb.ConfigurationManager;

@WebServlet({"/utils/mainlogo"})
public class UtilsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private ConfigurationManager configurationManager;	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		if (request.getRequestURI().endsWith("mainlogo")) {
			String logosFolder = configurationManager.getConfigurationItem("report.logos.source-path").getValue();			
			File logoFile = FileUtils.getFile(logosFolder, "institucional.jpg");		
			if (logoFile.exists()) { 
				byte[] imageBytes = FileUtils.readFileToByteArray(logoFile);
				response.setContentType("image/jpeg");
				response.setContentLength(imageBytes.length);
				response.getOutputStream().write(imageBytes);
				response.flushBuffer();
			}
			else {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
			}
		} else {
			response.setStatus(HttpStatus.SC_NOT_FOUND);
		}
		
	}
}
