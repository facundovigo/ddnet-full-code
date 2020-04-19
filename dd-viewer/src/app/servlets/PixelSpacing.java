package app.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.bd.dao.InstanceDAO;

/**
 * Servlet implementation class PixelSpacing
 */
public class PixelSpacing extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final InstanceDAO iDAO = new InstanceDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PixelSpacing() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
		String imageID = request.getParameter("imageID");
		
		if(imageID == null || "".equals(imageID)){
			out.close(); 
			return;
		}
		
		float pixelSpacing = 0;
				
		try {
			pixelSpacing = iDAO.getPixelSpacing(imageID);
		
		} catch (Exception e) {
			e.printStackTrace();
			out.close(); 
			return; 
		}
		
		com.google.gson.Gson gson = new com.google.gson.Gson();
        String strJson = gson.toJson( pixelSpacing );
        
        try { out.print(strJson); } finally { out.close(); }
	}

}
