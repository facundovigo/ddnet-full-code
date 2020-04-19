package app.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.bd.dao.StudyDAO;
import app.bd.dto.StudyDTO;

/**
 * Servlet implementation class SingleStudy
 */
public class SingleStudy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final StudyDAO studyDAO = new StudyDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleStudy() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StudyDTO study;
        String studyID = "";
        
        try{
        	studyID = request.getParameter("studyID");
        	study = studyDAO.getStudy(studyID);
			
		} catch (Exception e) { 
			e.printStackTrace();
			out.print(e.toString()); 
			out.close(); 
			return; 
		}
        
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String strJson = gson.toJson(
        	study != null  ? study : null
        );

		try { out.print(strJson); } finally { out.close(); }
	}
	
}
