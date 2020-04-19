package pgsql_bd;

import java.sql.*;

public class ConexionBD {
	
	public Connection conn() throws SQLException{
		
		try{ Class.forName("org.postgresql.Driver"); }
		catch(ClassNotFoundException e){}
		
		return DriverManager.getConnection("jdbc:postgresql://localhost:5432/pacsdb", "postgres", "pacsdb");
	}
}
