import java.awt.geom.Point2D;
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
		String database = "jdbc:mysql://localhost/convex?"
				+ "user=root&password=tomaszek77";
		Connection conn = DriverManager.getConnection(database);
		Statement st = conn.createStatement();

		// Select number unique of z
		ResultSet nop = st.executeQuery("SELECT count(DISTINCT z) FROM Ftable");

		if (nop.next()) {
			numberOfPolygons = nop.getInt(1);
		}

		ResultSet nopfmp = st
				.executeQuery("SELECT count(z) num FROM Ftable GROUP BY z ORDER BY num DESC LIMIT 1");

		if (nopfmp.next()) {
			numberOfPointsPerMaxPolygon = nopfmp.getInt(1);
		}

		float[][] SetOfPointsX = new float[numberOfPolygons][numberOfPointsPerMaxPolygon];
		float[][] SetOfPointsY = new float[numberOfPolygons][numberOfPointsPerMaxPolygon];

		float[] functionZ = new float[numberOfPolygons];

		// select unique z
		ResultSet z = st.executeQuery("SELECT z FROM Ftable group by z");

		int counter = 0;
		while (z.next()) {
			functionZ[counter] = z.getFloat(1);
			counter++;
		}

		int numberOfPolygon = 0;

		// Save
		for (Float x : functionZ) {
			ResultSet listOfPoints = st
					.executeQuery("SELECT x,y FROM Ftable WHERE CAST(z AS DECIMAL(5,2)) = CAST("
							+ x + "  AS DECIMAL(5,2))");
			int j = 0;
			while (listOfPoints.next()) {
				SetOfPointsX[numberOfPolygon][j] = listOfPoints.getFloat(1);
				SetOfPointsY[numberOfPolygon][j] = listOfPoints.getFloat(2);

				j++;
			}
			numberOfPolygon++;
		}
		// close db connection
		conn.close();

		float[] surfaceOfPolygons = new float[numberOfPolygons];
		
		for (int i = 0; i < numberOfPolygons; i++) {
			// find the convex hull
			List<Point2D.Float> convexHull = GrahamScan.getConvexHull(
					SetOfPointsX[i], SetOfPointsY[i]);
			surfaceOfPolygons[i]= Plain.countPolygonSurface(convexHull);
		}

		Arrays.sort(surfaceOfPolygons);
		System.out.printf("%.5f", surfaceOfPolygons[numberOfPolygons-1]);

	}

}
