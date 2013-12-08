import java.awt.geom.Point2D;
import java.util.*;

/** Class Plain - have method to count area of polygon */
public class Plain {

	/**
	 * Method countPolygonSurface, gives surface od polygon created from points
	 * from parameter
	 * 
	 * @param List
	 *            <Point2D.Float> points - list of points which are after
	 *            connection gives a polygon
	 * @return surface of polygon from given points
	 * */
	public static float countPolygonSurface(List<Point2D.Float> points) {

		// surface_part1 and surface_part2 are parts of the surface of polygon
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
		if (surface < 0) { surface = surface * -1;}
		
		return surface;
	}
}

/*
 * Copyright (c) 2010, Bart Kiers
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

final class GrahamScan {

	/**
	 * An enum denoting a directional-turn between 3 points (vectors).
	 */
	protected static enum Turn {
		CLOCKWISE, COUNTER_CLOCKWISE, COLLINEAR
	}

	/**
	 * Returns true iff all points in <code>points</code> are collinear.
	 * 
	 * @param points
	 *            the list of points.
	 * @return true iff all points in <code>points</code> are collinear.
	 */
	protected static boolean areAllCollinear(List<Point2D.Float> points) {

		if (points.size() < 2) {
			return true;
		}

		final Point2D.Float a = points.get(0);
		final Point2D.Float b = points.get(1);

		for (int i = 2; i < points.size(); i++) {

			Point2D.Float c = points.get(i);

			if (getTurn(a, b, c) != Turn.COLLINEAR) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the convex hull of the points created from <code>xs</code> and
	 * <code>ys</code>. Note that the first and last point in the returned
	 * <code>List&lt;java.awt.Point&gt;</code> are the same point.
	 * 
	 * @param xs
	 *            the x coordinates.
	 * @param ys
	 *            the y coordinates.
	 * @return the convex hull of the points created from <code>xs</code> and
	 *         <code>ys</code>.
	 * @throws IllegalArgumentException
	 *             if <code>xs</code> and <code>ys</code> don't have the same
	 *             size, if all points are collinear or if there are less than 3
	 *             unique points present.
	 */
	public static List<Point2D.Float> getConvexHull(float[] xs, float[] ys)
			throws IllegalArgumentException {

		if (xs.length != ys.length) {
			throw new IllegalArgumentException(
					"xs and ys don't have the same size");
		}

		List<Point2D.Float> points = new ArrayList<Point2D.Float>();

		for (int i = 0; i < xs.length; i++) {
			points.add(new Point2D.Float(xs[i], ys[i]));
		}

		return getConvexHull(points);
	}

	/**
	 * Returns the convex hull of the points created from the list
	 * <code>points</code>. Note that the first and last point in the returned
	 * <code>List&lt;java.awt.Point&gt;</code> are the same point.
	 * 
	 * @param points
	 *            the list of points.
	 * @return the convex hull of the points created from the list
	 *         <code>points</code>.
	 * @throws IllegalArgumentException
	 *             if all points are collinear or if there are less than 3
	 *             unique points present.
	 */
	public static List<Point2D.Float> getConvexHull(List<Point2D.Float> points)
			throws IllegalArgumentException {

		List<Point2D.Float> sorted = new ArrayList<Point2D.Float>(
				getSortedPointSet(points));

		if (sorted.size() < 3) {
			throw new IllegalArgumentException(
					"can only create a convex hull of 3 or more unique points");
		}

		if (areAllCollinear(sorted)) {
			throw new IllegalArgumentException(
					"cannot create a convex hull from collinear points");
		}

		Stack<Point2D.Float> stack = new Stack<Point2D.Float>();
		stack.push(sorted.get(0));
		stack.push(sorted.get(1));

		for (int i = 2; i < sorted.size(); i++) {

			Point2D.Float head = sorted.get(i);
			Point2D.Float middle = stack.pop();
			Point2D.Float tail = stack.peek();

			Turn turn = getTurn(tail, middle, head);

			switch (turn) {
			case COUNTER_CLOCKWISE:
				stack.push(middle);
				stack.push(head);
				break;
			case CLOCKWISE:
				i--;
				break;
			case COLLINEAR:
				stack.push(head);
				break;
			}
		}

		// close the hull
		stack.push(sorted.get(0));

		return new ArrayList<Point2D.Float>(stack);
	}

	/**
	 * Returns the points with the lowest y coordinate. In case more than 1 such
	 * point exists, the one with the lowest x coordinate is returned.
	 * 
	 * @param points
	 *            the list of points to return the lowest point from.
	 * @return the points with the lowest y coordinate. In case more than 1 such
	 *         point exists, the one with the lowest x coordinate is returned.
	 */
	protected static Point2D.Float getLowestPoint(List<Point2D.Float> points) {

		Point2D.Float lowest = points.get(0);

		for (int i = 1; i < points.size(); i++) {

			Point2D.Float temp = points.get(i);

			if (temp.y < lowest.y || (temp.y == lowest.y && temp.x < lowest.x)) {
				lowest = temp;
			}
		}

		return lowest;
	}

	/**
	 * Returns a sorted set of points from the list <code>points</code>. The set
	 * of points are sorted in increasing order of the angle they and the lowest
	 * point <tt>P</tt> make with the x-axis. If tow (or more) points form the
	 * same angle towards <tt>P</tt>, the one closest to <tt>P</tt> comes first.
	 * 
	 * @param points
	 *            the list of points to sort.
	 * @return a sorted set of points from the list <code>points</code>.
	 * @see GrahamScan#getLowestPoint(java.util.List)
	 */
	protected static Set<Point2D.Float> getSortedPointSet(
			List<Point2D.Float> points) {

		final Point2D.Float lowest = getLowestPoint(points);

		TreeSet<Point2D.Float> set = new TreeSet<Point2D.Float>(
				new Comparator<Point2D.Float>() {
					@Override
					public int compare(Point2D.Float a, Point2D.Float b) {

						if (a == b || a.equals(b)) {
							return 0;
						}

						// use longs to guard against int-underflow
						double thetaA = Math.atan2((long) a.y - lowest.y,
								(long) a.x - lowest.x);
						double thetaB = Math.atan2((long) b.y - lowest.y,
								(long) b.x - lowest.x);

						if (thetaA < thetaB) {
							return -1;
						} else if (thetaA > thetaB) {
							return 1;
						} else {
							// collinear with the 'lowest' point, let the point
							// closest to it come first

							// use longs to guard against int-over/underflow
							double distanceA = Math
									.sqrt((((long) lowest.x - a.x) * ((long) lowest.x - a.x))
											+ (((long) lowest.y - a.y) * ((long) lowest.y - a.y)));
							double distanceB = Math
									.sqrt((((long) lowest.x - b.x) * ((long) lowest.x - b.x))
											+ (((long) lowest.y - b.y) * ((long) lowest.y - b.y)));

							if (distanceA < distanceB) {
								return -1;
							} else {
								return 1;
							}
						}
					}
				});

		set.addAll(points);

		return set;
	}

	/**
	 * Returns the GrahamScan#Turn formed by traversing through the ordered
	 * points <code>a</code>, <code>b</code> and <code>c</code>. More
	 * specifically, the cross product <tt>C</tt> between the 3 points (vectors)
	 * is calculated:
	 * 
	 * <tt>(b.x-a.x * c.y-a.y) - (b.y-a.y * c.x-a.x)</tt>
	 * 
	 * and if <tt>C</tt> is less than 0, the turn is CLOCKWISE, if <tt>C</tt> is
	 * more than 0, the turn is COUNTER_CLOCKWISE, else the three points are
	 * COLLINEAR.
	 * 
	 * @param a
	 *            the starting point.
	 * @param b
	 *            the second point.
	 * @param c
	 *            the end point.
	 * @return the GrahamScan#Turn formed by traversing through the ordered
	 *         points <code>a</code>, <code>b</code> and <code>c</code>.
	 */
	protected static Turn getTurn(Point2D.Float a, Point2D.Float b,
			Point2D.Float c) {

		// use longs to guard against int-over/underflow
		float crossProduct = (((long) b.x - a.x) * ((long) c.y - a.y))
				- (((long) b.y - a.y) * ((long) c.x - a.x));

		if (crossProduct > 0) {
			return Turn.COUNTER_CLOCKWISE;
		} else if (crossProduct < 0) {
			return Turn.CLOCKWISE;
		} else {
			return Turn.COLLINEAR;
		}
	}
}