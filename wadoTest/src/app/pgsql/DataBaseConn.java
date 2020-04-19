package app.pgsql;

import java.sql.*;

public class DataBaseConn {
	
public Connection getConn() throws SQLException {
		
		
		try{ Class.forName("org.postgresql.Driver"); }
		catch(ClassNotFoundException e){e.printStackTrace();}
		
		return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/pacsdb", "postgres", "pacsdb");
		
	}

}
