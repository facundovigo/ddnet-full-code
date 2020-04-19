package app_objetos;

//import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.*;

public class app_Table  extends JTable{

	private static final long serialVersionUID = 1L;

	public app_Table(){
		
		
		DefaultTableModel modelo = new DefaultTableModel( null,
			//nombres de las columnas
			new String[]{"ID CORREO", "ASUNTO DEL CORREO", "DIRECCIÓN DESTINATARIO", "FECHA EVENTO"}) 
		{
			private static final long serialVersionUID = 1L;
		
		//tipo de dato de las columnas
			Class types[] = new Class[]{
					java.lang.String.class,
					java.lang.String.class,
                    java.lang.String.class,
                    java.lang.String.class,
                    java.lang.String.class,
                    java.lang.String.class
                };
			
		//si pueden editarse	
			boolean[] canEdit = new boolean[] {false,false,false,false,false,false};
		
		//setear en la tabla
			@Override
			public Class getColumnClass(int columnIndex) {return types [columnIndex];}
			@Override
			public boolean isCellEditable(int rowIndex, int colIndex){return canEdit[colIndex];}
        };
        
        setModel(modelo);	//modelo de tabla
        
        getTableHeader().setReorderingAllowed(false);	//las columnas no se pueden reordenar
        
        setRowSorter(new TableRowSorter<>(modelo));	//ordenar cada columna ascendente o descendentemente, al hacer click
        
        setDefaultRenderer(Object.class, new app_TableRender());
	}

}

