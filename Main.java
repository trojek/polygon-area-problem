import java.awt.Point;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		int numberOfPolygons = 0;
		int numberOfPointsPerMaxPolygon = 0;
		
		// Variables needed to connect with database
		// String database = args[0];
		String database = "jdbc:mysql://localhost/convex?" + "user=root&password=tomaszek77";
		Connection conn = DriverManager.getConnection(database);
		Statement st = conn.createStatement();

		// Select number unique of z 
		ResultSet nop = st.executeQuery("SELECT count(DISTINCT z) FROM Ftable");

		if (nop.next()){
			numberOfPolygons = nop.getInt(1);
		}
		
		ResultSet nopfmp = st.executeQuery("SELECT count(z) num FROM Ftable GROUP BY z ORDER BY num DESC LIMIT 1");
		
		if (nopfmp.next()){
			numberOfPointsPerMaxPolygon = nopfmp.getInt(1);
		}
		
				
		float[][][] SetOfPoints = new float[numberOfPolygons][numberOfPointsPerMaxPolygon][2];
		float[] functionZ = new float[numberOfPolygons];
		
		// select unique z
		ResultSet z = st.executeQuery("SELECT z FROM Ftable group by z");

		int counter = 0;
		while (z.next()){
			functionZ[counter] = z.getFloat(1);
			counter++;
		}
		
		int i = 0;
		
		//Save 
		for(Float x: functionZ) {
			ResultSet listOfPoints = st.executeQuery("SELECT x,y FROM Ftable WHERE CAST(z AS DECIMAL) = CAST("+ x +"  AS DECIMAL)");
			int j = 0;
			while (listOfPoints.next()){
				SetOfPoints[i][j][0]=listOfPoints.getFloat(1);
				SetOfPoints[i][j][1]=listOfPoints.getFloat(2);

				j++;
			}
			i++;
		}
		// close db connection
		conn.close();
		
		// x coordinates
		int[] xs = {3, 5, -1, 8, -6, 23, 4};

		// y coordinates
		int[] ys = {9, 2, -4, 3, 90, 3, -11};

		// find the convex hull
		List<Point> convexHull = GrahamScan.getConvexHull(xs, ys);

		for(java.awt.Point p : convexHull) {
		    System.out.println(p);
		}


	}

}
