package su.litvak.xonix;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Frame extends JFrame {
    JPanel jpMain = new JPanel();

    ToolbarPanel jpToolbar;
    FieldPanel jpField;

    /**
     * Create a new instance of the main frame.
     */
    public Frame() {
        setTitle("Xonix");
        setResizable(false);

        jpMain = new JPanel();
        jpField = new FieldPanel();
        jpToolbar = new ToolbarPanel(jpMain);

        // Initialize main panel
        jpMain.setLayout(new BorderLayout());
        jpMain.add(jpToolbar, BorderLayout.PAGE_START);
        jpMain.add(jpField, BorderLayout.CENTER);

        // Put main panel to the content pane
        getContentPane().add(jpMain);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Initializes frame with specified field.
     *
     * @param field data model
     */
    public void setField(Field field) {
        setTitle("Xonix " + (field.getCols() - 2) + "x" + (field.getRows() - 2));

        jpField.setField(field);
        jpToolbar.setField(field);

        // Show frame in the middle
        int w = getPreferredSize().width;
        int h = getPreferredSize().height;

        int screenWidth = getToolkit().getScreenSize().width;
        int screenHeight = getToolkit().getScreenSize().height;

        setBounds((screenWidth - w) / 2, (screenHeight - h) / 2, w, h);
    }
}
