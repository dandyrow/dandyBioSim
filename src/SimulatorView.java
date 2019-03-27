import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical user interface to control the simulation.
 * Creates a welcome tab, settings tab and simulation tab.
 * The simulation tab displays a colored rectangle for each location
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes, Michael Kölling and Daniel Lowry
 * @version 1.0
 */
public class SimulatorView extends JFrame implements ActionListener
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    // A timer to show each step of the simulation when started.
    private Timer simTimer = new Timer(50, this);
    // A simulator object
    private Simulator sim;
    // A content pane for the GUI
    private Container contents = getContentPane();

    // Height and width of the Simulator
    private int height = 94;
    private int width = 147;

    // Variables for changing the parameters of the animal classes.
    private int rabbitBA;
    private double rabbitBP = 0.12;
    private int rabbitMLS = 4;

    private int foxBA;
    private double foxBP = 0.08;
    private int foxMLS = 2;
    private int foxFV = 9;

    private int lionBA = 20;
    private double lionBP = 0.08;
    private int lionMLS = 2;
    private int lionRFV = 9;
    private int lionFFV = 5;

    /**
     * Draw the GUI and create a view of the given width and height.
     */
    public SimulatorView()
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("dandyBioSim v1.0");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        sim = new Simulator(height, width);
        sim.setView(this);
        setColor(Rabbit.class, Color.orange);
        setColor(Fox.class, Color.blue);
        setColor(Lion.class, Color.green);

        menu();
        tabs();
        pack();
        setVisible(true);
        reset();

        //Stops program when close icon pressed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Draws the menubar in the JFrame window.
     */
    private void menu() {
        JMenuBar menuBar = new JMenuBar();
            JMenu file = new JMenu("File");
            JMenuItem start = new JMenuItem("Start");
            start.addActionListener( e -> simTimer.start() );
            file.add(start);
            JMenuItem stop = new JMenuItem("Stop");
            stop.addActionListener( e -> simTimer.stop());
            file.add(stop);
            JMenuItem step = new JMenuItem("Step");
            step.addActionListener( e -> sim.simulateOneStep() );
            file.add(step);
            JMenuItem reset = new JMenuItem("Reset");
            reset.addActionListener( e -> reset() );
            file.add(reset);
        menuBar.add(file);

        JMenu window = new JMenu("Window");
            JMenuItem restore = new JMenuItem("Restore", new ImageIcon("images/restore.png"));
            restore.addActionListener( e -> setExtendedState(NORMAL) );
            window.add(restore);
            JMenuItem minimise = new JMenuItem("Minimise", new ImageIcon("images/minimise.png"));
            minimise.addActionListener( e -> setState(ICONIFIED) );
            window.add(minimise);
            JMenuItem maximise = new JMenuItem("Maximise", new ImageIcon("images/maximise.png"));
            maximise.addActionListener( e -> setExtendedState(MAXIMIZED_BOTH) );
            window.add(maximise);
            window.addSeparator();
            JMenuItem close = new JMenuItem("Close", new ImageIcon("images/close.png"));
            close.addActionListener( e -> { dispose(); System.exit(0);});
            window.add(close);
        menuBar.add(window);

        JMenu help = new JMenu("Help");
            JMenuItem website = new JMenuItem("Developer Website", new ImageIcon("images/favicon.png"));
            website.addActionListener(e -> siteLink());
            help.add(website);
            JMenuItem git = new JMenuItem("Developer GitHub", new ImageIcon("images/git.png"));
            git.addActionListener(e -> gitLink());
            help.add(git);
            JMenuItem about = new JMenuItem("About");
            about.addActionListener(e -> new About());
            help.add(about);
        menuBar.add(help);

        setJMenuBar(menuBar);
    }

    /**
     * Draws the tabs in the content pane.
     */
    private void tabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Welcome", welcome());
        tabbedPane.addTab("Settings", settings());
        tabbedPane.addTab("Simulation", view());
        contents.add(tabbedPane);
    }

    /**
     * Fills the welcome tab.
     * @return The container of the welcome GUI.
     */
    private Component welcome() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));

        JLabel title = new JLabel("Welcome to dandyBioSim!", SwingConstants.CENTER);
        title.setFont(new Font("Sans-Serif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));
        container.add(title, BorderLayout.NORTH);

        JTextArea text = new JTextArea (
                "This program runs a simulation of a habitat shared between rabbits, foxes and lions."
                + "\n" + "\n" + "To start the simulation with default values, go to the simulation tab and press start. " +
                        "Otherwise, parameters of the simulation can be altered in the settings tab."
        );
        text.setEditable(false);
        text.setLineWrap(true);
        text.setBackground(getBackground());
        text.setFont(Font.getFont("Sans-Serif"));
        container.add(text, BorderLayout.CENTER);

        return container;
    }

    /**
     * Fills the settings tab and records the changes of the parameters of the animal classes.
     * @return The container of the settings GUI.
     */
    private Component settings() {
        JPanel container = new JPanel(new GridLayout(1,3));
        container.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JPanel rabbitCont = new JPanel(new GridLayout(12,1));
        rabbitCont.setBorder(BorderFactory.createTitledBorder("Rabbit"));
            JLabel rabbitBALabel = new JLabel("Breeding Age:");
            rabbitCont.add(rabbitBALabel);
            JSlider rabbitBASlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 5);
            sliderHelper(rabbitBASlider);
            rabbitBASlider.addChangeListener( e -> {rabbitBA = rabbitBASlider.getValue(); reset();});
        rabbitCont.add(rabbitBASlider);
            JLabel rabbitBPLabel = new JLabel("Breeding Probability:");
            rabbitCont.add(rabbitBPLabel);
            JFormattedTextField rabbitBPField = new JFormattedTextField(NumberFormat.getNumberInstance());
            rabbitBPField.setValue(rabbitBP);
            rabbitBPField.setColumns(10);
            rabbitBPField.addPropertyChangeListener("value", e -> {rabbitBP = ((Number)rabbitBPField.getValue()).doubleValue(); reset();});
        rabbitCont.add(rabbitBPField);
            JLabel rabbitMLSLabel = new JLabel("Max Litter Size:");
            rabbitCont.add(rabbitMLSLabel);
            JFormattedTextField rabbitMLSField = new JFormattedTextField(NumberFormat.getNumberInstance());
            rabbitMLSField.setValue(rabbitMLS);
            rabbitMLSField.setColumns(10);
            rabbitMLSField.addPropertyChangeListener("value", e -> {rabbitMLS = ((Number)rabbitMLSField.getValue()).intValue(); reset();});
        rabbitCont.add(rabbitMLSField);
        container.add(rabbitCont);

        JPanel foxCont = new JPanel(new GridLayout(12,1));
        foxCont.setBorder(BorderFactory.createTitledBorder("Fox"));
            JLabel foxBALabel = new JLabel("Breeding Age:");
            foxCont.add(foxBALabel);
            JSlider foxBASlider = new JSlider(JSlider.HORIZONTAL, 0, 60,15);
            sliderHelper(foxBASlider);
            foxBASlider.addChangeListener(e -> {foxBA = foxBASlider.getValue(); reset();});
        foxCont.add(foxBASlider);
            JLabel foxBPLabel = new JLabel("Breeding Probability:");
            foxCont.add(foxBPLabel);
            JFormattedTextField foxBPField = new JFormattedTextField(NumberFormat.getNumberInstance());
            foxBPField.setValue(foxBP);
            foxBPField.setColumns(10);
            foxBPField.addPropertyChangeListener("value", e -> {foxBP = ((Number)foxBPField.getValue()).doubleValue(); reset();});
        foxCont.add(foxBPField);
            JLabel foxMLSLabel = new JLabel("Max Litter Size");
            foxCont.add(foxMLSLabel);
            JFormattedTextField foxMLSField = new JFormattedTextField(NumberFormat.getNumberInstance());
            foxMLSField.setValue(foxMLS);
            foxMLSField.setColumns(10);
            foxMLSField.addPropertyChangeListener("value", e -> {foxMLS = ((Number)foxMLSField.getValue()).intValue(); reset();});
        foxCont.add(foxMLSField);
            JLabel foxFVLabel = new JLabel("Food Value of Rabbits");
            foxCont.add(foxFVLabel);
            JFormattedTextField foxFVField = new JFormattedTextField(NumberFormat.getNumberInstance());
            foxFVField.setValue(foxFV);
            foxFVField.setColumns(10);
            foxFVField.addPropertyChangeListener("value", e -> {foxFV = ((Number)foxFVField.getValue()).intValue(); reset();});
        foxCont.add(foxFVField);
        container.add(foxCont);

        JPanel lionCont = new JPanel(new GridLayout(12,1));
        lionCont.setBorder(BorderFactory.createTitledBorder("Lion"));
            JLabel lionBALabel = new JLabel("Breeding Age:");
            lionCont.add(lionBALabel);
            JSlider lionBASlider = new JSlider(JSlider.HORIZONTAL, 0, 60,15);
            sliderHelper(lionBASlider);
            lionBASlider.addChangeListener(e -> {lionBA = lionBASlider.getValue(); reset();});
        lionCont.add(lionBASlider);
            JLabel lionBPLabel = new JLabel("Breeding Probability:");
            lionCont.add(lionBPLabel);
            JFormattedTextField lionBPField = new JFormattedTextField(NumberFormat.getNumberInstance());
            lionBPField.setValue(lionBP);
            lionBPField.setColumns(10);
            lionBPField.addPropertyChangeListener("value", e -> {lionBP = ((Number)lionBPField.getValue()).doubleValue(); reset();});
        lionCont.add(lionBPField);
            JLabel lionMLSLabel = new JLabel("Max Litter Size");
            lionCont.add(lionMLSLabel);
            JFormattedTextField lionMLSField = new JFormattedTextField(NumberFormat.getNumberInstance());
            lionMLSField.setValue(lionMLS);
            lionMLSField.setColumns(10);
            lionMLSField.addPropertyChangeListener("value", e -> {foxMLS = ((Number)lionMLSField.getValue()).intValue(); reset();});
        lionCont.add(lionMLSField);
            JLabel lionRFVLabel = new JLabel("Food Value of Rabbits");
            lionCont.add(lionRFVLabel);
            JFormattedTextField lionRFVField = new JFormattedTextField(NumberFormat.getNumberInstance());
            lionRFVField.setValue(lionRFV);
            lionRFVField.setColumns(10);
            lionRFVField.addPropertyChangeListener("value", e -> {lionRFV = ((Number)lionRFVField.getValue()).intValue(); reset();});
        lionCont.add(lionRFVField);
            JLabel lionFFVLabel = new JLabel("Food Value of Foxes");
            lionCont.add(lionFFVLabel);
            JFormattedTextField lionFFVField = new JFormattedTextField(NumberFormat.getNumberInstance());
            lionFFVField.setValue(lionFFV);
            lionFFVField.setColumns(10);
            lionFFVField.addPropertyChangeListener("value", e -> {lionFFV = ((Number)lionFFVField.getValue()).intValue(); reset();});
            lionCont.add(lionFFVField);
        container.add(lionCont);

        return container;
    }

    /**
     * Common settings for the sliders.
     * @param slider The slider which the settings are being changed.
     */
    private void sliderHelper(JSlider slider) {
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
    }

    /**
     * Draws the view of the simulation.
     * @return The view of the simulator.
     */
    private Component view() {
        JButton start = new JButton("Start");
        start.addActionListener( e -> simTimer.start() );
        JButton stop = new JButton("Stop");
        stop.addActionListener( e -> simTimer.stop());
        JButton step = new JButton("Step");
        step.addActionListener( e -> sim.simulateOneStep() );
        JButton reset = new JButton("Reset");
        reset.addActionListener( e -> reset() );
        JButton quit = new JButton("Quit");
        quit.addActionListener( e -> { dispose(); System.exit(0);} );

        JPanel controls = new JPanel();
        JPanel controls2 = new JPanel(new GridLayout(10,1));
        controls2.add(start);
        controls2.add(stop);
        controls2.add(step);
        controls2.add(reset);
        controls2.add(quit);
        controls.add(controls2);

        JPanel view = new JPanel(new BorderLayout());
        view.add(stepLabel, BorderLayout.NORTH);
        view.add(fieldView, BorderLayout.CENTER);
        view.add(population, BorderLayout.SOUTH);
        view.add(controls, BorderLayout.EAST);

        return view;
    }

    /**
     * Links to the developer's GitHub
     */
    private void gitLink() {
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/dandyrow").toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing webpage: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Links to the developer's website.
     */
    private void siteLink() {
        try {
            Desktop.getDesktop().browse(new URL("https://daniellowry.co.uk").toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing webpage: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color) { colors.put(animalClass, color); }

    /**
     * @return The color to be used for a given class of animal.
     */
    public Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null)
            // no color defined for this class
            return UNKNOWN_COLOR;
        else
            return col;
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible())
            setVisible(true);
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else
                    fieldView.drawMark(col, row, EMPTY_COLOR);
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field) { return stats.isViable(field); }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        sim.reset();

        // Show the starting state in the view.
        showStatus(sim.getStep(), sim.getField());
    }

    // Following methods get the parameters for the animal classes.
    public int getRabbitBA() { return rabbitBA; }
    public double getRabbitBP() { return rabbitBP; }
    public int getRabbitMLS() { return rabbitMLS; }
    public int getFoxBA() { return foxBA; }
    public double getFoxBP() { return foxBP; }
    public int getFoxMLS() { return foxMLS; }
    public int getFoxFV() { return foxFV; }
    public int getLionBA() { return lionBA; }
    public double getLionBP() { return lionBP; }
    public int getLionMLS() { return lionMLS; }
    public int getLionRFV() { return lionRFV; }
    public int getLionFFV() { return lionFFV; }

    /**
     * Calls simulateOneStep method of the simulator each time the timer ticks.
     * @param actionEvent The event that is being listened for.
     */
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == simTimer)
            sim.simulateOneStep();
    }

    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
