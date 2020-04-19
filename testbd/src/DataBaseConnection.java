

import java.sql.*;

import javax.naming.*;
import javax.sql.*;

public class DataBaseConnection {
	
	public static final String DATASOURCE_DATOJAVA = "java:/pacsDS";
	
	public static Connection getConnection() throws NamingException {
	    Connection cnn = null;
	    try {
	    	InitialContext initialContext = new InitialContext();
	    	DataSource ds = (DataSource) initialContext.lookup("java:pacsDS");
	    	cnn = ds.getConnection();
	     
	    } catch (Exception e){System.out.println(e.toString()+": "+e.getMessage());}
	    
	    return cnn;
	 }
	static DataSource ds = null;
	static InitialContext ic;
	static Connection con = null;
	
	public static Statement st() throws Exception{
		
		Statement st = null;
		
			ic = new InitialContext();
			ds = (DataSource) ic.lookup("java:pacsDS");
            con = ds.getConnection();
            st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		
		return st;
	}
}