package app.bd.conn;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class PgSQLConn {
	
	private static String PGSQL_HOST = "";
	private static String PGSQL_PORT = "";
    private static String PGSQL_DATABASE = "";
    private static String PGSQL_USERNAME = "";
    private static String PGSQL_PASSWORD = "";
	
	public Connection getConn() throws SQLException {
		
		try {
			Properties prop = new Properties();
			
			String fullPath= PgSQLConn.class.getProtectionDomain().getCodeSource().getLocation().toString();
		    fullPath = fullPath.replace('\\', '/');
		    int i= fullPath.indexOf(':')+1 ;
			int f= fullPath.indexOf("default")+7;
		    fullPath= fullPath.substring(i,f);
		    fullPath+= "/conf/dd-viewer-config.properties";
		    
		    prop.load(new FileInputStream(fullPath));
	    	PGSQL_HOST = prop.getProperty("pgsql.host");
	    	PGSQL_PORT = prop.getProperty("pgsql.port");
	    	PGSQL_DATABASE = prop.getProperty("pgsql.database");
	    	PGSQL_USERNAME = prop.getProperty("pgsql.username");
	    	PGSQL_PASSWORD = prop.getProperty("pgsql.password");
		    
		} catch (IOException ex) {
			PGSQL_HOST = "127.0.0.1";
	    	PGSQL_PORT = "5432";
	    	PGSQL_DATABASE = "pacsdb";
	    	PGSQL_USERNAME = "postgres";
	    	PGSQL_PASSWORD = "pacsdb";
	    	ex.printStackTrace();
		}
		
		try{ Class.forName("org.postgresql.Driver"); }
		catch(ClassNotFoundException e){e.printStackTrace();}
		
		return DriverManager.getConnection("jdbc:postgresql://"+PGSQL_HOST+":"+PGSQL_PORT+"/"+PGSQL_DATABASE, PGSQL_USERNAME, PGSQL_PASSWORD);
	}
}
