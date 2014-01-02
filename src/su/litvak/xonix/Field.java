package su.litvak.xonix;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

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
        Set<Tile> usedPoints = new HashSet<Tile>(borderSet);
        List<Set<Tile>> areas = new ArrayList<Set<Tile>>();
        int biggestAreaIndex = -1;

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
            for (int[] dxdy : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1} }) {
                Set<Tile> area = new HashSet<Tile>();
                fill(getTile(x + dxdy[0], y + dxdy[1]), usedPoints, area);
                if (!area.isEmpty()) {
                    areas.add(area);
                    usedPoints.addAll(area);

                    if (biggestAreaIndex < 0 || areas.get(biggestAreaIndex).size() < area.size()) {
                        biggestAreaIndex = areas.size() - 1;
                    }
                }
            }
        }

        Set<Tile> toFill = new HashSet<Tile>(borderSet);
        if (!areas.isEmpty()) {
            areas.remove(biggestAreaIndex);
            for (Set<Tile> area : areas) {
                toFill.addAll(area);
            }
        }

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
     * @param filled    set of points already filled
     * @param result    resulting set
     */
    private void fill(Tile point, Set<Tile> filled, Set<Tile> result) {
        if (point != null && point.state == TileState.EARTH && !filled.contains(point) && !result.contains(point)) {
            result.add(point);
            fill(getTile(point.x - 1, point.y), filled, result);
            fill(getTile(point.x + 1, point.y), filled, result);
            fill(getTile(point.x, point.y - 1), filled, result);
            fill(getTile(point.x, point.y + 1), filled, result);
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
