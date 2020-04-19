package app_objetos;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import pgsql_bd.DAO.MailDAO;
import correojava.CorreoJava;
import app_funciones.*;

public class app_Frame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	app_Table tabla = new app_Table();
	TableFunctions f = new TableFunctions();
	CuerpoMail cm = new CuerpoMail();
	JPanel panel;
	JScrollPane scrollpane;
	JTable tbMail;
	JLabel lblInfo;
	JButton btnStart, btnTbNo, btnTbYes;
	Timer timer = new Timer(0, null);
	
	public app_Frame(){
		
		tbMail = tabla;
		tbMail.setBounds(10, 10, 800, 300);
		
		scrollpane = new JScrollPane(tbMail);
		scrollpane.setBounds(10, 10, 800, 300);
		
		lblInfo = new JLabel("Cantidad:");
		lblInfo.setBounds(140, 330, 100, 25);
		
		btnStart = new JButton("Comenzar");
		btnStart.setBounds(10, 330, 120, 25);
		btnStart.setFocusable(false);
		btnStart.addActionListener(this);
		
		btnTbYes = new JButton("Ver Correos Recibidos");
		btnTbYes.setBounds(500, 330, 150, 25);
		btnTbYes.setFont(new Font(btnStart.getFont().getFontName(), Font.PLAIN, 9));
		btnTbYes.setFocusable(false);
		btnTbYes.addActionListener(btnTbYesActionPerformed);
		
		btnTbNo = new JButton("Ver Correos Fallidos");
		btnTbNo.setBounds(660, 330, 150, 25);
		btnTbNo.setFont(new Font(btnStart.getFont().getFontName(), Font.PLAIN, 9));
		btnTbNo.setFocusable(false);
		btnTbNo.addActionListener(btnTbNoActionPerformed);
		
		panel = new JPanel();
		panel.setLayout(null);
		panel.add(lblInfo);
		panel.add(btnStart);
		panel.add(btnTbYes);
		panel.add(btnTbNo);
		
		add(scrollpane);
		add(panel);
		setBounds(25, 25, 820, 430);
		setTitle("Envío de Correos");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true); 
		
		timer = new Timer(3000, new ActionListener(){
			
			@Override
	        public void actionPerformed(ActionEvent e) {
				
			try {f.addRow(tbMail);} catch (ParseException p) {}
			lblInfo.setText("Cantidad: " + tbMail.getRowCount());
				
			/*******
			
			long mailID = (long) tbMail.getValueAt(1, 0);
			int asunto = f.parseAsuntoStoI(tbMail.getValueAt(1, 1).toString());
			String fecha = tbMail.getValueAt(1, 3).toString();
			
			try {
				System.out.println(cm.getMailBody(mailID, asunto, fecha));
			} catch (SQLException exc) {
				// TODO Auto-generated catch block
				System.out.println(exc.toString());
			}
			
			*******/
			
			for(int i = 0; i < tbMail.getRowCount(); i++){
				
				//acción sobre cada fila de la tabla
				DefaultTableModel defModel = (DefaultTableModel)tbMail.getModel();
				
				long mailID = (long) tbMail.getValueAt(i, 0);
				int asunto = f.parseAsuntoStoI(tbMail.getValueAt(i, 1).toString());
				String destinatario = tbMail.getValueAt(i, 2).toString();
				String fecha = tbMail.getValueAt(i, 3).toString();
				
				try {new CorreoJava().EnviarCorreo(	tbMail.getValueAt(i, 1).toString(),
											  		cm.getMailBody(mailID, asunto, fecha),
											  		destinatario, mailID);
				
					 new MailDAO().setMailAsSent(mailID);
					 new MailDAO().setDateMailSent(mailID);
				} catch (SQLException exc) {System.out.println(exc.toString());}
				
				//pausa de un segundo
				try { Thread.sleep(1000); } catch (InterruptedException e1) { e1.printStackTrace(); } 
				
					defModel.removeRow(i);
					lblInfo.setText("Cantidad: " + tbMail.getRowCount());
				}
				
		}});
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                timer.stop();
            }
        });
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	if(btnStart.getText().equals("Comenzar")){
		timer.start();
		btnStart.setText("Pausa");
	}
	else if(btnStart.getText().equals("Pausa")){
		timer.stop();
		tbMail.repaint();
		btnStart.setText("Comenzar");
		lblInfo.setText("Cantidad: " + tbMail.getRowCount());
	}
	
	}
	
	ActionListener btnTbYesActionPerformed = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			timer.stop();
			try {f.addRowReceivedMail(tbMail);} catch (ParseException e1) {}
			lblInfo.setText("Cantidad: " + tbMail.getRowCount());
			btnStart.setText("Comenzar");
		}
	};
	ActionListener btnTbNoActionPerformed = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			timer.stop();
			try{f.addRowNotReceivedMail(tbMail);}catch(ParseException e1){}
			lblInfo.setText("Cantidad: " + tbMail.getRowCount());
			btnStart.setText("Comenzar");
		}
	};
	
}
