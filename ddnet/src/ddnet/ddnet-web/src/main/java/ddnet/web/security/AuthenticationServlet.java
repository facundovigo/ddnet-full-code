package ddnet.web.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({"/authentication-servlet/login", "/authentication-servlet/logout", "/authentication-servlet/restore"})
public class AuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private RestorePassword restore;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
				
		final HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		
		if (request.getRequestURI().endsWith("login")) {
			final String login = request.getParameterValues("login")[0];
			final String password = request.getParameterValues("password")[0];

			int ok = securityHelper.login(session, login, password);
			
			if (ok > 0) {
				
				switch(ok){
				
					case 1: response.sendRedirect(request.getServletContext().getContextPath() + "/index.html");
							break;
					case 2: response.sendRedirect(request.getServletContext().getContextPath() + "/index1.html");
							break;
					default: response.sendRedirect(request.getServletContext().getContextPath() + "/login-error.html");
							 break;
				}
			} else {
				response.sendRedirect(request.getServletContext().getContextPath() + "/login-error.html");
			}
		} else if (request.getRequestURI().endsWith("logout")) {
			securityHelper.logout(session);
			response.sendRedirect(request.getServletContext().getContextPath() + "/logout.html");
		}
		
		
		else if (request.getRequestURI().endsWith("restore")) {
			
			final String dato = request.getParameter("dato");	// tomo el dato ingresado por el cliente
			final String op = request.getParameter("op");		// tomo la opción seleccionada
			final String host = request.getParameter("host");	// tomo el host donde se hizo el pedido
			final int opcion = Integer.parseInt(op);
			final int flag = restore.flagDato(dato, opcion);	// flag que indica si el dato es válido
			boolean hecho = false;
			
			if(flag==1){
				switch(opcion){
					case 1:	hecho = restore.generatePasswordRestore1(dato, host);
							break;
					case 2: hecho = restore.generatePasswordRestore2(dato, host);
							break;
					default: break;
				}
			}
			else{}
			
			out.println("flag=" +flag+ ";hecho=" +hecho);	// retorno el flag
			out.close();
		}
	}
}
