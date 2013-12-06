import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Main {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		// Variables needed to connect with database
		// String database = args[0];
		String database = "jdbc:mysql://localhost/convex?" + "user=root&password=tomaszek77";
		Connection conn = DriverManager.getConnection(database);
		Statement st = conn.createStatement();

		// close db connection
		conn.close();


	}

}
