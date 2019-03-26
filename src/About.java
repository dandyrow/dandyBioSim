import javax.swing.*;
import java.awt.*;

/**
 * A helper class for Simulator View. Draws a new JFrame which is used to display version info.
 *
 * @author Daniel Lowry
 * @version 1.0
 */
public class About extends JFrame{

    private JPanel container;

    /**
     * Creates container, calls methods to fill said container, adds it to the JFrame.
     */
    public About() {
        container = new JPanel(new BorderLayout(2, 1));
        container.setBorder(BorderFactory.createEmptyBorder(20,20,0,20));

        title();
        text();

        add(container);
        setVisible(true);
        setPreferredSize(new Dimension(500, 400));
        setTitle("About");
        pack();
    }

    /**
     * Adds about text to container.
     */
    private void text() {
        JTextArea text = new JTextArea ("Thank you for using my first Java application!" + "\n" + "\n" + "Version: 1.0" + "\n" + "\n" + "Author: Daniel Lowry" +
                "\n" + "\n" + "A link to the developer's website and GitHub can be found under the help menu in the menubar.");
        text.setEditable(false);
        text.setLineWrap(true);
        text.setBackground(getBackground());
        text.setFont(Font.getFont("Sans-Serif"));
        container.add(text, BorderLayout.CENTER);
    }

    /**
     * Adds title to the container.
     */
    private void title() {
        JLabel title = new JLabel("dandyBioSim", SwingConstants.CENTER);
        title.setFont(new Font("Sans-Serif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        container.add(title, BorderLayout.NORTH);
    }
}
