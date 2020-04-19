package app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.bd.dao.StudyDAO;
import app.bd.dto.StudySeriesListDTO;

/**
 * Servlet implementation class StudySeriesList
 */
public class StudySeriesList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final StudyDAO sDAO = new StudyDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudySeriesList() {
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
		
		if(studyID == null || "".equals(studyID)){
			out.print("No se especificó Study Instance UID"); 
			out.close(); 
			return; 
		}
		
		List<StudySeriesListDTO> seriesList = new ArrayList<StudySeriesListDTO>();
		
		try{
			seriesList = sDAO.getStudySeriesList(studyID);
			
		} catch(Exception e) {
			e.printStackTrace();
			out.print(e.toString()); 
			out.close(); 
			return; 
		}
		
		com.google.gson.Gson gson = new com.google.gson.Gson();
        String strJson = gson.toJson(
        	seriesList != null && !seriesList.isEmpty() ? seriesList.get(0) : null
        );
        
        try { out.print(strJson); } finally { out.close(); }
	}

}
