package ddnet.web.api.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import ddnet.ejb.*;
import ddnet.ejb.StudySearchFilter.StudyReportStatus;
import ddnet.ejb.StudySearchFilter.StudySearchDateType;
import ddnet.ejb.entities.LegacyPatient;
import ddnet.ejb.entities.LegacyStudy;
import ddnet.ejb.entities.Study;
import ddnet.ejb.entities.User;
import ddnet.web.security.SecurityHelper;

@Stateless
@Path("")
public class Statistics {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_EXCEL_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	@EJB
	private StudyManager studyManager;
	@EJB
	private SecurityHelper securityHelper;
	@EJB
	private UserManager usermanager;
	@EJB
	private ConfigurationManager configurationManager;
	
	@Path("estadisticas/excel")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDownloadFile(@Context HttpServletRequest request,
									@Context HttpServletResponse response,
									@QueryParam("study-date-type") int studyDateType,
									@QueryParam("study-date-between-from") String studyDateFrom,
									@QueryParam("study-date-between-to") String studyDateTo,
									@QueryParam("study-modality") String modality,
									@QueryParam("patient-data") String patientData,
									@QueryParam("study-accessionnumber") String accessionNumber,
									@QueryParam("study-user") String studyUser,
									@QueryParam("urgente") String urgente,
									@QueryParam("prioridad") String prioridad,
									@QueryParam("adjunto") String adjunto,
									@QueryParam("firmado") String firmado,
									@QueryParam("preinformado") String preinformado,
									@QueryParam("no-leido") String noLeido,
									@QueryParam("incidencia") int incidencia,
									@QueryParam("another-filter") int anotherFilter,
									@QueryParam("patient-data-dob") String patientDOB,
									@QueryParam("study-diagnostic-center") long institutionID,
									@QueryParam("study-report-status") int reportStatus ) throws IOException, ParseException {		
		
		User user = securityHelper.getUser(request.getSession());
		
		StudyReportStatus reportStatusFilter = StudyReportStatus.getByCode(reportStatus);
		if (reportStatusFilter == null) reportStatusFilter = StudyReportStatus.any; 
							
		StudySearchFilter filter = new StudySearchFilter(
				StudySearchDateType.getByCode(studyDateType), 
				getDateFromString(studyDateFrom), 
				getDateFromString(studyDateTo), 
				accessionNumber, patientData,
				getDateFromString(patientDOB), modality, institutionID, reportStatusFilter,
				studyUser, urgente!=null, prioridad!=null, adjunto!=null, 
				firmado!=null, preinformado!=null, noLeido!=null, anotherFilter, false);
		
		final String xlsFolder = configurationManager.getConfigurationItem("statistics-xls-path").getValue();
		
		/*La ruta donde se creará el archivo*/
        String rutaArchivo = xlsFolder + "/Estadisticas.xls";
        
        /*Se crea el objeto de tipo File con la ruta del archivo*/
        File archivoXLS = new File(rutaArchivo);
        
        /*Si el archivo existe se elimina*/
        if(archivoXLS.exists()) archivoXLS.delete();
        
        /*Se crea el archivo*/
        archivoXLS.createNewFile();
		
        /*Se crea el libro de excel usando el objeto de tipo Workbook*/
        Workbook libro = new HSSFWorkbook();
        
        /*Se inicializa el flujo de datos con el archivo xls*/
		FileOutputStream archivo = new FileOutputStream(archivoXLS);
		
		Font headerFont = libro.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD); 
		
		CellStyle headerStyle = libro.createCellStyle();
		
		headerStyle.setFont(headerFont);
		
        /*Utilizamos la clase Sheet para crear una nueva hoja de trabajo dentro del libro que creamos anteriormente*/
        Sheet hoja = libro.createSheet("Estadísticas"+DATE_FOLDER_FORMAT.format(new Date()));
        

		Collection<Study> studies = studyManager.findStudies(user, filter);
		
