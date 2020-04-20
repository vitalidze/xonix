package su.litvak.xonix;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class Field {
    private TilePolygon earth;
    private List<Point> path;
    private Point hero;
    private List<FieldChangeListener> changeListeners;
    private List<Point> enemies;
    private final int width;
    private final int height;

    /**
     * Creates field without enemies
     *
     * @param width     width of earth part of the battlefield
     * @param height    height of earth part of the battlefield
     */
    public Field(int width, int height) {
        this(width, height, 0);
    }

    /**
     * @param width     width of earth part of the battlefield
     * @param height    height of earth part of the battlefield
     * @param numberOfEnemies   how many enemies to be added to the field
     */
    public Field(int width, int height, int numberOfEnemies) {
        this.width = width;
        this.height = height;

        earth = new TilePolygon();
        earth.addPoint(1, 1);
        earth.addPoint(width , 1);
        earth.addPoint(width, height);
        earth.addPoint(1, height);

        /**
         * Put hero to the upper left corner
         */
        hero = new Point(0, 0);

        /**
         * Initialize path
         */
        path = new ArrayList<>();

        /**
         * Initialize enemies
         */
        enemies = Collections.singletonList(new Point(1, height));
    }

    @Override
    public String toString() {
        String result = "";

        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getCols(); x++) {
                result += getTile(x, y).symbol + " ";
            }
            result += "\r\n";
        }

        return result;
    }

    /**
     * Cuts off area, which is smaller of the two parts divided by specified border
     */
    public void cut() {
        TilePolygon halfToCut = new TilePolygon();
        TilePolygon newEarth = new TilePolygon();
        path.forEach(p -> halfToCut.addPoint(p.x, p.y));

        earth.getPoints()
                .forEach(p -> {
                    int x = p.x;
                    int y = p.y;
                    TilePolygon testHalf = new TilePolygon(halfToCut);
                    testHalf.addPoint(x, y);
                    if (enemies.stream().noneMatch(e -> testHalf.contains(e.x, e.y))) {
                        halfToCut.addPoint(x, y);
                    } else {
                        newEarth.addPoint(x, y);
                    }
                });

        path.forEach(p -> {
            for (int[] d : new int[][] {{0, 1}, {1, 0}, {0, -1}, {-1, -1}}) {
                int newX = p.x + d[0];
                int newY = p.y + d[1];
                if (earth.contains(p.x, p.y) && !halfToCut.contains(p.x, p.y)) {
                    newEarth.addPoint(newX, newY);
                }
            }
        });

        earth = newEarth;
        for (int x = 0; x < getCols(); x++) {
            for (int y = 0; y < getRows(); y++) {
                if (halfToCut.contains(x, y)) {
                    fireChange(new Point(x, y));
                }
            }
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
//            fill(getTile(point.x - 1, point.y), filled, result);
//            fill(getTile(point.x + 1, point.y), filled, result);
//            fill(getTile(point.x, point.y - 1), filled, result);
//            fill(getTile(point.x, point.y + 1), filled, result);
        }
    }

    /**
     * @return  total number of columns
     */
    public int getCols() {
        return width + 2;
    }

    /**
     * @return  total number of rows
     */
    public int getRows() {
        return height + 2;
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
    public TileState getTile(int x, int y) {
        if (hero.x == x && hero.y == y) {
            return TileState.HERO;
        }

        if (enemies.stream().anyMatch(e -> e.x == x && e.y == y)) {
            return TileState.ENEMY;
        }

        if (earth.contains(x, y)) {
            if (path.stream().anyMatch(e -> e.x == x && e.y == y)) {
                return TileState.PATH;
            } else {
                return TileState.EARTH;
            }
        }

        return TileState.WATER;
    }

    /**
     * @return  current path of hero
     */
    public List<Point> getPath() {
        return path;
    }

    /**
     * Set up path of hero. Used in tests only
     *
     * @param path
     * @deprecated  to be used in tests only
     */
    public void setPath(List<Point> path) {
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
            TileState newState = getTile(newX, newY);

            hero.x = newX;
            hero.y = newY;

            if (newState == TileState.WATER && !path.isEmpty()) {
                cut();
            } else if (newState == TileState.EARTH) {
                path.add(new Point(oldX, oldY));
            }

            fireChange(new Point(oldX, oldY));
            fireChange(new Point(newX, newY));
        }
    }

    /**
     * @return tile of hero along with it's current position
     */
    public Point getHero() {
        return hero;
    }

    /**
     * Notify all field change listeners about some changes in specified tile
     *
     * @param tile
     */
    private void fireChange(Point tile) {
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

//    private void addEnemy() {
//        for (int y = getRows() - 1; y >= 0; y--) {
//            for (int x = 0; x < getCols(); x++) {
//                Tile tile = getTile(x, y);
//                if (tile.state == TileState.EARTH) {
//                    tile.state = TileState.ENEMY;
//                    enemies.add(tile);
//                    fireChange(tile);
//                    return;
//                }
//            }
//        }
//    }

    public List<Point> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }
}
