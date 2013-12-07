import java.awt.Point;
import java.util.*;

public class Plain {

	public static float countPolygonSurface(List<Point> points) {

		float surface = 0, surface_part1 = 0, surface_part2 = 0;

		int n = points.size();
		int j = 0;

		// area of irregular polygon formula
		for (int i = 0; i < n; i++) {
			j = (i + 1) % n;
			surface_part1 += points.get(i).x * points.get(j).y;
			surface_part2 += points.get(i).y * points.get(j).x;
		}
		surface = (surface_part1 - surface_part2) / 2;

		// absolute value from surface
		if (surface < 0) {
			surface = surface * -1;
		}
		return surface;
	}
}
