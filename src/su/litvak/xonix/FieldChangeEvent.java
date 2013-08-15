package su.litvak.xonix;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 16.08.13
 * Time: 1:04
 * To change this template use File | Settings | File Templates.
 */
public class FieldChangeEvent extends Rectangle {
    private Field field;

    public FieldChangeEvent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
