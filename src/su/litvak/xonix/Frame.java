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

    FieldPanel jpField = new FieldPanel();

    JPanel jpToolbar = new JPanel();
    JButton btnGoBottomTop = new JButton();

    public Frame() {
        setTitle("Xonix");
        setResizable(false);

        /**
         * Initialize main panel
         */
        jpMain.setLayout(new BorderLayout());
        jpMain.add(jpToolbar, BorderLayout.PAGE_START);
        jpMain.add(jpField, BorderLayout.CENTER);

        /**
         * Initialize tool bar
         */
        jpToolbar.setLayout(new BorderLayout());
        jpToolbar.add(btnGoBottomTop, BorderLayout.LINE_END);
        btnGoBottomTop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wasOnTop = isToolbarOnTop();
                jpMain.remove(jpToolbar);
                jpMain.add(jpToolbar, wasOnTop ? BorderLayout.PAGE_END : BorderLayout.PAGE_START);
                jpMain.revalidate();

                updateBottomTopButton();
            }
        });
        updateBottomTopButton();

        /**
         * Put main panel to the content pane
         */
        getContentPane().add(jpMain);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * @return  true if toolbar is at the top of window, false if at the bottom
     */
    private boolean isToolbarOnTop() {
        return jpToolbar.getParent() == null ||
               ((BorderLayout) jpMain.getLayout()).getConstraints(jpToolbar).equals(BorderLayout.PAGE_START);
    }

    private void updateBottomTopButton() {
        btnGoBottomTop.setToolTipText("Move tool bar to the " + (isToolbarOnTop() ? "bottom" : "top"));
        btnGoBottomTop.setIcon(getImage("go-" + (isToolbarOnTop() ? "bottom" : "top") + ".png"));
    }

    private ImageIcon getImage(String fileName) {
        return new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/" + fileName));
    }

    /**
     * Initializes frame with specified field
     *
     * @param field
     */
    public void setField(Field field) {
        setTitle("Xonix " + (field.getCols() - 2) + "x" + (field.getRows() - 2));

        jpField.setField(field);

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
