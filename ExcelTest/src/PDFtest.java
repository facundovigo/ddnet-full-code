
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PDFtest {

	public static void main(String[] args) throws DocumentException, IOException {
		
		String rutaArchivo = System.getProperty("user.home")+"/testPDF.pdf";
		File archivoPDF = new File(rutaArchivo);
		if(archivoPDF.exists()) archivoPDF.delete();
		archivoPDF.createNewFile();
		
		FileOutputStream archivo = new FileOutputStream(archivoPDF);
	    Document documento = new Document();
	    PdfWriter.getInstance(documento, archivo);
	    Paragraph paragraph;
	    
	    documento.open();
	    	
		    Image foto = Image.getInstance("/home/server/Apps/ddnet/logos/INST26.jpg");
		    //foto.scaleToFit(500, 100);
		    foto.scaleAbsolute(500f, 70f);
		    foto.setAlignment(Chunk.ALIGN_MIDDLE);
		    documento.add(foto);
	    
		    paragraph = new Paragraph("Este es el segundo y tiene una fuente rara",
	                FontFactory.getFont("arial",   // fuente
	                15,                            // tamaño
	                Font.BOLDITALIC,               // estilo
	                BaseColor.RED));
		    paragraph.setSpacingAfter(50);
		    documento.add(paragraph);
		    
		    PdfPTable tabla = new PdfPTable(3);
		    for (int i = 0; i < 15; i++){ 

			    PdfPCell cell = new PdfPCell(new Paragraph("okok" + i));
			    cell.setBorder(Rectangle.NO_BORDER);
			    cell.setBackgroundColor(new BaseColor(new Color(245, 245, 204)));
		    	tabla.addCell(cell);
		    }
		    
		    documento.add(tabla);
		      
	    documento.close();
	    
	    Desktop.getDesktop().open(archivoPDF);
	}

}
