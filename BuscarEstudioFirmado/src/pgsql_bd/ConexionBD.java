package pgsql_bd;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

import javax.swing.JOptionPane;

public class ConexionBD {
	
	public Connection conn() throws SQLException{
		
		String host = "", port = "", bd = "", user = "", pswd = "";
		
		try{ Class.forName("org.postgresql.Driver"); }
		catch(ClassNotFoundException e){}
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File ("config/config.ini")));
			
			host = prop.getProperty("hostname");
			port = prop.getProperty("port");
			bd = prop.getProperty("database");
			user = prop.getProperty("user");
			pswd = prop.getProperty("password");
			
		} catch (Exception e) {JOptionPane.showMessageDialog(	null, "Error al cargar datos BD: "+e.toString(), 
																"ERROR", JOptionPane.ERROR_MESSAGE	);}
		
		return DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+bd, user, pswd);
	}
}
