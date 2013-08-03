package su.litvak.xonix;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 02.08.13
 * Time: 23:28
 * To change this template use File | Settings | File Templates.
 */
public class Field {
    Tile[][] tiles;

    /**
     * @param width     width of earth part of the battlefield
     * @param height    height of earth part of the battlefield
     */
    public Field(int width, int height) {
        tiles = new Tile[width + 2][height + 2];

        width = tiles.length;
        height = tiles[0].length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = ((x == 0 || x == width - 1) && y >= 0) ||
                              ((y == 0 || y == height - 1) && x >= 0) ? Tile.WATER : Tile.EARTH;
            }
        }
    }

    @Override
    public String toString() {
        String result = "";

        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                result += tiles[x][y].symbol + " ";
            }
            result += "\r\n";
        }

        return result;
    }

    /**
     * Cuts off area, which is smaller of the two parts divided by specified border
     *
     * @param border    border separating field into two areas, one of which will be cut off
     */
    public void cut(List<Point> border) {
        if (border.size() < 2) {
            throw new IllegalArgumentException();
        }

        Set<Point> borderSet = new HashSet<Point>(border);
        Set<Point> part1 = new HashSet<Point>();
        Set<Point> part2 = new HashSet<Point>();

        /**
         * Walk through border, find opposite points
         */
        for (Point point : border) {
            final int x = point.x;
            final int y = point.y;

            /**
             * Movement was vertical, check left and right parts,
             * check top and bottom parts in any other case
             */
            if (border.contains(new Point(x, y - 1)) ||
                border.contains(new Point(x, y + 1))) {
                fill(x - 1, y, borderSet, part1);
                fill(x + 1, y, borderSet, part2);
            } else {
                fill(x, y - 1, borderSet, part1);
                fill(x, y + 1, borderSet, part2);
            }

            /**
             * Try to find non-empty areas
             */
            if (!part1.isEmpty() && !part2.isEmpty()) {
                break;
            } else {
                part1.clear();
                part2.clear();
            }
        }

        Set<Point> toFill = part1.size() > part2.size() ? part2 : part1;
        toFill.addAll(border);

        for (Point p : toFill) {
            tiles[p.x][p.y] = Tile.WATER;
        }
    }

    /**
     * Recursively fills area below the border starting from specified point
     *
     * @param x         x coordinate of a potential point to fill
     * @param y         y coordinate of a potential point to fill
     * @param border    set of points representing shape border
     * @param filled    set of points already filled
     */
    private void fill(int x, int y, Set<Point> border, Set<Point> filled) {
        Point p = new Point(x, y);

        Tile t = x > 0 && x < tiles.length - 1 &&
                 y > 0 && y < tiles[0].length - 1 ? tiles[x][y] : null;

        if (t == Tile.EARTH && !filled.contains(p) && !border.contains(p)) {
            filled.add(p);
            fill(x - 1, y, border, filled);
            fill(x + 1, y, border, filled);
            fill(x, y - 1, border, filled);
            fill(x, y + 1, border, filled);
        }
    }
}
