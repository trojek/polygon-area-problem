/** 
 * @author Tomasz Rojek
 * @version 1.0
 *  */

import java.awt.geom.Point2D;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Main class - control program */
public class Main {

	/**
	 * @param args - data needed to connect with database
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {

		int numberOfPolygons = 0;
		int numberOfPointsPerMaxPolygon = 0;

		// Variables needed to connect with database
		String database = args[0];
		Connection conn = DriverManager.getConnection(database);
		Statement st = conn.createStatement();

		// Select number of unique z (number of polygons)
		ResultSet nop = st.executeQuery("SELECT count(DISTINCT z) FROM Ftable");
		if (nop.next()) {
			numberOfPolygons = nop.getInt(1);
		}

		//Select the highest number of appearances of z (maximum points for polygon) 
		ResultSet nopfmp = st.executeQuery("SELECT count(z) num FROM Ftable GROUP BY z ORDER BY num DESC LIMIT 1");
		if (nopfmp.next()) {
			numberOfPointsPerMaxPolygon = nopfmp.getInt(1);
		}

		// Arrays which keeps points for polygons (eg. point (x4,y4) is (SetOfPointsX[v][4],SetOfPointsY[v][4]) where v is polygon number
		float[][] SetOfPointsX = new float[numberOfPolygons][numberOfPointsPerMaxPolygon];
		float[][] SetOfPointsY = new float[numberOfPolygons][numberOfPointsPerMaxPolygon];

		// Array for uniqe z variable
		float[] functionZ = new float[numberOfPolygons];

		// Select unique z
		int counter = 0;
		ResultSet z = st.executeQuery("SELECT z FROM Ftable group by z");
		while (z.next()) {
			functionZ[counter] = z.getFloat(1);
			counter++;
		}

		int PolygonNumber = 0;

		// Save points from database into an SetOfPointsX/Y Arrays
		for (Float x : functionZ) {
			ResultSet listOfPoints = st.executeQuery("SELECT x,y FROM Ftable WHERE CAST(z AS DECIMAL(5,2)) = CAST("+ x + " AS DECIMAL(5,2))");
			int j = 0;
			while (listOfPoints.next()) {
				SetOfPointsX[PolygonNumber][j] = listOfPoints.getFloat(1);
				SetOfPointsY[PolygonNumber][j] = listOfPoints.getFloat(2);

				j++;
			}
			PolygonNumber++;
		}
		
		// close db connection
		conn.close();

		// Array which keeps surface od all polygons
		float[] surfaceOfPolygons = new float[numberOfPolygons];
		
		// Count polygons surface and add in into an array
		for (int i = 0; i < numberOfPolygons; i++) {
			// find the convex hull
			List<Point2D.Float> convexHull = GrahamScan.getConvexHull(
					SetOfPointsX[i], SetOfPointsY[i]);
			surfaceOfPolygons[i]= Plain.countPolygonSurface(convexHull);
		}
		
		//Sort array to get the largest surface
		Arrays.sort(surfaceOfPolygons);
		
		//Print the largest number from array with surfaces
		System.out.printf("%.5f", surfaceOfPolygons[surfaceOfPolygons.length-1]);

	}

}
