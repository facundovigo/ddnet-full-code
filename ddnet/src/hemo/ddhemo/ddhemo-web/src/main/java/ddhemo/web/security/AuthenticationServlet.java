package ddhemo.web.security;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({"/authentication-servlet/login", "/authentication-servlet/logout"})
public class AuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@EJB
	private SecurityHelper securityHelper;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
				
		final HttpSession session = request.getSession();

		if (request.getRequestURI().endsWith("login")) {
			final String login = request.getParameterValues("login")[0];
			final String password = request.getParameterValues("password")[0];

			if (securityHelper.login(session, login, password)) {
				response.sendRedirect(request.getServletContext().getContextPath() + "/index.html");
			} else {
				response.sendRedirect(request.getServletContext().getContextPath() + "/login-error.html");
			}
		} else if (request.getRequestURI().endsWith("logout")) {
			securityHelper.logout(session);
			response.sendRedirect(request.getServletContext().getContextPath() + "/logout.html");
		}
	}
}
