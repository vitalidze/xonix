package su.litvak.xonix;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Controller {
    private Field field;
    private ScheduledExecutorService enemiesMover = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> enemiesFuture;

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
     * Set up hero movement actions on specified panel
     *
     * @param jpMain
     */
    public void registerHeroMovements(JPanel jpMain) {
        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "heroMoveUp");
        jpMain.getActionMap().put("heroMoveUp", new HeroMoveAction(0, -1));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "heroMoveDown");
        jpMain.getActionMap().put("heroMoveDown", new HeroMoveAction(0, 1));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "heroMoveRight");
        jpMain.getActionMap().put("heroMoveRight", new HeroMoveAction(1, 0));

        jpMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "heroMoveLeft");
        jpMain.getActionMap().put("heroMoveLeft", new HeroMoveAction(-1, 0));
    }

    private class EnemyMover implements Runnable {
        List<Point> previousPositions;
        List<Point> enemies;

        EnemyMover(List<Point> enemies) {
            this.enemies = enemies;
            this.previousPositions = new ArrayList<>(enemies.size());
            for (int i = 0; i < enemies.size(); i++) {
                this.previousPositions.add(null);
            }
        }

        @Override
        public void run() {
            for (int i = 0; i < enemies.size(); i++) {
                Point prev = previousPositions.get(i);
                // TODO calculate direction of movement
                if (prev == null) {

                }
            }
        }
    }

    public void startEnemies() {
        enemiesFuture = enemiesMover.scheduleAtFixedRate(new EnemyMover(field.getEnemies()), 1, 1, TimeUnit.SECONDS);
    }

    public void stopEnemies() {
        if (enemiesFuture != null) {
            enemiesFuture.cancel(true);
            enemiesFuture = null;
        }
    }

    public void setField(Field field) {
        this.field = field;
    }
}
