package su.litvak.xonix;

import javax.swing.*;
import java.awt.*;

import org.slf4j.*;

public class FieldPanel extends JPanel implements FieldChangeListener, FieldCutListener {
    final static Dimension TILE_SIZE = new Dimension(25, 25);
    final static Insets TILE_INSETS = new Insets(1, 1, 1, 1);

    Logger log = LoggerFactory.getLogger(FieldPanel.class);

    Field field;
    HeroController heroController;
    EnemyController enemyController;
    JLabel[][] labels;

    public FieldPanel() {
        this.heroController = new HeroController();
        this.enemyController = new EnemyController();

        setLayout(new GridBagLayout());
        setBackground(Color.BLUE);
    }

    public void setField(Field field) {
        if (this.field != null) {
            removeAll();
            this.field.removeChangeListener(this);
            this.field.removeCutListener(this);
        }

        heroController.setField(field);
        enemyController.setField(field);
        this.field = field;
        this.field.addChangeListener(this);
        this.field.addCutListener(this);

        /**
         * Prepare tiles
         */
        labels = new JLabel[field.getCols()][field.getRows()];
        for (int i = 0; i < field.getCols(); i++) {
            for (int j = 0; j < field.getRows(); j++) {
                JLabel l = new JLabel();
                labels[i][j] = l;
                l.setPreferredSize(TILE_SIZE);
                l.setMinimumSize(TILE_SIZE);
                l.setMaximumSize(TILE_SIZE);
                l.setOpaque(true);
                add(l, new GridBagConstraints(i, j, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, TILE_INSETS, 0, 0));
            }
        }

        fieldChanged(new FieldChangeEvent(0, 0, field.getCols(), field.getRows()));
        enemyController.startEnemies();
        heroController.registerHeroMovements(this);
    }

    /**
     * Updates UI when model changes
     *
     * @param e
     */
    @Override
    public void fieldChanged(FieldChangeEvent e) {
        for (int i = e.x; i < e.x + e.width; i++) {
            for (int j = e.y; j < e.y + e.height; j++) {
                Tile t = field.getTile(i, j);
                JLabel l = labels[i][j];
                l.setBackground(t.state.color);
            }
        }
    }

    @Override
    public void fieldCut(FieldCutEvent e) {
        revalidate();
        repaint();
    }
}
