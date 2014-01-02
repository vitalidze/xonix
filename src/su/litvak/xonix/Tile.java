package su.litvak.xonix;

import java.awt.*;

public class Tile extends Point {
    TileState state;

    public Tile(int x, int y, TileState state) {
        super(x, y);
        this.state = state;
    }
}
