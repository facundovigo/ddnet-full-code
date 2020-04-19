package app_objetos;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import pgsql_bd.DAO.StudyInfoDAO;
import pgsql_bd.DTO.StudyInfoDTO;
import app_funciones.*;

public class app_Frame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	Funciones f = new Funciones();
	StudyInfoDAO sdao = new StudyInfoDAO();
	
	JPanel panel;
	JButton btnSearch, btnClearFirm;
	JRadioButton radAccno;
	JTextField txtDato;
	JLabel 	lblBuscarPor, lblDato, lblPat, lblPatInfo, lblDate, lblDateInfo,
			lblTime, lblTimeInfo, lblMod, lblModInfo, lblPK, lblPKInfo,
			lblReportState, lblReport;
	
	
	public app_Frame(){
		
		lblBuscarPor = new JLabel("buscar estudio por: ");
		lblBuscarPor.setBounds(10, 10, 170, 25);
		
		radAccno = new JRadioButton("Accession Number");
		radAccno.setBounds(170, 10, 200, 25);
		radAccno.setFocusable(false);
		radAccno.setSelected(true);
		
		//----------------

		lblDato = new JLabel("Estudio: ");
		lblDato.setBounds(10, 50, 100, 25);

		txtDato = new JTextField();
		txtDato.setBounds(90, 50, 200, 25);
		
		btnSearch = new JButton("Buscar");
		btnSearch.setBounds(320, 50, 120, 25);
		btnSearch.setFocusable(false);
		btnSearch.addActionListener(btnSearchActionPerformed);
		
		//----------------
		
		lblPat = new JLabel("Paciente:");
		lblPat.setBounds(70, 100, 100, 25);
		
		lblPatInfo = new JLabel();
		lblPatInfo.setBounds(150, 100, 230, 25);
		lblPatInfo.setFont(new Font(lblPat.getFont().getFontName(), Font.BOLD, 10));
		
		//----------------
		
		lblDate = new JLabel("Fecha Estudio:");
		lblDate.setBounds(70, 135, 120, 25);
		
		lblDateInfo = new JLabel();
		lblDateInfo.setBounds(190, 135, 190, 25);
		lblDateInfo.setFont(new Font(lblPat.getFont().getFontName(), Font.BOLD, 10));

		//----------------
		
		lblTime = new JLabel("Hora Estudio:");
		lblTime.setBounds(70, 170, 120, 25);
		
		lblTimeInfo = new JLabel();
		lblTimeInfo.setBounds(190, 170, 190, 25);
		lblTimeInfo.setFont(new Font(lblPat.getFont().getFontName(), Font.BOLD, 10));

		//----------------
		
		lblMod = new JLabel("Modalidad:");
		lblMod.setBounds(70, 205, 120, 25);
		
		lblModInfo = new JLabel();
		lblModInfo.setBounds(165, 205, 190, 25);
		lblModInfo.setFont(new Font(lblPat.getFont().getFontName(), Font.BOLD, 10));

		//----------------
		
		lblPK = new JLabel("Study PK:");
		lblPK.setBounds(70, 240, 120, 25);
		
		lblPKInfo = new JLabel();
		lblPKInfo.setBounds(165, 240, 190, 25);
		lblPKInfo.setFont(new Font(lblPat.getFont().getFontName(), Font.BOLD, 10));

		//----------------
		
		lblReport = new JLabel("Estado Informe:");
		lblReport.setBounds(40, 290, 120, 25);
		
		lblReportState = new JLabel();
		lblReportState.setBounds(170, 290, 120, 25);
		lblReportState.setFont(new Font(lblPat.getFont().getFontName(), Font.BOLD, 10));
		
		btnClearFirm = new JButton("Habilitar");
		btnClearFirm.setBounds(310, 290, 100, 25);
		btnClearFirm.setFocusable(false);
		btnClearFirm.addActionListener(btnClearActionPerformed);
		btnClearFirm.setEnabled(false);
		
		
		
		panel = new JPanel();
		panel.setLayout(null);
		
		panel.add(lblBuscarPor);
		panel.add(radAccno);

		panel.add(lblDato);
		panel.add(txtDato);
		panel.add(btnSearch);
		
		panel.add(lblPat);
		panel.add(lblPatInfo);
		
		panel.add(lblDate);
		panel.add(lblDateInfo);
		
		panel.add(lblTime);
		panel.add(lblTimeInfo);
		
		panel.add(lblMod);
		panel.add(lblModInfo);
		
		panel.add(lblPK);
		panel.add(lblPKInfo);
		
		panel.add(lblReport);
		panel.add(lblReportState);
		panel.add(btnClearFirm);
		
		add(panel);
		setBounds(25, 25, 455, 400);
		setTitle("Habilitar Informes Cerrados");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
	}
	
	
	ActionListener btnSearchActionPerformed = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(txtDato.getText() == null || txtDato.getText().trim().isEmpty()){
				
				JOptionPane.showMessageDialog(null, "Escriba un dato", "AVISO", JOptionPane.INFORMATION_MESSAGE); 
			
			} else{
			
				lblPatInfo.setText("");
				lblDateInfo.setText("");
				lblTimeInfo.setText("");
				lblModInfo.setText("");
				lblPKInfo.setText("");
				lblReportState.setForeground(Color.BLACK);
				lblReportState.setText("");
				btnClearFirm.setEnabled(false);
			
				int aux = 0;
			
				try {
					ArrayList<StudyInfoDTO> info = sdao.getDataByAccNumb(txtDato.getText());
					
					
					for(StudyInfoDTO i : info){
						
						lblPatInfo.setText(i.getPatName().replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " "));
						lblDateInfo.setText(i.getStudyDate().substring(0,10));
						lblTimeInfo.setText(i.getStudyDate().substring(11,19));
						lblModInfo.setText(i.getStudyMod());
						lblPKInfo.setText(i.getId().toString());
						aux =  i.isReported() ? 3 : i.getReportBody() == null || i.getReportBody().trim().isEmpty() ? 1 : 2;
					}
					
				} catch (Exception exc) {JOptionPane.showMessageDialog(	null, "Ocurrió un error al buscar los datos. \n" +
																		exc.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);}
				
				switch(aux){
					
					case 1: lblReportState.setForeground(Color.RED);
							lblReportState.setText("A INFORMAR");
							btnClearFirm.setEnabled(false);
							break;
					case 2: lblReportState.setForeground(new Color(200,200,0));
							lblReportState.setText("A CONFIRMAR");
							btnClearFirm.setEnabled(false);
							break;
					case 3: lblReportState.setForeground(new Color(0,180,0));
							lblReportState.setText("INFORMADO");
							btnClearFirm.setEnabled(true);
							break;
				}
			}
		}
	};
	
	ActionListener btnClearActionPerformed = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			try{
				sdao.ClearStudyReport(lblPKInfo.getText());
				
				JOptionPane.showMessageDialog(null, "El informe ha sido habilitado, en estado A CONFIRMAR", "HECHO", JOptionPane.PLAIN_MESSAGE);
				
				txtDato.setText("");
				lblPatInfo.setText("");
				lblDateInfo.setText("");
				lblTimeInfo.setText("");
				lblModInfo.setText("");
				lblPKInfo.setText("");
				lblReportState.setForeground(Color.BLACK);
				lblReportState.setText("");
				btnClearFirm.setEnabled(false);
				
			}catch(Exception exc) {JOptionPane.showMessageDialog(	null, "Ocurrió un error al habilitar el informe. \n" +
																	exc.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);}
		}
	};
	
}
