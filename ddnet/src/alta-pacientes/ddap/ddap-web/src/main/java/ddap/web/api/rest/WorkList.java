package ddap.web.api.rest;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
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
import javax.ws.rs.core.Response.Status;

import ddap.ejb.ConfigurationManager;
import ddap.ejb.LoggerManager;
import ddap.ejb.PacientesManager;
import ddap.ejb.PracticaPacienteManager;
import ddap.ejb.PracticasManager;
import ddap.ejb.UsuariosManager;
import ddap.ejb.WorklistManager;
import ddap.ejb.entities.Logger;
import ddap.ejb.entities.Paciente;
import ddap.ejb.entities.Practica;
import ddap.ejb.entities.PracticaxPaciente;
import ddap.ejb.entities.Usuario;
import ddap.ejb.entities.Worklist;
import ddap.web.security.SecurityHelper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Stateless
@Path("/worklist")
public class WorkList {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");
	private static final SimpleDateFormat DATE_LOG_FORMAT = new SimpleDateFormat("yyyyMMdd HHmmss");
	private static final SimpleDateFormat DATE_FILE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final SimpleDateFormat DATE_DOWNLOAD_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat DATE_PDF_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat DATE_UID_FORMAT= new SimpleDateFormat("HH.mm.ss.SSS");
	
	@EJB private SecurityHelper securityHelper;
	@EJB private UsuariosManager userManager;
	@EJB private ConfigurationManager confManager;
	@EJB private WorklistManager wManager;
	@EJB private PacientesManager patManager;
	@EJB private PracticasManager practManager;
	@EJB private PracticaPacienteManager ppManager;
	@EJB private LoggerManager logManager;
	
