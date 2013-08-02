package su.litvak;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 02.08.13
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public enum Tile {
    EARTH('+'), WATER('O'), HERO('H'), ENEMY('E');

    final char symbol;

    private Tile(char symbol) {
        this.symbol = symbol;
    }
}
