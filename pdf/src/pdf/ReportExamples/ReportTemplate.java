package pdf.ReportExamples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReportTemplate {

	public String getFromArchivo() throws IOException{
		String cadena, output="";
	    FileReader f= new FileReader("/home/server/Apps/ddnet/report-templates-nuevo/US/ECO OBSTETRICO SCAN FETAL.txt");
	    BufferedReader b= new BufferedReader(f);
	    while((cadena= b.readLine())!=null) { output+= cadena+"\n"; }
	    b.close();
	    return output;
	}
}