	@Path("/new")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response sendToWorkList
	(	@Context HttpServletRequest request,
		@Context HttpServletResponse response,
		@QueryParam("study-acc-number")String accessionNumber,
		@QueryParam("patID") String patID,
		@QueryParam("firstName") String firstName,
		@QueryParam("lastName") String lastName,
		@QueryParam("patSex") String patSex,
		@QueryParam("birthDate") String birthDate,
		@QueryParam("modality") String mod,
		@QueryParam("equipment") String equipment,
		@QueryParam("practice") String practice,
		@QueryParam("ref-physician") String medico,
		@QueryParam("value") float value	)throws Exception{
		
		Usuario u= securityHelper.getUser(request.getSession());
		Worklist w= new Worklist();
		Date now= new Date();
		
		if(accessionNumber==null||"".equals(accessionNumber)) return Response.status(Status.NO_CONTENT).build();
		for(Worklist wl: wManager.getAll()) if(wl.getAccNumb().equals(accessionNumber)) return Response.status(Status.NOT_ACCEPTABLE).build();
		
		w.setAccNumb(accessionNumber);
		w.setStudyID(accessionNumber+"."+DATE_UID_FORMAT.format(now));
		if(patID!=null && !patID.isEmpty()) w.setPatID(patID);
		if(firstName!=null && !firstName.isEmpty()) w.setFirstName(firstName);
		if(lastName!=null && !lastName.isEmpty()) w.setLastName(lastName);
		if(patSex!=null && !patSex.isEmpty()) w.setPatSex(patSex);
		if(birthDate!=null && !birthDate.isEmpty()) w.setBirthDate(birthDate);
		if(mod!=null && !mod.isEmpty()) w.setMod(mod);
		if(equipment!=null && !equipment.isEmpty()) w.setPractRoom(equipment);
		if(practice!=null && !practice.isEmpty()) w.setPractDesc(practice.split("_")[1]);
		w.setRefPhysician(medico);
		w.setSchedAET("ddap");
		
		String stdDate= DATE_FORMAT.format(now);
		String stdTime= TIME_FORMAT.format(now);
		String pdfDate= DATE_PDF_FORMAT.format(now);
		String rutaArchivo= getPDFUploadDirectory(now).getAbsolutePath() + "/Practica"+DATE_FILE_FORMAT.format(now)+".pdf";
		
		PracticaxPaciente pp= new PracticaxPaciente();
		Paciente pat= patManager.getByPatID(patID);
		Practica pract= practManager.findById(Long.parseLong(practice.split("_")[0]));
		
	    pp.setPatient(pat);
		pp.setPractice(pract);
		pp.setRegDate(stdDate);
		pp.setRegTime(stdTime);
		pp.setPdfPath(rutaArchivo);
		pp.setCode(accessionNumber);
		ppManager.persist(pp);

		w.setStdDate(stdDate);
		w.setStdTime(stdTime);
		wManager.persist(w);
		recordInLog(u.getLogin(),"ENVÍO A LA WORKLIST",patID);
		
		File archivoPDF= new File(rutaArchivo);
		if(archivoPDF.exists()) archivoPDF.delete();
		archivoPDF.createNewFile();
		
		FileOutputStream archivo= new FileOutputStream(archivoPDF);
	    Document documento= new Document();
	    PdfWriter.getInstance(documento, archivo);
	    //Paragraph paragraph;
	    //Chunk chunk;
	    
	    documento.open();
	    	
	    	final String imgFolder = confManager.getConfigurationItem("report.logos.source-path").getValue();
	    	String rutaImagen = imgFolder + "/institucional.jpg";
		    Image foto = Image.getInstance(rutaImagen);
		    foto.scaleAbsolute(500f,70f);
		    foto.setAlignment(Chunk.ALIGN_MIDDLE);
		    documento.add(foto);
		    
		    PdfPTable tabla= new PdfPTable(1);
		    int widths[]= {800};
		    tabla.setWidths(widths);
		    tabla.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		    tabla.setSpacingBefore(30);
		    for(int i=0; i<8; i++){
		    	switch(i) {
		    	case 0:	Chunk chunkParCell0= new Chunk("Práctica  "+accessionNumber+"  -  "+pdfDate, FontFactory.getFont("arial", 14, Font.BOLD, BaseColor.WHITE));
			    	    Paragraph paragraphCell0= new Paragraph();
					    paragraphCell0.add(chunkParCell0);
			    		PdfPCell cell0= new PdfPCell(paragraphCell0);
			    		cell0.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			    		cell0.setBackgroundColor(new BaseColor(new Color(100,100,100)));
			    		cell0.setBorderColor(new BaseColor(new Color(100,100,100)));
			    		cell0.setFixedHeight(23);
				    	tabla.addCell(cell0);
				break;
		    	case 1:	PdfPCell cell1= new PdfPCell(new Paragraph());
		    			cell1.setBackgroundColor(new BaseColor(new Color(220,220,220)));
		    			cell1.setBorderColor(new BaseColor(new Color(100,100,100)));
		    			cell1.setFixedHeight(12);
		    			tabla.addCell(cell1);
		    	break;
		    	case 2: PdfPTable tablaCol2= new PdfPTable(2);
					    int widthsCol2[]= {150,450};
					    tablaCol2.setWidths(widthsCol2);
					    for(int c=0; c<4; c++){
					    	String texto="";
					    	switch(c){
					    	case 0:texto="PatientID";break;
					    	case 1:texto="PACIENTE";break;
					    	case 2:texto="\n"+patID;break;
					    	case 3:texto="\n"+lastName+" "+firstName;break;
					    	}
					    	Chunk chunkParCell2= new Chunk(texto, FontFactory.getFont("arial", 10, c<2? Font.BOLD:Font.NORMAL, BaseColor.BLACK));
				    	    Paragraph paragraphCell2= new Paragraph();
						    paragraphCell2.add(chunkParCell2);
				    		PdfPCell celda= new PdfPCell(paragraphCell2);
				    		celda.setHorizontalAlignment(Chunk.ALIGN_CENTER);
				    		celda.setBorderColor(new BaseColor(new Color(100,100,100)));
				    		if(c<2) celda.setBackgroundColor(new BaseColor(new Color(190,190,190)));
				    		tablaCol2.addCell(celda);
					    }
					    PdfPCell cell2= new PdfPCell(tablaCol2);
			    		cell2.setBackgroundColor(new BaseColor(new Color(220,220,220)));
			    		cell2.setBorderColor(new BaseColor(new Color(100,100,100)));
			    		cell2.setFixedHeight(50);
			    		tabla.addCell(cell2);
			    break;
		    	case 3: PdfPTable tablaCol3= new PdfPTable(2);
					    int widthsCol3[]= {300,300};
					    tablaCol3.setWidths(widthsCol3);
					    for(int c=0; c<4; c++){
					    	String texto="";
					    	switch(c){
					    	case 0:texto="Fecha Nac.";break;
					    	case 1:texto="Sexo";break;
					    	case 2:texto="\n"+birthDate;break;
					    	case 3:texto="\n"+patSex;break;
					    	}
					    	Chunk chunkParCell3= new Chunk(texto, FontFactory.getFont("arial", 10, c<2? Font.BOLD:Font.NORMAL, BaseColor.BLACK));
				    	    Paragraph paragraphCell3= new Paragraph();
						    paragraphCell3.add(chunkParCell3);
				    		PdfPCell celda= new PdfPCell(paragraphCell3);
				    		celda.setHorizontalAlignment(Chunk.ALIGN_CENTER);
				    		celda.setBorderColor(new BaseColor(new Color(100,100,100)));
				    		if(c<2) celda.setBackgroundColor(new BaseColor(new Color(190,190,190)));
				    		tablaCol3.addCell(celda);
					    }
					    PdfPCell cell3= new PdfPCell(tablaCol3);
			    		cell3.setBackgroundColor(new BaseColor(new Color(220,220,220)));
			    		cell3.setBorderColor(new BaseColor(new Color(100,100,100)));
			    		cell3.setFixedHeight(45);
			    		tabla.addCell(cell3);
				break;
		    	case 4: PdfPTable tablaCol4= new PdfPTable(1);
					    int widthsCol4[]= {600};
					    tablaCol4.setWidths(widthsCol4);
					    for(int c=0; c<2; c++){
					    	String texto="";
					    	switch(c){
					    	case 0:texto="PRÁCTICA";break;
					    	case 1:texto="\n"+practice.split("_")[1];break;
					    	}
					    	Chunk chunkParCell4= new Chunk(texto, FontFactory.getFont("arial", 10, c==0? Font.BOLD:Font.NORMAL, BaseColor.BLACK));
				    	    Paragraph paragraphCell4= new Paragraph();
						    paragraphCell4.add(chunkParCell4);
				    		PdfPCell celda= new PdfPCell(paragraphCell4);
				    		celda.setHorizontalAlignment(Chunk.ALIGN_CENTER);
				    		celda.setBorderColor(new BaseColor(new Color(100,100,100)));
				    		if(c==0) celda.setBackgroundColor(new BaseColor(new Color(190,190,190)));
				    		tablaCol4.addCell(celda);
					    }
					    PdfPCell cell4= new PdfPCell(tablaCol4);
			    		cell4.setBackgroundColor(new BaseColor(new Color(220,220,220)));
			    		cell4.setBorderColor(new BaseColor(new Color(100,100,100)));
			    		cell4.setFixedHeight(50);
			    		tabla.addCell(cell4);
				break;
		    	case 5: PdfPTable tablaCol5= new PdfPTable(2);
					    int widthsCol5[]= {300,300};
					    tablaCol5.setWidths(widthsCol5);
					    for(int c=0; c<4; c++){
					    	String texto="";
					    	switch(c){
					    	case 0:texto="Modalidad";break;
					    	case 1:texto="Equipo";break;
					    	case 2:texto="\n"+mod;break;
					    	case 3:texto="\n"+equipment;break;
					    	}
					    	Chunk chunkParCell5= new Chunk(texto, FontFactory.getFont("arial", 10, c<2? Font.BOLD:Font.NORMAL, BaseColor.BLACK));
				    	    Paragraph paragraphCell5= new Paragraph();
						    paragraphCell5.add(chunkParCell5);
				    		PdfPCell celda= new PdfPCell(paragraphCell5);
				    		celda.setHorizontalAlignment(Chunk.ALIGN_CENTER);
				    		celda.setBorderColor(new BaseColor(new Color(100,100,100)));
				    		if(c<2) celda.setBackgroundColor(new BaseColor(new Color(190,190,190)));
				    		tablaCol5.addCell(celda);
					    }
					    PdfPCell cell5= new PdfPCell(tablaCol5);
			    		cell5.setBackgroundColor(new BaseColor(new Color(220,220,220)));
			    		cell5.setBorderColor(new BaseColor(new Color(100,100,100)));
			    		cell5.setFixedHeight(45);
			    		tabla.addCell(cell5);
				break;
		    	case 6:	PdfPCell cell6= new PdfPCell(new Paragraph());
		    			cell6.setBackgroundColor(new BaseColor(new Color(220,220,220)));
		    			cell6.setBorderColor(new BaseColor(new Color(100,100,100)));
		    			cell6.setFixedHeight(100);
		    			tabla.addCell(cell6);
		    	break;
		    	case 7:	Chunk chunkParCell7= new Chunk(String.format("VALOR : $ %.2f",value), FontFactory.getFont("arial", 11, Font.BOLD, BaseColor.BLACK));
			    	    Paragraph paragraphCell7= new Paragraph();
					    paragraphCell7.add(chunkParCell7);
		    			PdfPCell cell7= new PdfPCell(paragraphCell7);
		    			cell7.setBackgroundColor(new BaseColor(new Color(190,190,190)));
		    			cell7.setBorderColor(new BaseColor(new Color(100,100,100)));
		    			cell7.setFixedHeight(18);
		    			cell7.setHorizontalAlignment(Chunk.ALIGN_RIGHT);
		    			tabla.addCell(cell7);
		    	break;
		    	}
		    } documento.add(tabla);

	    documento.close();
	    
	    response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=\""+lastName+", "+firstName+" ["+DATE_DOWNLOAD_FORMAT.format(new Date())+"].pdf\"");
		response.setHeader("Content-Length", String.valueOf(1));
		response.setHeader("Set-Cookie","fileDownload=true; path=/");
		return Response.ok(archivoPDF).build();
	}
	
	
	
	private File getPDFUploadDirectory(Date now) {
		File rootDirectory = new File(confManager.getConfigurationItem("worklist-pdf-path").getValue());
		File PDFFileDateFolder = new File(rootDirectory, DATE_FORMAT.format(now));
		if (!PDFFileDateFolder.exists()) PDFFileDateFolder.mkdirs();
		return PDFFileDateFolder;
	}
	
	
	public void recordInLog(String login, String accion, String detalle){
		
		Logger log = new Logger();
		
			log.setDate(DATE_LOG_FORMAT.format(new Date()));
			log.setUser(login);
			log.setAction(accion);
			log.setDetails(detalle);
			
		logManager.persist(log);
	}
}

























