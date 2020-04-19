import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;










/*Librerías para trabajar con archivos excel*/
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class ExcelTest {

	public static void main(String[] args) {
		
		/*La ruta donde se creará el archivo*/
        String rutaArchivo = System.getProperty("user.home")+"/ejemploExcelJava.xls";
        
        /*Se crea el objeto de tipo File con la ruta del archivo*/
        File archivoXLS = new File(rutaArchivo);
        
        /*Si el archivo existe se elimina*/
        if(archivoXLS.exists()) archivoXLS.delete();
        
        /*Se crea el archivo*/
        try {
			archivoXLS.createNewFile();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
        
        
        /*Se crea el libro de excel usando el objeto de tipo Workbook*/
        Workbook libro = new HSSFWorkbook();
        
        Font font = libro.createFont();
        Font font1 = libro.createFont();
        CellStyle style = libro.createCellStyle();
        CellStyle style1 = libro.createCellStyle();
        
        font.setColor(HSSFColor.BRIGHT_GREEN.index);
        style.setFont(font);
        
        font1.setBold(true);
        style1.setFont(font1);
        
        /*Se inicializa el flujo de datos con el archivo xls*/
        try {
			FileOutputStream archivo = new FileOutputStream(archivoXLS);
		
        /*Utilizamos la clase Sheet para crear una nueva hoja de trabajo dentro del libro que creamos anteriormente*/
        Sheet hoja = libro.createSheet("Mi hoja de trabajo 1");
        
        
        /*Hacemos un ciclo para inicializar los valores de 10 filas de celdas*/
        for(int f=0;f<10;f++){
            
        	/*La clase Row nos permitirá crear las filas*/
            Row fila = hoja.createRow(f);
            
            /*Cada fila tendrá 5 celdas de datos*/
            for(int c=0;c<5;c++){
                
            	/*Creamos la celda a partir de la fila actual*/
                Cell celda = fila.createCell(c);
                
                /*Si la fila es la número 0, estableceremos los encabezados*/
                if(f==0){
                    celda.setCellStyle(style1);
                	celda.setCellValue("Encabezado #"+c);
                    
                }else{
                	celda.setCellStyle(style);
                	/*Si no es la primera fila establecemos un valor*/
                    celda.setCellValue("Valor celda "+c+","+f);
                }
            }
        }
        
        
        for(int c=0;c<5;c++){
        	hoja.autoSizeColumn(c);
        }
        
        /*Escribimos en el libro*/
        libro.write(archivo);
        
        /*Cerramos el flujo de datos*/
        archivo.close();
        
        libro.close();
        
        /*Y abrimos el archivo con la clase Desktop*/
        Desktop.getDesktop().open(archivoXLS);
        
        } catch(Exception e){
        	System.out.println(e.toString());
        }
        
        
	}

}
