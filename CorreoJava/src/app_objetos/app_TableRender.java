package app_objetos;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class app_TableRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int column)
	{

		super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);

		if(table.getColumnCount() == 6)
			this.setForeground(new Color(230,0,0));
		else if(table.getColumnCount() == 5){
			this.setForeground(new Color(0,170,0));
		}
		
		return this;	
	}
}
