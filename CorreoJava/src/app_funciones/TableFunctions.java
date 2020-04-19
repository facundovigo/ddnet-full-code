package app_funciones;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JTable;
import javax.swing.table.*;

import pgsql_bd.DAO.MailDAO;
import pgsql_bd.DTO.MailDTO;

public class TableFunctions {
	
	MailDAO dao = new MailDAO();
	private static final SimpleDateFormat DATE_FORMAT_IN = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat DATE_FORMAT_OUT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public void addRow(JTable tabla) throws ParseException{
		
		DefaultTableModel defModel = (DefaultTableModel)tabla.getModel();
		
		if(tabla.getColumnCount() == 6){
			tabla.removeColumn(tabla.getColumnModel().getColumn(5));
			tabla.removeColumn(tabla.getColumnModel().getColumn(4));
		}
		
		else if(tabla.getColumnCount() == 5)
					tabla.removeColumn(tabla.getColumnModel().getColumn(4));
		
		defModel.setColumnCount(4);
		
		while(tabla.getRowCount() > 0) defModel.removeRow(0);
		
		try {
			for(MailDTO dto : dao.getNoSentMails()){
				Object[] fila = {
						dto.getId(),
						parseAsuntoItoS(dto.getSubject()),
						dto.getReceiver(),
						DATE_FORMAT_OUT.format(DATE_FORMAT_IN.parse(dto.getDateRecord()))
				};
				if(fila != null) defModel.addRow(fila);
			}
		} catch (SQLException e) {}
		
	}
	
	public void addRowReceivedMail(JTable tabla) throws ParseException{
		
		DefaultTableModel defModel = (DefaultTableModel)tabla.getModel();
		
		if(tabla.getColumnCount() > 5)
				tabla.removeColumn(tabla.getColumnModel().getColumn(5));
		else if(tabla.getColumnCount() < 5)
				defModel.addColumn("FECHA ENVÍO");
		
		defModel.setColumnCount(5);
		
		while(tabla.getRowCount() > 0) defModel.removeRow(0);
		
		try {
			for(MailDTO dto : dao.getReceivedMails()){
				Object[] fila = {
						dto.getId(),
						parseAsuntoItoS(dto.getSubject()),
						dto.getReceiver(),
						DATE_FORMAT_OUT.format(DATE_FORMAT_IN.parse(dto.getDateRecord())),
						DATE_FORMAT_OUT.format(DATE_FORMAT_IN.parse(dto.getDateSent()))
				};
				if(fila != null) defModel.addRow(fila);
			}
		} catch (SQLException e) {}
		
	}

	public void addRowNotReceivedMail(JTable tabla) throws ParseException{
	
		DefaultTableModel defModel = (DefaultTableModel)tabla.getModel();
		
		if(tabla.getColumnCount() == 4){
		
			defModel.addColumn("FECHA ENVÍO");
			defModel.addColumn("DETALLE ERROR");
		}
		else if(tabla.getColumnCount() == 5){
			
			defModel.addColumn("DETALLE ERROR");
		}
		
		defModel.setColumnCount(6);
		
		while(tabla.getRowCount() > 0) defModel.removeRow(0);
	
		try {
			for(MailDTO dto : dao.getNotReceivedMails()){
				Object[] fila = {
						dto.getId(),
						parseAsuntoItoS(dto.getSubject()),
						dto.getReceiver(),
						DATE_FORMAT_OUT.format(DATE_FORMAT_IN.parse(dto.getDateRecord())),
						DATE_FORMAT_OUT.format(DATE_FORMAT_IN.parse(dto.getDateSent())),
						dto.getErrorDetail()
				};
				if(fila != null) defModel.addRow(fila);
			}
		} catch (SQLException e) {}
	
	}
	
	public String parseAsuntoItoS(int asunto_i){
		
		String asunto_s = "";
		
		switch(asunto_i){
			
			case 1 : asunto_s = "Estudio Asignado";
					 break;
			case 2 : asunto_s = "Estudio Reasignado";
					 break;
			case 3 : asunto_s = "Emergencia Médica";
					 break;
			case 4 : asunto_s = "Restaurar Contraseña";
					 break;
			default: break;
		}
		
		return asunto_s;
	}
	
	public int parseAsuntoStoI(String asunto_s){
		
		int asunto_i = 0;
		
		switch(asunto_s){
		
			case "Estudio Asignado" : 
					asunto_i = 1;
					break;
			case "Estudio Reasignado" : 
					asunto_i = 2;
					break;
			case "Emergencia Médica" : 
					asunto_i = 3;
					break;
			case "Restaurar Contraseña" :
					asunto_i = 4;
			default: break;
			
		}
		
		return asunto_i;
	}
	
}
