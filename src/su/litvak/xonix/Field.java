package su.litvak.xonix;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.*;

public class Field {
    private final Tile[][] tiles;
    private final int width;
    private final int height;

    private List<Tile> path;
    private Tile hero;
    private List<Tile> enemies;
    private List<FieldChangeListener> changeListeners;


    /**
     * @param earthWidth     width of earth part of the battlefield
     * @param earthHeight    height of earth part of the battlefield
     */
    public Field(int earthWidth, int earthHeight) {
        this.width = earthWidth + 2;
        this.height = earthHeight + 2;
        this.tiles = new Tile[width][height];

        // Fill earth and water
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState tileState =
                        ((x == 0 || x == width - 1)) ||
                        ((y == 0 || y == height - 1)) ? TileState.WATER : TileState.EARTH;

                tiles[x][y] = new Tile(x, y, tileState);
            }
        }

        // Put hero to the upper left corner
        hero = new Tile(0, 0, TileState.HERO);

        // Initialize path
        path = new ArrayList<>();

        // Set up enemies
        enemies = Collections.singletonList(new Tile(1, height - 2, TileState.ENEMY));
    }

    @Override
    public String toString() {
        String result = "";

        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getCols(); x++) {
                result += getTile(x, y).state.symbol + " ";
            }
            result += "\r\n";
        }

        return result;
    }

    /**
     * Cuts off area, which is smaller of the two parts divided by specified border
     */
    public void cut() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = getTileFromField(x, y);
                if (tile.state == TileState.EARTH) {
                    tile.state = TileState.DEEP_WATER;
                }
            }
        }

        enemies.forEach(this::floodFill);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = getTileFromField(x, y);
                if (tile.state == TileState.DEEP_WATER || tile.state == TileState.PATH) {
                    tile.state = TileState.WATER;
                    fireChange(tile);
                }
            }
        }

        getPath().clear();

        fireCut();
    }

    /**
     * Recursively fills area with earth surrounding the specified enemy location
     */
    private void floodFill(Tile enemy) {
        Queue<Tile> queue = new LinkedList<>();
        queue.add(getTileFromField(enemy));
        Tile next;
        while (!queue.isEmpty()) {
            next = queue.remove();
            if (next != null && next.state == TileState.DEEP_WATER) {
                next.state = TileState.EARTH;
                queue.add(getTileFromField(next.x - 1, next.y));
                queue.add(getTileFromField(next.x + 1, next.y));
                queue.add(getTileFromField(next.x, next.y + 1));
                queue.add(getTileFromField(next.x, next.y - 1));
            }
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

    private Tile getTileFromField(Point p) {
        return getTileFromField(p.x, p.y);
    }

    private Tile getTileFromField(int x, int y) {
        return x >= 0 && x < getCols() &&
               y >= 0 && y < getRows() ? tiles[x][y] : null;
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
        if (hero.x == x && hero.y == y) {
            return hero;
        } else {
            return enemies.stream().filter(e -> e.x == x && e.y == y)
                    .findFirst()
                    .orElseGet(() -> getTileFromField(x, y));
        }
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
     * Moves enemy according to specified dx,dy
     *
     * @param dx
     * @param dy
     */
    public void moveEnemy(Tile enemy, int dx, int dy) {
        int oldX = enemy.x;
        int oldY = enemy.y;
        int newX = oldX + dx;
        int newY = oldY + dy;

        if (Optional.ofNullable(getTileFromField(newX, newY))
                .map(t -> t.state)
                .orElse(TileState.WATER) == TileState.EARTH) {
            enemy.x = newX;
            enemy.y = newY;

            fireChange(getTile(oldX, oldY));
            fireChange(getTile(newX, newY));
        }
    }

    /**
     * @return tile of hero along with it's current position
     */
    public Tile getHero() {
        return hero;
    }

    /**
     * @return enemies tiles
     */
    public List<Tile> getEnemies() {
        return Collections.unmodifiableList(enemies);
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

    private void fireCut() {
        if (changeListeners == null) {
            return;
        }

        for (int i = changeListeners.size() - 1; i >= 0; i--) {
            changeListeners.get(i).fieldCut(new FieldCutEvent());
        }
    }

    /**
     * Register specified field change listener to listen events from this field object
     *
     * @param l
     */
    public void addChangeListener(FieldChangeListener l) {
        if (changeListeners == null) {
            changeListeners = new ArrayList<>();
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
