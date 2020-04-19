package pdf.Templates;

import java.awt.Color;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class TopPageTemplate {

	public void setPageTemplate
	(	PdfWriter writer,
		Document documento,
		String informe,
		boolean pieDePagina	) throws IOException, DocumentException {
		
		/**
		 * 	Logo Institucional encabezado de página
		 */
		String rutaImagen= "/home/server/Apps/ddnet/logos/institucional.jpg";
	    Image foto= Image.getInstance(rutaImagen);
	    foto.scaleAbsolute(500f,65f);
	    foto.setAlignment(Chunk.ALIGN_MIDDLE);
	    documento.add(foto);
	    
	    /**
	     * 	Generar el Cuerpo del Informe
	     */
	    PdfPTable tabla= new PdfPTable(1);
	    tabla.setWidthPercentage(100);
	    tabla.setHorizontalAlignment(Chunk.ALIGN_CENTER);
	    tabla.setSpacingBefore(15);
	    
	    Chunk chunk; Paragraph paragraph; PdfPCell cell; PdfPTable childTable; PdfPCell childCell;
	    BaseColor backgroundColor= new BaseColor(new Color(230,230,230));
	    BaseColor theadColor= new BaseColor(new Color(210,210,210));
	    String mensaje= "";
	    
	    for(int i=0; i<6; i++){
	    	switch(i) {
	    	case 0: childTable= new PdfPTable(2);
	    			childTable.setWidths(new float[]{25,75});
	    			for(int c=0; c<2; c++){
	    				switch(c){
	    				case 0:mensaje= "PatientID"; break;
	    				case 1:mensaje= "PACIENTE"; break;
	    				}
	    				chunk= new Chunk(mensaje,new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK));
	    				paragraph= new Paragraph();
	    				paragraph.add(chunk);
	    				childCell= new PdfPCell(paragraph);
	    				childCell.setFixedHeight(20);
			    		childCell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			    		childCell.setBorderColor(BaseColor.BLACK);
			    		childCell.setBackgroundColor(theadColor);
				    	childTable.addCell(childCell);
	    			}
	    			cell= new PdfPCell(childTable);
	    			tabla.addCell(cell);
	    	break;
	    	case 1: childTable= new PdfPTable(2);
	    			childTable.setWidths(new float[]{25,75});
	    			for(int c=0; c<2; c++){
	    				switch(c){
	    				case 0:mensaje= "38277317"; break;
	    				case 1:mensaje= "Nahuel López"; break;
	    				}
	    				chunk= new Chunk(mensaje,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK));
	    				paragraph= new Paragraph();
	    				paragraph.add(chunk);
	    				childCell= new PdfPCell(paragraph);
	    				childCell.setFixedHeight(25);
			    		childCell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			    		childCell.setBorderColor(BaseColor.BLACK);
			    		childCell.setBackgroundColor(backgroundColor);
				    	childTable.addCell(childCell);
	    			}
	    			cell= new PdfPCell(childTable);
	    			tabla.addCell(cell);
	    	break;		
	    	case 2: childTable= new PdfPTable(3);
	    			childTable.setWidths(new float[]{25,65,10});
	    			for(int c=0; c<3; c++){
	    				switch(c){
	    				case 0:mensaje= "Fecha"; break;
	    				case 1:mensaje= "Estudio"; break;
	    				case 2:mensaje= "Mod"; break;
	    				}
	    				chunk= new Chunk(mensaje,new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK));
	    				paragraph= new Paragraph();
	    				paragraph.add(chunk);
	    				childCell= new PdfPCell(paragraph);
	    				childCell.setFixedHeight(20);
			    		childCell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			    		childCell.setBorderColor(BaseColor.BLACK);
			    		childCell.setBackgroundColor(theadColor);
				    	childTable.addCell(childCell);
	    			}
	    			cell= new PdfPCell(childTable);
	    			tabla.addCell(cell);
	    	break;
	    	case 3:	childTable= new PdfPTable(3);
	    			childTable.setWidths(new float[]{25,65,10});
	    			for(int c=0; c<3; c++){
	    				switch(c){
	    				case 0:mensaje= "06-07-1994 16:36"; break;
	    				case 1:mensaje= "Reso de Muslo"; break;
	    				case 2:mensaje= "MR"; break;
	    				}
	    				chunk= new Chunk(mensaje,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK));
	    				paragraph= new Paragraph();
	    				paragraph.add(chunk);
	    				childCell= new PdfPCell(paragraph);
	    				childCell.setFixedHeight(25);
			    		childCell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
			    		childCell.setBorderColor(BaseColor.BLACK);
			    		childCell.setBackgroundColor(backgroundColor);
				    	childTable.addCell(childCell);
	    			}
	    			cell= new PdfPCell(childTable);
	    			tabla.addCell(cell);
			break;
	    	case 4: chunk= new Chunk("INFORME",new Font(Font.FontFamily.COURIER,12,Font.BOLD,BaseColor.BLACK));
		    	    paragraph= new Paragraph();
				    paragraph.add(chunk);
		    		cell= new PdfPCell(paragraph);
		    		cell.setFixedHeight(20);
		    		cell.setHorizontalAlignment(Chunk.ALIGN_CENTER);
		    		cell.setBorderColor(BaseColor.BLACK);
		    		cell.setBackgroundColor(theadColor);
			    	tabla.addCell(cell);
	    	break;
	    	case 5: chunk= new Chunk(informe,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK));
			    	paragraph= new Paragraph();
				    paragraph.add(chunk);
				    chunk= new Chunk("\n\n",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK));
				    paragraph.add(chunk);
	    			cell= new PdfPCell(paragraph);
	    			cell.setBorderColor(BaseColor.BLACK);
	    			cell.setBackgroundColor(backgroundColor);
	    			cell.setFixedHeight(475);
	    			tabla.addCell(cell);
	    	break;
	    	}
	    }
	    tabla.setSpacingAfter(4);
	    documento.add(tabla);
	    
	    /**
	     * 	Si se pidió agregar Pie de Página
	     */
	    if(pieDePagina){
		    String rutaPieImagen= "/home/server/Apps/ddnet/logos/INST_FOOTER1.jpg";
		    Image piePagina= Image.getInstance(rutaPieImagen);
		    piePagina.scaleAbsolute(500f,30f);
		    piePagina.setAlignment(Chunk.ALIGN_MIDDLE);
		    documento.add(piePagina);
	    }
	    
	    /**
	     * 	Número de página al final (una sola pág)
	     */
	    Paragraph p= new Paragraph();
	    p.add(new Chunk("página "+writer.getPageNumber(),new Font(Font.FontFamily.COURIER,8,Font.ITALIC,BaseColor.BLACK)));
	    p.setAlignment(Chunk.ALIGN_RIGHT);
	    documento.add(p);
	}
}
