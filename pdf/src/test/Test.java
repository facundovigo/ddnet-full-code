package test;

import java.awt.Color;
import java.io.FileOutputStream;

import pdf.ReportExamples.ReportTemplate;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
 
public class Test {
 
	//Nombre del fichero <strong>PDF</strong> Resultante de la ejecucion
	public static final String RESULT = "/home/server/Escritorio/nahu.pdf";
	private final static ReportTemplate templ= new ReportTemplate();
	
	public static class HeaderFooter extends PdfPageEventHelper {
		
		public  HeaderFooter() {}

        public void onEndPage(PdfWriter writer, Document document) {

        	try{
	        	PdfPTable table; Image foto; PdfPCell cell;

	        // Encabezado de Página
	        	table= new PdfPTable(1);
	        	table.setTotalWidth(590);
	        	foto= Image.getInstance("resources/logo.jpg");
	        	foto.scaleAbsolute(590f, 70f);
	        	cell= new PdfPCell(foto);
	        	cell.setBorder(Rectangle.NO_BORDER);
	        	table.addCell(cell);

	        	table.writeSelectedRows(0, -1, document.left(), 830, writer.getDirectContent());
	        // Encabezado de Página

	        // Pie de Página
	        	Chunk c= new Chunk(String.format("página %d",writer.getPageNumber()), new Font(Font.FontFamily.COURIER,8,Font.ITALIC,BaseColor.BLACK));            
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(c), document.right(50), 35, 0);

				table= new PdfPTable(1);
				table.setTotalWidth(575);
				foto= Image.getInstance("resources/pie.jpg");
				foto.scaleAbsolute(575f, 20f);
				cell= new PdfPCell(foto);
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);

				table.writeSelectedRows(0, -1, document.left(), 25, writer.getDirectContent());
			// Encabezado de Página

        	} catch(Exception e){System.out.println(e.getMessage());}
        }
/*
		Image foto = Image.getInstance("resources/logo.jpg");
		ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("nahuellopez1994@gmail.com"), rect.getRight(), rect.getTop(), 0);
*/
	}
	
	public static void main (String[] args) {
		
		
		try {
			String informeMR= templ.getFromArchivo();

			Test.HeaderFooter event= new Test.HeaderFooter();
			
			Document document= new Document(PageSize.A4, 10, 10, 85, 50);
			document.addAuthor("NLopez");
			document.addTitle("Plantilla Informe");

			PdfWriter writer= PdfWriter.getInstance(document, new FileOutputStream(RESULT));
			Rectangle rct= new Rectangle(35, 35, 559, 788);
			
			writer.setInitialLeading(18);
			writer.setBoxSize("art", rct);
			writer.setPageEvent(event);

			document.open();
			
			
			BaseColor backgroundColor= new BaseColor(new Color(230,230,230));
		    BaseColor theadColor= new BaseColor(new Color(210,210,210));
		    PdfPCell cell;

			PdfPTable table= new PdfPTable(4);
			table.setWidthPercentage(95);
			table.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			table.setWidths(new float[]{25,50,15,10});
	        table.setSplitLate(false);
			
		// th "PatientID" + "PatientName"
			cell= new PdfPCell(new Paragraph(new Chunk("PatientID",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			table.addCell(cell);
			
			cell= new PdfPCell(new Paragraph(new Chunk("PACIENTE",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(3);
			table.addCell(cell);
		// th "PatientID" + "PatientName"
			
		// data "PatientID" + "PatientName"
			cell= new PdfPCell(new Paragraph(new Chunk("38277317",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			table.addCell(cell);
			
			cell= new PdfPCell(new Paragraph(new Chunk("Nahuel López",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(3);
			table.addCell(cell);
		// data "PatientID" + "PatientName"
			
		// th "StudyDate" + "StudyDesc" + "StudyMod"
			cell= new PdfPCell(new Paragraph(new Chunk("Fecha",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			table.addCell(cell);
		
			cell= new PdfPCell(new Paragraph(new Chunk("Estudio",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(2);
			table.addCell(cell);
		
			cell= new PdfPCell(new Paragraph(new Chunk("Mod",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			table.addCell(cell);
		// th "StudyDate" + "StudyDesc" + "StudyMod"
			
		// data "StudyDate" + "StudyDesc" + "StudyMod"
			cell= new PdfPCell(new Paragraph(new Chunk("06/07/1994",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			table.addCell(cell);
			
			cell= new PdfPCell(new Paragraph(new Chunk("Reso de Muslo",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell= new PdfPCell(new Paragraph(new Chunk("MR",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			table.addCell(cell);
		// data "StudyDate" + "StudyDesc" + "StudyMod"
			
		// th "StudyReport"
			cell= new PdfPCell(new Paragraph(new Chunk("INFORME",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(22);
			cell.setColspan(4);
			table.addCell(cell);
		// th "StudyReport"
		
		// data	"StudyReport"
			cell= new PdfPCell(new Paragraph(new Chunk("\n"+informeMR+"\n",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_LEFT);
			cell.setColspan(4);
			table.addCell(cell);
		// data	"StudyReport"
			
			
			
		// th "StudyReportPhysician" + "StudyReportPhysicianMatr"
			cell= new PdfPCell(new Paragraph(new Chunk("Informe realizado por:",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell= new PdfPCell(new Paragraph(new Chunk("Matrícula",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK))));
			//cell.setBackgroundColor(theadColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(2);
			table.addCell(cell);
		// th "StudyReportPhysician" + "StudyReportPhysicianMatr"
			
		// data "StudyReportPhysician" + "StudyReportPhysicianMatr"
			cell= new PdfPCell(new Paragraph(new Chunk("El Dr. Chapatín",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell= new PdfPCell(new Paragraph(new Chunk("MN 1234",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK))));
			//cell.setBackgroundColor(backgroundColor);
			cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			cell.setFixedHeight(20);
			cell.setColspan(2);
			table.addCell(cell);
		// data "StudyReportPhysician" + "StudyReportPhysicianMatr"
		
			document.add(table);

			document.close();

		} catch(Exception ex){ System.out.println(ex.getMessage()); }
	}

	
	
}