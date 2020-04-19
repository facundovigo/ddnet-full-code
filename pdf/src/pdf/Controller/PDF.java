package pdf.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import pdf.ReportExamples.ReportTemplate;
import pdf.Templates.BottomPageTemplate;
import pdf.Templates.MiddlePageTemplate;
import pdf.Templates.SinglePageTemplate;
import pdf.Templates.TopPageTemplate;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PDF {
	
	private final static SinglePageTemplate DOCUMENTO_1_PAG= new SinglePageTemplate();
	private final static TopPageTemplate DOCUMENTO_MULTI_PAG_TOP= new TopPageTemplate();
	private final static MiddlePageTemplate DOCUMENTO_MULTI_PAG_MIDDLE= new MiddlePageTemplate();
	private final static BottomPageTemplate DOCUMENTO_MULTI_PAG_BOTTOM= new BottomPageTemplate();
	private final static ReportTemplate templ= new ReportTemplate();
	
	public static void main(String[] args) throws IOException, DocumentException {
		String informeMR= templ.getFromArchivo();
		
		File archivoPDF= new File("/home/server/Escritorio/nahu.pdf");
		if(archivoPDF.exists()) archivoPDF.delete();
		archivoPDF.createNewFile();
		
		FileOutputStream archivo= new FileOutputStream(archivoPDF);
		Document documento= new Document();
	    final PdfWriter writer= PdfWriter.getInstance(documento, archivo);
        
	    Paragraph parrafo;
        int i;
        DOCUMENTO_1_PAG.setEncabezado("\n\nPrueba de encabezado en iText");
        
        writer.setPageEvent(DOCUMENTO_1_PAG);
	    
	    documento.open();
	    	
	    for(i=0; i<10; i++)
        {
            //parrafo = new Paragraph("Esta es una de las paginas de prueba de nuestro programa, es la pagina numero 0x" + String.format("%03X", i+42));
	    	parrafo = new Paragraph("Esta es una de las paginas");
            parrafo.setAlignment(Element.ALIGN_CENTER);
            
            
            documento.add(parrafo);
            //documento.newPage();
        }
	    //DOCUMENTO_1_PAG.setPageTemplate(writer, documento, informeMR, true);
	    documento.close();
	}
	
	public static void recursivePagesReading(PdfWriter writer, Document documento, String texto, int lineas){
		int l= 0;
		if(lineas>=44){
			for(int x=0; x<texto.length(); x++) {
				try {
				if(texto.charAt(x)=='\n') l++;
				if(l==54) {
					DOCUMENTO_MULTI_PAG_MIDDLE.setPageTemplate(writer, documento, texto, true);
					if(texto.substring(++x,texto.length())!=null)
					recursivePagesReading(writer,documento,texto.substring(++x,texto.length()),lineas-53);
					else DOCUMENTO_MULTI_PAG_BOTTOM.setPageTemplate(writer, documento, "\n", true);
					break;
				}} catch (IOException | DocumentException e) { e.printStackTrace(); }
			}
		} else {
			try {
			DOCUMENTO_MULTI_PAG_BOTTOM.setPageTemplate(writer, documento, texto, true);
			} catch (IOException | DocumentException e) { e.printStackTrace(); }
		}
	}
}
