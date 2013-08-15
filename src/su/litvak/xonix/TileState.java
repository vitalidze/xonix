package su.litvak.xonix;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 16.08.13
 * Time: 0:26
 * To change this template use File | Settings | File Templates.
 */
public enum TileState {
    EARTH('+', Color.YELLOW),
    WATER('O', Color.BLUE),
    HERO('H', Color.GREEN),
    ENEMY('E', Color.RED),
    PATH('x', Color.BLACK);

    final char symbol;
    final Color color;

    private TileState(char symbol, Color color) {
        this.symbol = symbol;
        this.color = color;
    }
}
