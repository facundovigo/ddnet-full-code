package app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.dcm.dao.StudyListDAO;
import app.dcm.dto.StudyListDTO;

/**
 * Servlet implementation class Study
 */
public class Study extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static StudyListDAO studyDAO = new StudyListDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Study() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
		String studyID = request.getParameter("studyID");
		
		if(studyID == null || studyID.equals("")) {	
			try { out.print("No se ha especificado Study Instance UID"); } finally { out.close(); }
		
		} else {
			List<StudyListDTO> studyList = new ArrayList<StudyListDTO>();
			try {
				studyList = studyDAO.getStudyList(studyID);
				
			} catch (Exception e) { e.printStackTrace(); return; }
			
			com.google.gson.Gson gson = new com.google.gson.Gson();
	        String strJson = gson.toJson(
	        	studyList != null && !studyList.isEmpty() ? studyList : null
	        );
   
			try { out.print(strJson); } finally { out.close(); }
		}
	}

}
