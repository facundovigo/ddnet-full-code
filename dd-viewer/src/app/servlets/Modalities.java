package app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.bd.dao.ModalityDAO;

/**
 * Servlet implementation class Modalities
 */
public class Modalities extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final ModalityDAO modDao = new ModalityDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Modalities() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        List<String> modalities = new ArrayList<String>();
        
        try {
			modalities = modDao.getModalities();
			
		} catch (Exception e) { 
			out.print(e.toString()); 
			out.close(); 
			return; 
		}
        
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String strJson = gson.toJson(
        	modalities != null && !modalities.isEmpty() ? modalities : null
        );

		try { out.print(strJson); } finally { out.close(); }
	}

}
