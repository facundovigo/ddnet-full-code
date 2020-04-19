package app_funciones;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import pgsql_bd.DAO.StudyInfoDAO;
import pgsql_bd.DTO.StudyInfoDTO;


public class Funciones {
	
	StudyInfoDAO sdao = new StudyInfoDAO();
	
	public List<String> getStudyInfo(String dato) throws SQLException{
		
		List<String> data = new ArrayList<String>();
		ArrayList<StudyInfoDTO> info = sdao.getDataByAccNumb(dato);
		
		for(StudyInfoDTO i : info){
			
			data.add(i.getPatName());
			data.add(i.getStudyDate().substring(0,10));
			data.add(i.getStudyDate().substring(11,19));
			data.add(i.getStudyMod());
			data.add(i.getId().toString());
			data.add(i.isReported() ? "INFORMADO" : 
						i.getReportBody() == null || i.getReportBody().trim().isEmpty() ? 
						"A CONFIRMAR" : "A INFORMAR");
		}
		
		return data;
	}
	
}
