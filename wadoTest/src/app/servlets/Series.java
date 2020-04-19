package app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.dcm.dao.StudySeriesListDAO;
import app.dcm.dto.StudySeriesListDTO;

/**
 * Servlet implementation class Series
 */
public class Series extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static StudySeriesListDAO studySerieDAO = new StudySeriesListDAO();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Series() {
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
		
		List<StudySeriesListDTO> studySeriesList;
		try {
			studySeriesList = studySerieDAO.getStudySeriesList(studyID);
			
		} catch (Exception e) { e.printStackTrace(); return; }
		
		com.google.gson.Gson gson = new com.google.gson.Gson();
        String strJson = gson.toJson(
        	studySeriesList != null && !studySeriesList.isEmpty() ? studySeriesList.get(0) : null
        );
        
		try { out.print(strJson); } finally { out.close(); }
	}

}
