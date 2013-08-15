package su.litvak.xonix;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 02.08.13
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class Tile extends Point {
    TileState state;

    public Tile(int x, int y, TileState state) {
        super(x, y);
        this.state = state;
    }
}
