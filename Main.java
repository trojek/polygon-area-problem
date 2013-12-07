import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class Main {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		int numberOfPolygons = 0;
		
		// Variables needed to connect with database
		// String database = args[0];
		String database = "jdbc:mysql://localhost/convex?" + "user=root&password=tomaszek77";
		Connection conn = DriverManager.getConnection(database);
		Statement st = conn.createStatement();

		// Select number unique of z 
		ResultSet nop = st.executeQuery("SELECT count(DISTINCT z) FROM Ftable;");

		if (nop.next()){
			numberOfPolygons = nop.getInt(1);
		}
		
		float[] functionZ = new float[numberOfPolygons];
		
		// select unique z
		ResultSet z = st.executeQuery("SELECT z FROM Ftable group by z");
		
		int counter = 0;
		while (z.next()){
			functionZ[counter] = z.getFloat(1);
			counter++;
		}
		
		//SELECT x,y FROM Ftable WHERE CAST(z AS DECIMAL) = CAST(22.23 AS DECIMAL)
		// close db connection
		conn.close();
		
		


	}

}
