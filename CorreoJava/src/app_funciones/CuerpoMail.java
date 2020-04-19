package app_funciones;

import pgsql_bd.DAO.*;
import pgsql_bd.DTO.*;

import java.sql.*;

public class CuerpoMail {
	
	StudyDAO Sdao = new StudyDAO();
	UserDAO Udao = new UserDAO();
	RestorePasswordDAO Rdao = new RestorePasswordDAO();
	
	public String getMailBody(Long mailID, int asunto, String fecha) throws SQLException{
		
		String cuerpo = "";
		Long studyId = Sdao.getStudyByMail(mailID);
		UserDTO userDTO = Udao.getUserByMailID(mailID).get(0);
		String dia = fecha.substring(0, 10);
		String hora = fecha.substring(11, 19);
		
		switch(asunto){
		
		case 1: cuerpo = asuntoEstudioAsignado(studyId, dia, hora);
				break;
		case 2: cuerpo = asuntoEstudioReasignado(studyId, dia, hora);
				break;
		case 3: cuerpo = asuntoEmergenciaMedica(studyId, userDTO, dia, hora);
				break;
		case 4: cuerpo = asuntoRestaurarContrasena(mailID, userDTO);
				break;
		default: cuerpo = "";
				break;
		}
		
		return cuerpo;
	}
	
	public String asuntoEstudioAsignado(Long studyID, String dia, String hora) throws SQLException {
		
		String cuerpo = "";
		LegacyStudyDTO studyDTO = Sdao.getLegacyStudyByPK(studyID).get(0);
		LegacyPatientDTO patDTO = Sdao.getLegacyPatientByPK(studyDTO.getPatientID()).get(0);
		
		cuerpo += "En el día: <b>"+dia+"</b> a las: <b>"+hora+"</b> hs se le ha asignado un estudio.<br><br>";
		
		if(patDTO.getPatName() != null)
			cuerpo += "Paciente: <b>" + patDTO.getPatName() + "</b><br>";
		if(patDTO.getPatSex() != null)
			cuerpo += "Sexo: <b>" + patDTO.getPatSex() + "</b><br>";
		if(patDTO.getPatAge() != null)
			cuerpo += "Edad: <b>" + patDTO.getPatAge() + "</b><br>";
		if(studyDTO.getStudyDate() != null)
			cuerpo += "Fecha Estudio: <b>" + studyDTO.getStudyDate() + "</b><br>";
		if(studyDTO.getStudyMod() != null)
			cuerpo += "Modalidad: <b>" + studyDTO.getStudyMod() + "</b><br>";
		if(studyDTO.getStudyDesc() != null)
			cuerpo += "Descripción: <b>" + studyDTO.getStudyDesc() + "</b><br>";
		
		cuerpo += "<br>Queda bajo su responsabilidad tanto el informe como el seguimiento del mencionado estudio.";
		
		if(Sdao.getDatosClinicosByStudyID(studyID) != null && !Sdao.getDatosClinicosByStudyID(studyID).isEmpty()){
			DatosClinicosDTO dcDTO = Sdao.getDatosClinicosByStudyID(studyID).get(0);
		
			if(dcDTO.getPriority() == 2)
				cuerpo += "<br>Estudio <span style='color: red;'>URGENTE</span> a informar en <b>1 hora</b>.";
			else if(dcDTO.getPriority() == 1)
				cuerpo += "<br>Estudio <span style='color: orange;'>PREFERENTE</span> a informar en <b>24 horas</b>.";
			
		} else cuerpo += "<br>Estudio <b>ELECTIVO</b> a informar en <b>48 horas</b>.";
		
		return cuerpo;
	}
	
