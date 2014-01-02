package su.litvak.xonix;

import java.awt.*;

public class FieldChangeEvent extends Rectangle {
    private Field field;

    public FieldChangeEvent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
