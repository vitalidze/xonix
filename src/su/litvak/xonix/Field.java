package su.litvak.xonix;

import java.awt.Point;
import java.util.*;

public class Field {
    private final Tile[][] tiles;
    private final int width;
    private final int height;

    private List<Tile> path;
    private Tile hero;
    private List<Tile> enemies;
    private List<FieldChangeListener> changeListeners;
    private List<FieldCutListener> cutListeners;
    private List<ScoreChangeListener> scoreChangeListeners;
    private int score;

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
        StringBuilder result = new StringBuilder("");

        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getCols(); x++) {
                result.append(getTile(x, y).state.symbol).append(" ");
            }
            result.append("\r\n");
        }

        return result.toString();
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
                    if (tile.state == TileState.DEEP_WATER) {
                        score++;
                    }
                    tile.state = TileState.WATER;
                    fireChange(tile);
                }
            }
        }

        getPath().clear();

        fireCut();
        fireScoreChanged();
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
            Tile oldTile = getTileFromField(oldX, oldY);
            if (oldTile.state == TileState.EARTH) {
                oldTile.state = TileState.PATH;
                path.add(oldTile);
            }
            Tile newTile = getTileFromField(newX, newY);
            if (newTile.state == TileState.EARTH) {
                score++;
                fireScoreChanged();
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
        if (changeListeners != null) {
            for (int i = changeListeners.size() - 1; i >= 0; i--) {
                changeListeners.get(i).fieldChanged(e);
            }
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
        Optional.ofNullable(changeListeners).ifPresent(lst -> lst.remove(l));
    }

    private void fireCut() {
        if (cutListeners != null) {
            for (int i = cutListeners.size() - 1; i >= 0; i--) {
                cutListeners.get(i).fieldCut(new FieldCutEvent());
            }
        }
    }

    public void addCutListener(FieldCutListener l) {
        if (cutListeners == null) {
            cutListeners = new ArrayList<>();
        }
        cutListeners.add(l);
    }

    public void removeCutListener(FieldCutListener l) {
        Optional.ofNullable(cutListeners).ifPresent(lst -> lst.remove(l));
    }

    private void fireScoreChanged() {
        if (scoreChangeListeners != null) {
            for (int i = scoreChangeListeners.size() - 1; i >= 0; i--) {
                scoreChangeListeners.get(i).scoreChanged(new ScoreChangeEvent(score));
            }
        }
    }

    public void addScoreChangeListener(ScoreChangeListener l) {
        if (scoreChangeListeners == null) {
            scoreChangeListeners = new ArrayList<>();
        }
        scoreChangeListeners.add(l);
    }

    public void removeScoreChangeListener(ScoreChangeListener l) {
        Optional.ofNullable(scoreChangeListeners).ifPresent(lst -> lst.remove(l));
    }
}
