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
    private Tile[][] tiles;
    private List<Tile> path;
    private Tile hero;
    private List<FieldChangeListener> changeListeners;

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

        for (Tile tile : toFill) {
            tile.state = TileState.WATER;
            fireChange(tile);
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
     * <p>Looks for existing tile in a field. Will return null if coordinates are invalid.</p>
     *
     * <p>Never throws IndexOutOfBoundsException.</p>
     *
     * @param x x-coordinate of desired tile
     * @param y y-coordinate of desired tile
     * @return  field tile, null if coordinates point out of field bounds
     */
    public Tile getTile(int x, int y) {
        return x >= 0 && x < getCols() &&
               y >= 0 && y < getRows() ? tiles[x][y] : null;
    }

    /**
     * @return  current path of hero
     */
    public List<Tile> getPath() {
        return path;
    }

    /**
     * Set up path of hero. Used in tests only
     *
     * @param path
     * @deprecated  to be used in tests only
     */
    public void setPath(List<Tile> path) {
        this.path = path;
    }

    /**
     * Moves hero according to specified differences in x/y coordinates
     *
     * @param dx
     * @param dy
     */
    public void moveHero(int dx, int dy) {
        int oldX = hero.x;
        int oldY = hero.y;
        int newX = oldX + dx;
        int newY = oldY + dy;

        if (newX >= 0 && newY >= 0 &&
            newX < getCols() && newY < getRows()) {
            if (tiles[oldX][oldY].state == TileState.EARTH) {
                tiles[oldX][oldY].state = TileState.PATH;
                path.add(tiles[oldX][oldY]);
            }
            hero.x = newX;
            hero.y = newY;

            fireChange(getTile(oldX, oldY));
            fireChange(getTile(newX, newY));

            if (tiles[newX][newY].state == TileState.WATER && !path.isEmpty()) {
                cut();
            }
        }
    }

    /**
     * @return tile of hero along with it's current position
     */
    public Tile getHero() {
        return hero;
    }

    /**
     * Notify all field change listeners about some changes in specified tile
     *
     * @param tile
     */
    private void fireChange(Tile tile) {
        fireChange(new FieldChangeEvent(tile.x, tile.y, 1, 1));
    }

    /**
     * Notify all field change listeners with specified field change event
     *
     * @param e
     */
    private void fireChange(FieldChangeEvent e) {
        if (changeListeners == null) {
            return;
        }

        for (int i = changeListeners.size() - 1; i >= 0; i--) {
            changeListeners.get(i).fieldChanged(e);
        }
    }

    /**
     * Register specified field change listener to listen events from this field object
     *
     * @param l
     */
    public void addChangeListener(FieldChangeListener l) {
        if (changeListeners == null) {
            changeListeners = new ArrayList<FieldChangeListener>();
        }

        changeListeners.add(l);
    }

    /**
     * Un-register specified field change listener
     *
     * @param l
     */
    public void removeChangeListener(FieldChangeListener l) {
        if (changeListeners == null) {
            return;
        }

        changeListeners.remove(l);
    }
}
