package su.litvak.xonix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Frame extends JFrame {
    JPanel jpMain = new JPanel();

    ToolbarPanel jpToolbar;
    FieldPanel jpField;

    public Frame() {
        setTitle("Xonix");
        setResizable(false);

        jpMain = new JPanel();
        jpField = new FieldPanel();
        jpToolbar = new ToolbarPanel(jpMain);

        /**
         * Initialize main panel
         */
        jpMain.setLayout(new BorderLayout());
        jpMain.add(jpToolbar, BorderLayout.PAGE_START);
        jpMain.add(jpField, BorderLayout.CENTER);

        /**
         * Put main panel to the content pane
         */
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

        jpField.setField(field);
        jpToolbar.setField(field);

        /**
         * Show frame in the middle
         */
        int w = getPreferredSize().width;
        int h = getPreferredSize().height;

        int sW = getToolkit().getScreenSize().width;
        int sH = getToolkit().getScreenSize().height;

        setBounds((sW - w) / 2, (sH - h) / 2, w, h);
    }
}
