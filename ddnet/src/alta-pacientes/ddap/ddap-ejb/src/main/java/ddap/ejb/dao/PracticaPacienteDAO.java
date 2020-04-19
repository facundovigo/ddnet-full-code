package ddap.ejb.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import ddap.ejb.PracticaPacienteSearchFilter;
import ddap.ejb.PracticaPacienteSearchFilter.ppSearchDateType;
import ddap.ejb.entities.PracticaxPaciente;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class PracticaPacienteDAO extends AbstractDAO<Long, PracticaxPaciente> {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public PracticaPacienteDAO() {
		super(PracticaxPaciente.class);
	}


	@Override
	public void persist(PracticaxPaciente p) {
		super.persist(p);
	}
	
	@Override
	public void remove(PracticaxPaciente p) {
		super.remove(p);
	}
	
	public List<PracticaxPaciente> findPracticaPaciente( PracticaPacienteSearchFilter filter ){
		
		if( filter == null ) throw new IllegalArgumentException("'filter' can not be null");
		if( filter.getDateType() == ppSearchDateType.between &&
				(filter.getBetweenDateFrom() == null || filter.getBetweenDateTo() == null))
				throw new IllegalArgumentException("From and To dates are required when the 'between' study date type is specified.");
		
		final Calendar now = Calendar.getInstance();
		Calendar betweenDateFrom= Calendar.getInstance();
		Calendar betweenDateTo= Calendar.getInstance();
		betweenDateFrom.clear();
		betweenDateTo.clear();
		
		switch( filter.getDateType() ){
			case today:
				betweenDateFrom.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				betweenDateTo.setTime(now.getTime());
				break;
			case yesterday:
				betweenDateFrom.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				betweenDateFrom.add(Calendar.DATE, -1);
				betweenDateTo.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				betweenDateTo.add(Calendar.DATE, -1);
				break;
			case lastweek:
				betweenDateFrom.setTime(now.getTime());
				betweenDateFrom.add(Calendar.DATE, -7);
				betweenDateTo.setTime(now.getTime());
				break;
			case lastmonth:
				betweenDateFrom.setTime(now.getTime());
				betweenDateFrom.add(Calendar.DATE, -30);
				betweenDateTo.setTime(now.getTime());
				break;
			case any:
				betweenDateFrom= null;
				betweenDateTo= null;
				break;
			case between:
				betweenDateFrom.setTime(filter.getBetweenDateFrom());
				betweenDateFrom.add(Calendar.DATE, -1);
				betweenDateTo.setTime(filter.getBetweenDateTo());
				break;
		}
		
		String jpql = " SELECT pp FROM PracticaxPaciente pp INNER JOIN FETCH pp.patient pat INNER JOIN FETCH pp.practice pract "
					+ " WHERE pp.id > 0 ";
		
		
		if( filter.getDateType() != ppSearchDateType.any) jpql += " AND pp.regDate BETWEEN :betweenDateFrom AND :betweenDateTo ";
		
		
		if( filter.isSetPracticeState() ){
			if( filter.getPracticeState().equals("registered") ) jpql += " AND pp.inside = FALSE AND pp.outside = FALSE AND pp.cancelled = FALSE ";
			if( filter.getPracticeState().equals("started") ) jpql += " AND pp.inside = TRUE AND pp.outside = FALSE AND pp.cancelled = FALSE ";
			if( filter.getPracticeState().equals("finished") ) jpql += " AND pp.inside = TRUE AND pp.outside = TRUE AND pp.cancelled = FALSE ";
			if( filter.getPracticeState().equals("cancelled") ) jpql += " AND pp.inside = TRUE AND pp.outside = FALSE AND pp.cancelled = TRUE ";
		}
		if( filter.isSetAccessionNumber() )
			jpql += " AND LOWER(TRIM(BOTH FROM pp.code)) LIKE :accessionNumber ";
		if( filter.isSetPatientFilter() )
			jpql += " AND LOWER(TRIM(BOTH FROM pat.docNumber)) LIKE :patientFilter "
				 +  " OR LOWER(TRIM(BOTH FROM pat.lastName)) LIKE :patientFilter "
				 +  " OR LOWER(TRIM(BOTH FROM pat.firstName)) LIKE :patientFilter ";
		if( filter.isSetPatSex() ) jpql += " AND LOWER(TRIM(BOTH FROM pat.patSex)) LIKE :patSex ";
		
		jpql += " ORDER BY pp.regDate DESC ";
		TypedQuery<PracticaxPaciente> query = entityManager.createQuery(jpql, PracticaxPaciente.class);
		
		
		if( filter.getDateType() != ppSearchDateType.any)
			query.setParameter("betweenDateFrom", DATE_FORMAT.format(betweenDateFrom.getTime()))
				 .setParameter("betweenDateTo", DATE_FORMAT.format(betweenDateTo.getTime()));
		
		
		if( filter.isSetAccessionNumber())
			query.setParameter("accessionNumber", "%" + filter.getAccessionNumber().trim().toLowerCase() + "%");
		if( filter.isSetPatientFilter())
			query.setParameter("patientFilter", "%" + filter.getPatientFilter().trim().toLowerCase() + "%");
		if( filter.isSetPatSex() )
			query.setParameter("patSex", "%" + filter.getPatSex().trim().toLowerCase() + "%");
		
		List<PracticaxPaciente> resultList = query.getResultList();
		return resultList;
	}
	
	public Collection<PracticaxPaciente> getAll(){
		
		List<PracticaxPaciente> result = 
				entityManager.createQuery(" SELECT pp FROM PracticaxPaciente pp "
										+ "	ORDER BY pp.regTime DESC ", PracticaxPaciente.class)
				.getResultList();
		
		return result.isEmpty() ? null : result;
	}
	
	public PracticaxPaciente getByCode(String code){
		
		List<PracticaxPaciente> result =
				entityManager.createQuery(" SELECT pp FROM PracticaxPaciente pp "
										+ " WHERE pp.code = :codeParam ", PracticaxPaciente.class)
				.setParameter("codeParam", code)
				.getResultList();
		
		return result.isEmpty() ? null : result.get(0);
	}
}
