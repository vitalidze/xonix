package su.litvak.xonix;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TilePolygon {
    private List<Point> points;
    private Rectangle bounds;

    public TilePolygon() {
        this.points = new ArrayList<>();
    }

    public TilePolygon(List<Point> points) {
        this.points = new ArrayList<>(points.size());
        points.stream().map(Point::new).forEach(this.points::add);
    }

    public TilePolygon(TilePolygon tilePolygon) {
        this(tilePolygon.points);
    }

    Rectangle calculateBounds() {
        int boundsMinX = Integer.MAX_VALUE;
        int boundsMinY = Integer.MAX_VALUE;
        int boundsMaxX = Integer.MIN_VALUE;
        int boundsMaxY = Integer.MIN_VALUE;

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            int x = point.x;
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            int y = point.y;
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }
        return new Rectangle(boundsMinX, boundsMinY,
                       boundsMaxX - boundsMinX,
                      boundsMaxY - boundsMinY);
    }

    Rectangle getBounds() {
        if (bounds == null) {
            bounds = calculateBounds();
        }
        return bounds;
    }

    void addPoint(int x, int y) {
        points.add(new Point(x, y));
        bounds = null;
    }

    boolean contains(int x, int y) {
        if (points.size() > 2) {
            Rectangle bounds = getBounds();
            if (x < bounds.x || x > bounds.x + bounds.width
                    || y < bounds.y || y > bounds.y + bounds.height) {
                return false;
            }
        }

        int hits = 0;
        List<Point> testPoints = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            Point nextPoint = i == points.size() - 1 ? points.get(0) : points.get(i + 1);
            testPoints.add(point);
            if (nextPoint.x != point.x && nextPoint.y != point.y) {
                // add fictive test point
                testPoints.add(new Point(Math.min(point.x, nextPoint.x), Math.max(point.y, nextPoint.y)));
            }
        }
        for (int i = 0; i < testPoints.size(); i++) {
            Point point = testPoints.get(i);
            Point nextPoint = i == testPoints.size() - 1 ? testPoints.get(0) : testPoints.get(i + 1);
            // check intersection with vertical line
            if (point.x == nextPoint.x && x <= point.x) {
                int minY = Math.min(point.y, nextPoint.y);
                int maxY = Math.max(point.y, nextPoint.y);

                if (y >= minY && y <= maxY) {
                    // resides on line
                    if (x == point.x) {
                        return true;
                    } else {
                        hits++;
                    }
                }
            } else if (point.y == nextPoint.y && y == point.y) {
                // check horisontal line
                int minX = Math.min(point.x, nextPoint.x);
                int maxX = Math.max(point.x, nextPoint.x);

                if (x >= minX && x <= maxX) {
                    return true;
                }
            }
        }

        return hits % 2 != 0;
    }

    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }
}
