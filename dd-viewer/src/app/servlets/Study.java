package app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.bd.dao.StudyDAO;
import app.bd.dto.StudyDTO;
import app.filters.StudySearchFilter;
import app.filters.StudySearchFilter.StudySearchDateType;

/**
 * Servlet implementation class Study
 */
public class Study extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final StudyDAO studyDAO = new StudyDAO();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
       
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
        Collection<StudyDTO> result = new ArrayList<StudyDTO>();
        
        String dateType  = request.getParameter("date-type");
        String dateBetweenFrom = request.getParameter("date-between-from");
        String dateBetweenTo = request.getParameter("date-between-to");
        String modality = request.getParameter("modality");
        String studyFilter = request.getParameter("study");
        String patientFilter = request.getParameter("patient");
        
        try {
        	Date studyDateFrom = !"".equals(dateBetweenFrom) ? DATE_FORMAT.parse(dateBetweenFrom) : null;
        	Date studyDateTo = !"".equals(dateBetweenTo) ? DATE_FORMAT.parse(dateBetweenTo) : null;
        	
			result = studyDAO.findStudies(
				new StudySearchFilter(
					StudySearchDateType.getByCode(Integer.parseInt(dateType)), 
					studyDateFrom, 
					studyDateTo, 
					modality, 
					studyFilter, 
					patientFilter
			));
			
		} catch (Exception e) { 
			e.printStackTrace();
			out.print(e.toString()); 
			out.close(); 
			return; 
		}
        
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String strJson = gson.toJson(
        	result != null && !result.isEmpty() ? result : null
        );

		try { out.print(strJson); } finally { out.close(); }
	}

}
