package su.litvak.xonix;

import org.junit.Assert;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class FieldFixture {
    final int width;
    final int height;
    List<Point> heroPath = new ArrayList<>();
    Set<Point> expectedWater = new HashSet<>();
    Point hero;
    Point enemy;

    FieldFixture(int width, int height) {
        this.width = width;
        this.height = height;
        hero = new Point(0, 0 );
        enemy = new Point(1, height);
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
        List<Point> result = new ArrayList<Point>(points.length / 2);
        for (int i = 0; i < points.length; i += 2) {
            result.add(new Point(points[i], points[i + 1]));
        }
        return result;
    }

    private Set<Point> set(int... points) {
        Set<Point> result = new HashSet<Point>(points.length / 2);
        for (int i = 0; i < points.length; i += 2) {
            result.add(new Point(points[i], points[i + 1]));
        }
        return result;
    }

    TileState[][] getActualState(Field field) {
        TileState[][] actual = new TileState[field.getRows()][field.getCols()];
        for (int x = 0; x < field.getCols(); x++) {
            for (int y = 0; y < field.getRows(); y++) {
                actual[y][x] = field.getTile(x, y);
            }
        }
        return actual;
    }

    public void check() {
        Field field = new Field(width, height);
        System.out.println(toString(getActualState(field)));
        field.setPath(heroPath);
        System.out.println(toString(getActualState(field)));
        field.cut();
        System.out.println(toString(getActualState(field)));

        TileState[][] actual = getActualState(field);

        TileState[][] expected = new TileState[field.getRows()][field.getCols()];
        for (int x = 0; x < field.getCols(); x++) {
            for (int y = 0; y < field.getRows(); y++) {
                expected[y][x] = TileState.EARTH;
            }
        }

        for (Point p : heroPath) {
            expected[p.y][p.x] = TileState.WATER;
        }

        for (Point p : expectedWater) {
            expected[p.y][p.x] = TileState.WATER;
        }

        expected[hero.y][hero.x] = TileState.HERO;
        expected[enemy.y][enemy.x] = TileState.ENEMY;

        String message = "Field mismatch. Expected: \n\n" + toString(expected) + " \nBut was: \n\n" + toString(actual);

        for (int x = 0; x < field.getCols(); x++) {
            for (int y = 0; y < field.getRows(); y++) {
                Assert.assertTrue(message, expected[y][x] == actual[y][x]);
            }
        }
    }

    String toString(TileState[][] states) {
        StringBuilder result = new StringBuilder();

        for (int x = 0; x < states.length; x++) {
            for (int y = 0; y < states[0].length; y++) {
                result.append(states[x][y].symbol).append(" ");
            }
            result.append("\r\n");
        }

        return result.toString();
    }
}