	public String asuntoEstudioReasignado(Long studyID, String dia, String hora) throws SQLException {
		
		String cuerpo = "";
		LegacyStudyDTO studyDTO = Sdao.getLegacyStudyByPK(studyID).get(0);
		LegacyPatientDTO patDTO = Sdao.getLegacyPatientByPK(studyDTO.getPatientID()).get(0);
		
		cuerpo += "En el día: <b>"+dia+"</b> a las: <b>"+hora+"</b> hs se ha re-asignado un estudio, ";
		
		cuerpo += "asignado a usted previamente, para otro usuario.<br><br>";
		
		if(patDTO.getPatName() != null)
			cuerpo += "Paciente: <b>" + patDTO.getPatName() + "</b><br>";
		if(patDTO.getPatSex() != null)
			cuerpo += "Sexo: <b>" + patDTO.getPatSex() + "</b><br>";
		if(patDTO.getPatAge() != null)
			cuerpo += "Edad: <b>" + patDTO.getPatAge() + "</b><br>";
		if(studyDTO.getStudyDate() != null)
			cuerpo += "Fecha Estudio: <b>" + studyDTO.getStudyDate() + "</b><br>";
		if(studyDTO.getStudyMod() != null)
			cuerpo += "Modalidad: <b>" + studyDTO.getStudyMod() + "</b><br>";
		if(studyDTO.getStudyDesc() != null)
			cuerpo += "Descripción: <b>" + studyDTO.getStudyDesc() + "</b><br>";
		
		cuerpo += "<br>Ya no posee la responsabilidad sobre el informe o el seguimiento del mismo.";
		
		return cuerpo;
	}
	
	public String asuntoEmergenciaMedica(Long studyID, UserDTO uDTO, String dia, String hora) throws SQLException {
		
		String cuerpo = "";
		LegacyStudyDTO studyDTO = Sdao.getLegacyStudyByPK(studyID).get(0);
		LegacyPatientDTO patDTO = Sdao.getLegacyPatientByPK(studyDTO.getPatientID()).get(0);
		
		cuerpo += "En el día: <b>"+dia+"</b> a las: <b>"+hora+"</b> hs se ha declarado una ";
		
		cuerpo += "<span style='color: red;'>EMERGENCIA MÉDICA</span>.<br><br>";
		
		if(patDTO.getPatName() != null)
			cuerpo += "Paciente: <b>" + patDTO.getPatName() + "</b><br>";
		if(patDTO.getPatSex() != null)
			cuerpo += "Sexo: <b>" + patDTO.getPatSex() + "</b><br>";
		if(patDTO.getPatAge() != null)
			cuerpo += "Edad: <b>" + patDTO.getPatAge() + "</b><br>";
		if(studyDTO.getStudyDate() != null)
			cuerpo += "Fecha Estudio: <b>" + studyDTO.getStudyDate() + "</b><br>";
		if(studyDTO.getStudyMod() != null)
			cuerpo += "Modalidad: <b>" + studyDTO.getStudyMod() + "</b><br>";
		if(studyDTO.getStudyDesc() != null)
			cuerpo += "Descripción: <b>" + studyDTO.getStudyDesc() + "</b><br>";
		
		cuerpo += "<br>El informe del estudio antes mencionado fue realizado por ";
		
		cuerpo += "<b>" + uDTO.getFirstName() + " " + uDTO.getLastName() + "</b>, ";
		
		cuerpo += "usuario <b>" + uDTO.getLogin() + "</b>.";
		
		return cuerpo;
	}
	
	public String asuntoRestaurarContrasena(Long mailID, UserDTO uDTO) throws SQLException {
		
		String cuerpo = "";
		RestorePasswordDTO restDTO = Rdao.getRestorePasswordByMailID(mailID).get(0);
		
		cuerpo += "Usuario: <b>"+ uDTO.getLogin() +"</b><br><br>" ;
		
		cuerpo += "Se ha registrado el pedido de restaurar su contraseña.<br>" 
				+ "Para hacerlo, ingrese en la siguiente dirección: <br><br>" ;
		
		cuerpo += "<a target='_blank' href='http://" + restDTO.getRestHost() + 
				  ":8180/ddnet-web/new-password.html?key="  + restDTO.getRestKey() + "'> RESTAURAR CONTRASEÑA</a>" ;
		
		cuerpo += "<br><br>Se le informa que tiene hasta 24hs del pedido para la "
				+ "restauración, de lo contrario este enlace ya no será válido. <br>"
				+ "Además posee un límite de ingresos si es que entra en la página "
				+ "y no realiza las modificaciones correspondientes." ;
		
		
		return cuerpo;
	}
}
