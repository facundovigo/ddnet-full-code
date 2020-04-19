package ddnet.web.security;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

@WebServlet({"/basic-authentication-servlet"})
public class BasicAuthenticationServlet extends HttpServlet {
	private final static long serialVersionUID = 1L;

	@EJB
	private SecurityHelper securityHelper;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		
		final String operation = request.getParameter("operation");		
		final HttpSession session = request.getSession();
		boolean loginOK = false;
		
		if ("logon".equalsIgnoreCase(operation)) {
			String login = null;
			String password = null;

			String authorizationHeader = request.getHeader("Authorization");			
			if (authorizationHeader != null) {
				StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
				if (tokenizer.hasMoreTokens()) {
					String authenticationMethod = tokenizer.nextToken();

					if ("Basic".equalsIgnoreCase(authenticationMethod)) {
						String authenticationData = new String(Base64.decodeBase64(tokenizer.nextToken()));

						int separatorPosition = authenticationData.indexOf(":");
						if (separatorPosition >= 0) {
							login = authenticationData.substring(0, separatorPosition);
							password = authenticationData.substring(separatorPosition + 1);

							try {
								loginOK = securityHelper.login(session, login, password);
							} catch (Throwable t) {
								loginOK = false;
							}
						}		               
					}
				}
			}

			// If the logon did not succeded, signal the need for authentication to the client.
			if (!loginOK) {
				response.setStatus(401);
				response.setHeader("WWW-Authenticate", "Basic realm=\"DDNET\"");
			}
		} else if ("logout".equalsIgnoreCase(operation)) {
			securityHelper.logout(session);
			loginOK = false;
		} else if ("status".equalsIgnoreCase(operation)) {
			loginOK = securityHelper.isAuthenticated(session);
		} else {
			throw new ServletException("Invalid operation: [" + operation + "]");
		}
				
		response.setHeader("X-DDNET-IsAuthenticated", ((Boolean)loginOK).toString().toLowerCase());
	}
}