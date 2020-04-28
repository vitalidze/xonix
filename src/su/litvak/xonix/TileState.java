package su.litvak.xonix;

import java.awt.*;

public enum TileState {
    EARTH('+', Color.YELLOW),
    WATER('O', Color.BLUE),
    DEEP_WATER('D', Color.BLUE.darker()),
    HERO('H', Color.GREEN),
    ENEMY('E', Color.RED),
    PATH('x', Color.BLACK);

    final char symbol;
    final Color color;

    TileState(char symbol, Color color) {
        this.symbol = symbol;
        this.color = color;
    }
}
