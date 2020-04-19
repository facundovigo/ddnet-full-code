package ddnet.web.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/restore-servlet/new")
public class RestoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private RestorePassword restore;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		final String key = request.getParameter("key");
		
		com.google.gson.Gson gson = new com.google.gson.Gson();
		String strJson = gson.toJson(restore.getPRData(key));
		
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
			out.print(strJson);
			out.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		final String key = request.getParameter("key");
		final String userID = request.getParameter("userID");
		final String newPassword = request.getParameter("newpwd");
		
		restore.changeThePassword(key, userID, newPassword);
	}
}
