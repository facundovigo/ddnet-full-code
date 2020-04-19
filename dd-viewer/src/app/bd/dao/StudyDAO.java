package app.bd.dao;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import app.bd.conn.PgSQLConn;
import app.bd.dto.StudyDTO;
import app.bd.dto.StudySeriesListDTO;
import app.filters.StudySearchFilter;
import app.filters.StudySearchFilter.StudySearchDateType;

public class StudyDAO {
	
	private final PgSQLConn conn = new PgSQLConn();
	private static final int R1 = ResultSet.TYPE_SCROLL_SENSITIVE,
			 				 R2 = ResultSet.CONCUR_READ_ONLY;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_QUERY_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final SimpleDateFormat DATE_SINGLE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SeriesDAO seriesDAO = new SeriesDAO();
	
	public Collection<StudyDTO> findStudies(StudySearchFilter filter) throws Exception {
		
		List<StudyDTO> result = new ArrayList<StudyDTO>();
		
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		if (filter == null)
			throw new IllegalArgumentException("'filter' can not be null");
		
		if (filter.getDateType() == StudySearchDateType.between &&
				(filter.getDateBetweenFrom() == null || filter.getDateBetweenTo() == null))
				throw new IllegalArgumentException("From and To dates are required when the 'between' study date type is specified.");
		
		final Calendar now = Calendar.getInstance();
		Calendar studyDateFrom = Calendar.getInstance();
		Calendar studyDateTo = Calendar.getInstance();
		studyDateFrom.clear();
		studyDateTo.clear();
		
		switch(filter.getDateType()) {
			case any:
				studyDateFrom = null;
				studyDateTo = null;
				break;
			case today:
				studyDateFrom.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				studyDateTo.setTime(now.getTime());
				break;
			case yesterday:
				studyDateFrom.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				studyDateFrom.add(Calendar.DATE, -1);
				studyDateTo.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				break;
			case lastweek:
				studyDateFrom.setTime(now.getTime());
				studyDateFrom.add(Calendar.DATE, -7);
				studyDateTo.setTime(now.getTime());
				break;
			case lastmonth:
				studyDateFrom.setTime(now.getTime());
				studyDateFrom.add(Calendar.DATE, -30);
				studyDateTo.setTime(now.getTime());
				break;
			case between:
				studyDateFrom.setTime(filter.getDateBetweenFrom());
				studyDateTo.setTime(filter.getDateBetweenTo());
				break;
		}
		
		String jpql = 
				" SELECT s.study_iuid, p.pat_name, p.pat_id, s.study_datetime, "
				+ " s.mods_in_study, s.study_desc, s.num_series, s.num_instances "
				+ " FROM study s "
				+ " JOIN patient p ON p.pk = s.patient_fk "
				+ " WHERE s.num_instances > 0 ";
		
		if (filter.getDateType() != StudySearchDateType.any)
			jpql += " AND s.study_datetime BETWEEN :studyDateFrom AND :studyDateTo ";
		if (filter.isSetModality())
			jpql += " AND LOWER(TRIM(BOTH FROM s.mods_in_study)) LIKE :modality ";
		if (filter.isSetStudyFilter())
			jpql += " AND LOWER(TRIM(BOTH FROM s.study_iuid)) LIKE :studyFilter "
				  + " OR LOWER(TRIM(BOTH FROM s.accession_no)) LIKE :studyFilter ";
		if (filter.isSetPatientFilter())
			jpql += " AND LOWER(TRIM(BOTH FROM p.pat_name)) LIKE :patientFilter "
				  + " OR LOWER(TRIM(BOTH FROM p.pat_id)) LIKE :patientFilter "; 
		
		jpql += " ORDER BY s.study_datetime DESC ";
		
		if (filter.getDateType() != StudySearchDateType.any){
			jpql = jpql.replace(":studyDateFrom", String.format("'%%%s%%'", DATE_QUERY_FORMAT.format(studyDateFrom.getTime())));
			jpql = jpql.replace(":studyDateTo", String.format("'%%%s%%'", DATE_QUERY_FORMAT.format(studyDateTo.getTime())));
		}
		if (filter.isSetModality())
			jpql = jpql.replace(":modality", "'%" + filter.getModality().trim().toLowerCase().replace("_","\\\\") + "%'");
		if (filter.isSetStudyFilter())
			jpql = jpql.replace(":studyFilter", "'%" + filter.getStudyFilter().trim().toLowerCase() + "%'");
		if (filter.isSetPatientFilter())
			jpql = jpql.replace(":patientFilter", "'%" + filter.getPatientFilter().trim().toLowerCase() + "%'");
		
		
		ResultSet rs = st.executeQuery(jpql);
		
		while(rs.next()){
			result.add(
				new StudyDTO(
					rs.getString(1), 
					rs.getString(2).replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " "), 
					rs.getString(3), 
					DATE_FORMAT.format(rs.getDate(4)), 
					rs.getString(5), 
					rs.getString(6), 
					rs.getInt(7),
					rs.getInt(8)
			));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return result;
	}
	
	public List<StudySeriesListDTO> getStudySeriesList(String studyID) throws Exception {
		List<StudySeriesListDTO> studySeriesList = new ArrayList<StudySeriesListDTO>();
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT p.pat_name, p.pat_id, s.study_datetime, "
				+ " s.mods_in_study, s.study_desc, s.num_instances, s.study_iuid "
				+ " FROM study s "
				+ " JOIN patient p ON p.pk = s.patient_fk "
				+ " WHERE s.study_iuid = :studyID "
				.replace(":studyID", String.format("'%s'", studyID));
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			studySeriesList.add(
				new StudySeriesListDTO(
						rs.getString(1).replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " "), 
						rs.getString(2), 
						DATE_FORMAT.format(rs.getDate(3)), 
						rs.getString(4),
						rs.getString(5),
						rs.getInt(6),
						rs.getString(7),
						seriesDAO.getSeriesList(rs.getString(7))
			));
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return studySeriesList;
	}
	
	public StudyDTO getStudy(String studyID) throws Exception {
		StudyDTO dto = null;
		Connection conn = this.conn.getConn();
		Statement st = conn.createStatement(R1,R2);
		
		String query = 
				" SELECT s.study_iuid, p.pat_name, p.pat_id, s.study_datetime, "
				+ " s.mods_in_study, s.study_desc, s.num_series, s.num_instances "
				+ " FROM study s "
				+ " JOIN patient p ON p.pk = s.patient_fk "
				+ " WHERE s.study_iuid = :studyID "
				.replace(":studyID", String.format("'%s'", studyID));
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()){
			dto = new StudyDTO(
					rs.getString(1), 
					rs.getString(2).replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " "), 
					rs.getString(3), 
					DATE_SINGLE_FORMAT.format(rs.getDate(4)), 
					rs.getString(5), 
					rs.getString(6), 
					rs.getInt(7),
					rs.getInt(8)
			);
		}
		
		rs.close();
		st.close();
		conn.close();
		
		return dto;
	}
}
