package su.litvak.xonix;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class HeroController {
    private Field field;

    private class HeroMoveAction extends AbstractAction {
        int dx;
        int dy;

        HeroMoveAction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            field.moveHero(dx, dy);
        }
    }

    /**
     * Set up hero movement actions on specified panel.
     *
     * @param jpMain the panel to register keystrokes on
     */
    public void registerHeroMovements(JPanel jpMain) {
        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "heroMoveUp");
        jpMain.getActionMap().put("heroMoveUp", new HeroMoveAction(0, -1));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "heroMoveDown");
        jpMain.getActionMap().put("heroMoveDown", new HeroMoveAction(0, 1));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "heroMoveRight");
        jpMain.getActionMap().put("heroMoveRight", new HeroMoveAction(1, 0));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "heroMoveLeft");
        jpMain.getActionMap().put("heroMoveLeft", new HeroMoveAction(-1, 0));
    }

    public void setField(Field field) {
        this.field = field;
    }
}
