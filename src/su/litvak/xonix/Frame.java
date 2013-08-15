package su.litvak.xonix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 10.08.13
 * Time: 0:09
 * To change this template use File | Settings | File Templates.
 */
public class Frame extends JFrame {
    FieldPanel jpMain = new FieldPanel();

    public Frame() {
        setTitle("Xonix");
        setResizable(false);

        jpMain.setLayout(new GridBagLayout());
        jpMain.setBackground(Color.BLUE);
        getContentPane().add(jpMain);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Initializes frame with specified field
     *
     * @param field
     */
    public void setField(Field field) {
        setTitle("Xonix " + (field.getCols() - 2) + "x" + (field.getRows() - 2));

        jpMain.setField(field);

        /**
         * Show frame in the middle
         */
        int w = field.getCols() * FieldPanel.TILE_SIZE.width + field.getCols() * 2 + 25;
        int h = field.getRows() * FieldPanel.TILE_SIZE.height + field.getRows() * 2 + 55;

        int sW = getToolkit().getScreenSize().width;
        int sH = getToolkit().getScreenSize().height;

        setBounds((sW - w) / 2, (sH - h) / 2, w, h);
    }
}
