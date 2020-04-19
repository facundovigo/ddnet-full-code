package app.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Visualizador
 */
public class Visualizador extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Visualizador() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String studyId = request.getParameter("studyUID");
		
		if(studyId==null || studyId.isEmpty()){
			this.getServletContext().getRequestDispatcher("/url-error.html").forward(request, response);
			
		} else {
			this.getServletContext().getRequestDispatcher("/single-viewer.html").forward(request, response);
		}
	}

}