		if(studies != null && !studies.isEmpty()){
		
			Object std[] = studies.toArray();
			int index = studies.size();
        
	        /*Hacemos un ciclo para inicializar los valores de 10 filas de celdas*/
	        for(int f=0; f<index+1; f++){
	            
	        	/*La clase Row nos permitirá crear las filas*/
	            Row fila = hoja.createRow(f);
	            
	            /*Cada fila tendrá 5 celdas de datos*/
	            for(int c=0;c<12;c++){
	                
	            	/*Creamos la celda a partir de la fila actual*/
	                Cell celda = fila.createCell(c);
	                
	                /*Si la fila es la número 0, estableceremos los encabezados*/
	                if(f==0){
	                	celda.setCellStyle(headerStyle);
	                	switch(c){
	                		case 0: celda.setCellValue("N°");
	                				break;
	                		case 1: celda.setCellValue("Accession Number");
	        						break;
	                		case 2: celda.setCellValue("Descripción");
	        						break;
	                		case 3: celda.setCellValue("ID Paciente");
	        						break;
	                		case 4: celda.setCellValue("Nombre Paciente");
	        						break;
	                		case 5: celda.setCellValue("Edad");
	        						break;
	                		case 6: celda.setCellValue("Modalidad");
	        						break;
	                		case 7: celda.setCellValue("Fecha");
	        						break;
	                		case 8: celda.setCellValue("Institución");
	        						break;
	                		case 9: celda.setCellValue("Informado");
	        						break;
	                		case 10: celda.setCellValue("Médico Informe");
	        						break;
	                		case 11: celda.setCellValue("Valor Práctica");
    						break;
	                	}
	                
	                } else{
	                	
	                    int i = f - 1 ;
	                    Study s = (Study)std[i];
	                    if(s == null) continue;
	                    LegacyStudy ls = s.getLegacyStudy();
	                    LegacyPatient lp = ls.getLegacyPatient();
	                    
	                	/*Si no es la primera fila establecemos un valor*/
	                	switch(c){
		            		case 0: celda.setCellValue(s.getId());
		            				break;
		            		case 1: celda.setCellValue(ls.getAccessionNumber() != null ? ls.getAccessionNumber() : "---");
		    						break;
		            		case 2: celda.setCellValue(ls.getDescription() != null ? ls.getDescription() : "---");
		    						break;
		            		case 3: celda.setCellValue(lp.getPatientID() != null ? lp.getPatientID() : "---");
		    						break;
		            		case 4: celda.setCellValue(lp.getName() != null ? lp.getName().replace("^^^^", "")
		            									.replace("^^^", "").replace("^^", "").replace("^", " ") : "---");
		    						break;
		            		case 5: celda.setCellValue(lp.getCalculatedAge() != null ? lp.getCalculatedAge() : "---");
		    						break;
		            		case 6: celda.setCellValue(ls.getModalities() != null ? ls.getModalities() : "---");
		    						break;
		            		case 7: celda.setCellValue(DATE_EXCEL_FORMAT.format(ls.getDate()) != null ? DATE_EXCEL_FORMAT.format(ls.getDate()) : "---");
		    						break;
		            		case 8: celda.setCellValue(s.getInstitution().getName() != null ? s.getInstitution().getName() : "---");
		    						break;
		            		case 9: celda.setCellValue(s.isReported() ? "SÍ" : "NO");
		    						break;
		            		case 10: celda.setCellValue(s.isReported() && s.getInforme() != null ? s.getInforme().getUserLogin() : "---");
		    						break;
		            		case 11: celda.setCellValue(s.getRemuneracion()!= null ? "$"+s.getRemuneracion():"$0");
    								break;
	                	}
	                }
	            }
	        }
        
		} else{
			Row fila = hoja.createRow(0);
			Cell celda = fila.createCell(0);
			
			celda.setCellValue("No se encontraron estudios con esas especificaciones.");
		}
        
        for(int c=0;c<12;c++) hoja.autoSizeColumn(c);
        
        /*Escribimos en el libro*/
        libro.write(archivo);
        
        /*Cerramos el flujo de datos*/
        archivo.close();
        
        response.setContentType("application/xls");
		response.addHeader("Content-Disposition", "attachment; filename=\"Estadisticas"+DATE_FOLDER_FORMAT.format(new Date())+".xls\"");
		response.setHeader("Content-Length", String.valueOf(1));
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		
		return Response.ok(archivoXLS).build();
	}
	
	private Date getDateFromString(String input) throws ParseException {
		return (input != null && !input.trim().equals("")) ? DATE_FORMAT.parse(input) : null;
	}
	
}