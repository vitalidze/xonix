package su.litvak.xonix;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 02.08.13
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public enum Tile {
    EARTH('+', Color.YELLOW),
    WATER('O', Color.BLUE),
    HERO('H', Color.GREEN),
    ENEMY('E', Color.RED),
    PATH('x', Color.BLACK);

    final char symbol;
    final Color color;

    private Tile(char symbol, Color color) {
        this.symbol = symbol;
        this.color = color;
    }
}
