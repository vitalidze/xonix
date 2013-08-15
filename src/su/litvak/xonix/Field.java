package su.litvak.xonix;

import java.awt.Point;
import java.util.ArrayList;
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
    List<Tile> path;
    Tile hero;

    /**
     * @param width     width of earth part of the battlefield
     * @param height    height of earth part of the battlefield
     */
    public Field(int width, int height) {
        tiles = new Tile[width + 2][height + 2];

        width = tiles.length;
        height = tiles[0].length;

        /**
         * Fill earth and water
         */
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState tileState =
                        ((x == 0 || x == width - 1) && y >= 0) ||
                        ((y == 0 || y == height - 1) && x >= 0) ? TileState.WATER : TileState.EARTH;

                tiles[x][y] = new Tile(x, y, tileState);
            }
        }

        /**
         * Put hero to the upper left corner
         */
        hero = new Tile(0, 0, TileState.HERO);

        /**
         * Initialize path
         */
        path = new ArrayList<Tile>();
    }

    @Override
    public String toString() {
        String result = "";

        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getCols(); x++) {
                result += tiles[x][y].state.symbol + " ";
            }
            result += "\r\n";
        }

        return result;
    }

    /**
     * Cuts off area, which is smaller of the two parts divided by specified border
     */
    public void cut() {
        List<Tile> border = getPath();
        Set<Tile> borderSet = new HashSet<Tile>(border);
        Set<Tile> part1 = new HashSet<Tile>();
        Set<Tile> part2 = new HashSet<Tile>();

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
            if (borderSet.contains(getTile(x, y - 1)) ||
                borderSet.contains(getTile(x, y + 1))) {
                fill(getTile(x - 1, y), borderSet, part1);
                fill(getTile(x + 1, y), borderSet, part2);
            } else {
                fill(getTile(x, y - 1), borderSet, part1);
                fill(getTile(x, y + 1), borderSet, part2);
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

        Set<Tile> toFill = part1.size() > part2.size() ? part2 : part1;
        toFill.addAll(borderSet);

        for (Point p : toFill) {
            tiles[p.x][p.y].state = TileState.WATER;
        }

        getPath().clear();
    }

    /**
     * Recursively fills area below the border starting from specified point
     *
     * @param point     potential point to fill
     * @param border    set of points representing shape border
     * @param filled    set of points already filled
     */
    private void fill(Tile point, Set<Tile> border, Set<Tile> filled) {
        if (point != null && point.state == TileState.EARTH && !filled.contains(point) && !border.contains(point)) {
            filled.add(point);
            fill(getTile(point.x - 1, point.y), border, filled);
            fill(getTile(point.x + 1, point.y), border, filled);
            fill(getTile(point.x, point.y - 1), border, filled);
            fill(getTile(point.x, point.y + 1), border, filled);
        }
    }

    /**
     * @return  total number of columns
     */
    public int getCols() {
        return tiles.length;
    }

    /**
     * @return  total number of rows
     */
    public int getRows() {
        return tiles[0].length;
    }

    /**
     * TODO comments
     *
     * @param x
     * @param y
     * @return
     */
    public Tile getTile(int x, int y) {
        return x >= 0 && x < getCols() &&
               y >= 0 && y < getRows() ? tiles[x][y] : null;
    }

    /**
     * TODO comments
     *
     * @return
     */
    public List<Tile> getPath() {
        return path;
    }

    public void moveHero(int dx, int dy) {
        int newX = hero.x + dx;
        int newY = hero.y + dy;

        if (newX >= 0 && newY >= 0 &&
            newX < getCols() && newY < getRows()) {
            if (tiles[hero.x][hero.y].state == TileState.EARTH) {
                tiles[hero.x][hero.y].state = TileState.PATH;
                path.add(tiles[hero.x][hero.y]);
            }
            hero.x = newX;
            hero.y = newY;
            if (tiles[newX][newY].state == TileState.WATER && !path.isEmpty()) {
                cut();
            }
        }
    }
}
