
package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import exceptions.MismatchedSizeException;

public class World {

    private int rows;
    private int columns;

    private boolean[][] grid;
    private boolean[][] buffer;

    public World(int rows, int columns) {

        this.rows = rows;
        this.columns = columns;

        grid = new boolean[rows][columns];
        buffer = new boolean[rows][columns];
    }

    public boolean getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, boolean status) {
        grid[row][col] = status;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void randomize() {

        clear();

        Random random = new Random();

        int numberOfCells = (rows * columns) / 10;

        for (int i = 0; i < numberOfCells; i++) {

            int row = random.nextInt(rows);
            int col = random.nextInt(columns);

            setCell(row, col, true);
        }
    }

    public void clear() {

        for (int row = 0; row < rows; row++) {
            Arrays.fill(grid[row], false);
            Arrays.fill(buffer[row], false);
        }
    }

    private int countNeighbours(int row, int col) {

        int neighbours = 0;

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {

                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }

                int gridRow = row + rowOffset;
                int gridCol = col + colOffset;

                if (gridRow < 0 || gridRow >= rows
                        || gridCol < 0 || gridCol >= columns) {
                    continue;
                }

                if (getCell(gridRow, gridCol)) {
                    neighbours++;
                }
            }
        }

        return neighbours;
    }

    public void next() {

        // Calculate every new cell using the current generation.
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                int neighbours = countNeighbours(row, col);
                boolean alive = getCell(row, col);

                if (neighbours < 2) {
                    // Underpopulation: the cell dies.
                    buffer[row][col] = false;

                } else if (neighbours > 3) {
                    // Overpopulation: the cell dies.
                    buffer[row][col] = false;

                } else if (neighbours == 3) {
                    // Exactly three neighbors: the cell is alive.
                    buffer[row][col] = true;

                } else {
                    // Exactly two neighbors: the cell keeps its state.
                    buffer[row][col] = alive;
                }
            }
        }

        // Update the entire grid only after all cells are calculated.
        for (int row = 0; row < rows; row++) {
            System.arraycopy(buffer[row], 0, grid[row], 0, columns);
        }
    }

    public void save(File selectedFile) throws IOException {

        try (DataOutputStream dos =
                     new DataOutputStream(
                             new FileOutputStream(selectedFile))) {

            dos.writeInt(rows);
            dos.writeInt(columns);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    dos.writeBoolean(grid[row][col]);
                }
            }
        }
    }

    public void load(File selectedFile)
            throws IOException, MismatchedSizeException {

        try (DataInputStream dis =
                     new DataInputStream(
                             new FileInputStream(selectedFile))) {

            int fileRows = dis.readInt();
            int fileCols = dis.readInt();

            if (fileRows <= 0 || fileCols <= 0) {
                throw new IOException("Invalid saved grid dimensions.");
            }

            // Clear old cells so they cannot remain alive after loading.
            clear();

            for (int row = 0; row < fileRows; row++) {
                for (int col = 0; col < fileCols; col++) {

                    boolean status = dis.readBoolean();

                    if (row < rows && col < columns) {
                        grid[row][col] = status;
                    }
                }
            }

            if (fileRows != rows || fileCols != columns) {
                throw new MismatchedSizeException();
            }
        }
    }
}
