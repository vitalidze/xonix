package su.litvak.xonix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldPanel extends JPanel implements FieldChangeListener, FieldCutListener {
    static final Dimension TILE_SIZE = new Dimension(25, 25);
    static final Insets TILE_INSETS = new Insets(1, 1, 1, 1);

    Logger log = LoggerFactory.getLogger(FieldPanel.class);

    Field field;
    HeroController heroController;
    EnemyController enemyController;
    JLabel[][] labels;

    /**
     * Create new instance of the panel displaying battle field.
     */
    public FieldPanel() {
        this.heroController = new HeroController();
        this.enemyController = new EnemyController();

        setLayout(new GridBagLayout());
        setBackground(Color.BLUE);
    }

    void setField(Field field) {
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

        // Prepare tiles
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

    @Override
    public void fieldChanged(FieldChangeEvent e) {
        // Update UI when model changes
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
