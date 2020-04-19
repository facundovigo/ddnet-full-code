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

public class MiddlePageTemplate {

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
	    
	    Chunk chunk; Paragraph paragraph; PdfPCell cell;
	    BaseColor backgroundColor= new BaseColor(new Color(230,230,230));
	    
	    chunk= new Chunk(informe,new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK));
    	paragraph= new Paragraph();
	    paragraph.add(chunk);
	    chunk= new Chunk("\n\n",new Font(Font.FontFamily.COURIER,11,Font.NORMAL,BaseColor.BLACK));
	    paragraph.add(chunk);
		cell= new PdfPCell(paragraph);
		cell.setBorderColor(BaseColor.BLACK);
		cell.setBackgroundColor(backgroundColor);
		cell.setFixedHeight(600);
		tabla.addCell(cell);
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
