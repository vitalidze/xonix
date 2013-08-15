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
    final static Dimension TILE_SIZE = new Dimension(25, 25);
    final static Insets TILE_INSETS = new Insets(1, 1, 1, 1);

    JPanel jpMain = new JPanel();
    Field field;
    JLabel[][] labels;

    private class HeroMoveAction extends AbstractAction {
        int dx;
        int dy;

        HeroMoveAction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            /**
             * TODO move this method to a new controller class (i.e. method, which moves hero). Do the same with cutting method in Field class.
             */
            field.moveHero(dx, dy);
            fireFieldChanged();
        }
    }

    public Frame() {
        setTitle("Xonix");
        setResizable(false);

        jpMain.setLayout(new GridBagLayout());
        jpMain.setBackground(Color.BLUE);
        getContentPane().add(jpMain);

        /**
         * Set up hero movement actions
         */
        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "heroMoveUp");
        jpMain.getActionMap().put("heroMoveUp", new HeroMoveAction(0, -1));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "heroMoveDown");
        jpMain.getActionMap().put("heroMoveDown", new HeroMoveAction(0, 1));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "heroMoveRight");
        jpMain.getActionMap().put("heroMoveRight", new HeroMoveAction(1, 0));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "heroMoveLeft");
        jpMain.getActionMap().put("heroMoveLeft", new HeroMoveAction(-1, 0));

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Initializes frame with specified field
     *
     * @param field
     */
    public void setField(Field field) {
        this.field = field;

        setTitle("Xonix " + (field.getCols() - 2) + "x" + (field.getRows() - 2));
        jpMain.removeAll();

        int w = field.getCols() * TILE_SIZE.width + field.getCols() * 2 + 25;
        int h = field.getRows() * TILE_SIZE.height + field.getRows() * 2 + 55;

        /**
         * Prepare tiles
         */
        labels = new JLabel[field.getCols()][field.getRows()];
        for (int i = 0; i < field.getCols(); i++) {
            for (int j = 0; j < field.getRows(); j++) {
                JLabel l = new JLabel();
                labels[i][j] = l;
                l.setPreferredSize(TILE_SIZE);
                l.setOpaque(true);
                jpMain.add(l, new GridBagConstraints(i, j, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, TILE_INSETS, 0, 0));
            }
        }

        fireFieldChanged();

        /**
         * Show frame in the middle
         */
        int sW = getToolkit().getScreenSize().width;
        int sH = getToolkit().getScreenSize().height;

        setBounds((sW - w) / 2, (sH - h) / 2, w, h);
    }

    /**
     * Updates UI when model changes
     *
     * TODO create events to update/repaint only changed part of the field
     */
    private void fireFieldChanged() {
        for (int i = 0; i < field.getCols(); i++) {
            for (int j = 0; j < field.getRows(); j++) {
                Tile t = field.tiles[i][j];
                JLabel l = labels[i][j];
                if (field.hero.x == i && field.hero.y == j) {
                    l.setBackground(field.hero.state.color);
                } else {
                    l.setBackground(t.state.color);
                }
            }
        }

        jpMain.repaint();
    }
}
