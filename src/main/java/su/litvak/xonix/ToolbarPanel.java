package su.litvak.xonix;

import java.awt.BorderLayout;
import java.net.URL;
import java.text.NumberFormat;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ToolbarPanel extends JPanel implements ScoreChangeListener {
    final JPanel jpMain;
    JButton btnGoBottomTop;
    JLabel lblScore;
    Field field;
    NumberFormat percentFormatter;

    ToolbarPanel(JPanel jpMain) {
        this.jpMain = jpMain;
        btnGoBottomTop = new JButton();
        lblScore = new JLabel();
        percentFormatter = NumberFormat.getPercentInstance();;

        setLayout(new BorderLayout());

        add(btnGoBottomTop, BorderLayout.LINE_END);
        add(lblScore, BorderLayout.LINE_START);

        btnGoBottomTop.addActionListener(e -> {
            boolean wasOnTop = isToolbarOnTop();
            jpMain.remove(this);
            jpMain.add(this, wasOnTop ? BorderLayout.PAGE_END : BorderLayout.PAGE_START);
            jpMain.revalidate();

            updateBottomTopButton();
        });
        updateBottomTopButton();
        updateScore(0);
    }

    /**
     * Used to find out the curent position of the toolbar.
     *
     * @return  true if toolbar is at the top of window, false if at the bottom
     */
    private boolean isToolbarOnTop() {
        if (getParent() == null) {
            return true;
        }
        BorderLayout mainLayout = (BorderLayout) jpMain.getLayout();
        return mainLayout.getConstraints(this).equals(BorderLayout.PAGE_START);
    }

    private void updateBottomTopButton() {
        btnGoBottomTop.setToolTipText("Move tool bar to the "
                + (isToolbarOnTop() ? "bottom" : "top"));
        btnGoBottomTop.setIcon(getImage("go-" + (isToolbarOnTop() ? "bottom" : "top") + ".png"));
    }

    private ImageIcon getImage(String fileName) {
        URL imageUrl = Thread.currentThread().getContextClassLoader()
                .getResource("images/" + fileName);
        return new ImageIcon(imageUrl);
    }

    void setField(Field field) {
        if (this.field != null) {
            updateScore(0);
            this.field.removeScoreChangeListener(this);
        }

        this.field = field;
        updateScore(0);
        field.addScoreChangeListener(this);
    }

    private int getTotalScore() {
        return field == null ? 0 : (field.getCols() - 2) * (field.getRows() - 2);
    }

    private void updateScore(int score) {
        double percent = getTotalScore() == 0 ? 0d : (double) score / getTotalScore();
        lblScore.setText(String.format("Score: %d / %d (%s)",
                score, getTotalScore(), percentFormatter.format(percent)));
    }

    @Override
    public void scoreChanged(ScoreChangeEvent e) {
        updateScore(e.newScore);
    }
}
