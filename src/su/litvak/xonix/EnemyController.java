package su.litvak.xonix;

import java.util.concurrent.*;

import javax.swing.*;

public class EnemyController {
    private Field field;
    ScheduledExecutorService threadPool;

    public void setField(Field field) {
        this.field = field;
    }

    private class EnemyMover implements Runnable {
        final Tile enemy;
        // try to use the movement from top left to the bottom right corner by default
        int dx = 1;
        int dy = -1;

        private EnemyMover(Tile enemy) {
            this.enemy = enemy;
        }

        @Override
        public void run() {
            if (field.canMoveEnemy(enemy, dx, dy)) {
                SwingUtilities.invokeLater(() ->  field.moveEnemy(enemy, dx, dy));
                // change direction
            } else {
                if (field.canMoveEnemy(enemy, dx, 0)) {
                    dy *= -1;
                } else if (field.canMoveEnemy(enemy, 0, dy)) {
                    dx *= -1;
                } else {
                    dx *= -1;
                    dy *= -1;
                }
                run();
            }
        }
    }

    public void startEnemies() {
        if (threadPool != null) {
            threadPool.shutdown();
        }
        threadPool = Executors.newSingleThreadScheduledExecutor();

        field.getEnemies().forEach(e -> threadPool.scheduleAtFixedRate(new EnemyMover(e), 500, 100, TimeUnit.MILLISECONDS));
    }
}
