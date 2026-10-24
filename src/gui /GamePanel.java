
package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import exceptions.MismatchedSizeException;
import model.World;

public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int CELLSIZE = 10;

    private static final Color backgroundColor = Color.BLACK;
    private static final Color foregroundColor = Color.GREEN;
    private static final Color gridColor = Color.GRAY;

    private int topBottomMargin;
    private int leftRightMargin;

    private World world;

    private final Timer timer;
    private boolean running = false;

    public GamePanel() {

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = (e.getY() - topBottomMargin) / CELLSIZE;
                int col = (e.getX() - leftRightMargin) / CELLSIZE;

                if (row < 0 || row >= world.getRows()
                        || col < 0 || col >= world.getColumns()) {
                    return;
                }

                boolean status = world.getCell(row, col);
                world.setCell(row, col, !status);

                repaint();
            }
        });

        // The simulation starts paused.
        timer = new Timer(500, e -> next());
    }

    public void start() {
        if (!running) {
            timer.start();
            running = true;
        }
    }

    public void pause() {
        timer.stop();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        leftRightMargin = ((width % CELLSIZE) + CELLSIZE) / 2;
        topBottomMargin = ((height % CELLSIZE) + CELLSIZE) / 2;

        int rows = (height - 2 * topBottomMargin) / CELLSIZE;
        int columns = (width - 2 * leftRightMargin) / CELLSIZE;

        if (world == null) {
            world = new World(rows, columns);
        } else if (world.getRows() != rows
                || world.getColumns() != columns) {
            world = new World(rows, columns);
        }

        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, width, height);

        drawGrid(g2, width, height);

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                boolean status = world.getCell(row, col);
                fillCell(g2, row, col, status);
            }
        }
    }

    private void fillCell(Graphics2D g2, int row, int col,
                          boolean status) {

        Color color = status ? foregroundColor : backgroundColor;
        g2.setColor(color);

        int x = leftRightMargin + (col * CELLSIZE);
        int y = topBottomMargin + (row * CELLSIZE);

        g2.fillRect(x + 1, y + 1, CELLSIZE - 2, CELLSIZE - 2);
    }

    private void drawGrid(Graphics2D g2, int width, int height) {
        g2.setColor(gridColor);

        for (int x = leftRightMargin;
             x <= width - leftRightMargin; x += CELLSIZE) {
            g2.drawLine(x, topBottomMargin,
                    x, height - topBottomMargin);
        }

        for (int y = topBottomMargin;
             y <= height - topBottomMargin; y += CELLSIZE) {
            g2.drawLine(leftRightMargin, y,
                    width - leftRightMargin, y);
        }
    }

    public void randomize() {
        world.randomize();
        repaint();
    }

    public void clear() {
        world.clear();
        repaint();
    }

    public void next() {
        world.next();
        repaint();
    }

    public void save(File selectedFile) {
        try {
            world.save(selectedFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot save selected file",
                    "An error occurred",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void open(File selectedFile) {
        try {
            world.load(selectedFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot load selected file",
                    "An error occurred",
                    JOptionPane.ERROR_MESSAGE);
        } catch (MismatchedSizeException e) {
            JOptionPane.showMessageDialog(this,
                    "Loading grid size from a larger or smaller grid",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        repaint();
    }
}
