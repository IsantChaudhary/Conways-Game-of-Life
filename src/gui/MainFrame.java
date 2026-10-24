
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final GamePanel gamePanel = new GamePanel();

    private static final String defaultFileName = "gameoflife.gol";

    public MainFrame() {

        super("Conway's Game of Life");

        setLayout(new BorderLayout());

        // Instructions displayed above the game grid
        JLabel instructions = new JLabel(
                "<html><center>"
                        + "<b>How to Play</b><br>"
                        + "Manual: Click cells to create a pattern, then press Start.<br>"
                        + "Random: Press Enter to generate a random pattern, then press Start.<br>"
                        + "Space: Next Step (advance one generation) &nbsp; | &nbsp; "
                        + "Backspace: Clear Grid"
                        + "</center></html>",
                JLabel.CENTER
        );

        instructions.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.add(instructions, BorderLayout.CENTER);

        // Visible Start and Pause buttons
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 8)
        );

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);

        // Instructions and buttons above the grid
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(instructionPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        startButton.addActionListener(e -> gamePanel.start());
        pauseButton.addActionListener(e -> gamePanel.pause());

        // File menu
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");

        Menu fileMenu = new Menu("File");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        MenuBar menuBar = new MenuBar();
        menuBar.add(fileMenu);

        setMenuBar(menuBar);

        // File chooser
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter =
                new FileNameExtensionFilter(
                        "Game of Life Files", "gol"
                );

        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        openItem.addActionListener(e -> {
            int userOption =
                    fileChooser.showOpenDialog(MainFrame.this);

            if (userOption == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                gamePanel.open(selectedFile);
            }
        });

        saveItem.addActionListener(e -> {
            fileChooser.setSelectedFile(new File(defaultFileName));

            int userOption =
                    fileChooser.showSaveDialog(MainFrame.this);

            if (userOption == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                gamePanel.save(selectedFile);
            }
        });

        // Keyboard controls
        InputMap inputMap = getRootPane().getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
                "nextGeneration"
        );

        actionMap.put("nextGeneration", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.next();
            }
        });

        inputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
                "clearGrid"
        );

        actionMap.put("clearGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.clear();
            }
        });

        inputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "randomizeGrid"
        );

        actionMap.put("randomizeGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.randomize();
            }
        });

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
