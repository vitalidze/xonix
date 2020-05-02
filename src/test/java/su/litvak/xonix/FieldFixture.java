package su.litvak.xonix;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;

class FieldFixture {
    final int width;
    final int height;
    List<Point> heroPath = new ArrayList<>();
    Set<Point> expectedWater = new HashSet<>();
    Point hero;
    List<Point> enemies;

    FieldFixture(int width, int height) {
        this.width = width;
        this.height = height;

        hero = new Point(0, 0);
        enemies = Collections.singletonList(new Point(1, height - 2));
    }

    FieldFixture path(int... points) {
        heroPath.addAll(list(points));
        return this;
    }

    FieldFixture waterPoints(int... points) {
        expectedWater.addAll(set(points));
        return this;
    }

    FieldFixture waterRect(int x, int y, int w, int h) {
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                expectedWater.add(new Point(i, j));
            }
        }
        return this;
    }

    private List<Point> list(int... points) {
        List<Point> result = new ArrayList<>(points.length / 2);
        for (int i = 0; i < points.length; i += 2) {
            result.add(new Point(points[i], points[i + 1]));
        }
        return result;
    }

    private Set<Point> set(int... points) {
        Set<Point> result = new HashSet<>(points.length / 2);
        for (int i = 0; i < points.length; i += 2) {
            result.add(new Point(points[i], points[i + 1]));
        }
        return result;
    }

    void check() {
        Field actual = new Field(width, height);
        List<Tile> pathTiles = new ArrayList<>(heroPath.size());
        for (Point p : heroPath) {
            Tile tile = actual.getTile(p.x, p.y);
            tile.state = TileState.PATH;
            pathTiles.add(tile);
        }
        actual.setPath(pathTiles);
        actual.cut();

        Field expected = new Field(width, height);
        for (Point p : heroPath) {
            if (expected.getTile(p.x, p.y).state != TileState.ENEMY) {
                expected.getTile(p.x, p.y).state = TileState.WATER;
            }
        }
        for (Point p : expectedWater) {
            expected.getTile(p.x, p.y).state = TileState.WATER;
        }

        String message = "Field mismatch. Expected: \n\n" + expected + " \nBut was: \n\n" + actual;

        for (int x = 0; x < width + 2; x++) {
            for (int y = 0; y < height + 2; y++) {
                Assert.assertTrue(message,
                        expected.getTile(x, y).state == actual.getTile(x, y).state);
            }
        }
    }
}
