package org.example;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CellCollection {
    HashSet<Cell> cells = new HashSet<Cell>();

    record Cell(int x, int y) implements Serializable {

    }

    public CellCollection() {
    }

    void nextStep() {
        Map<Cell, Integer> neighborCounts = new HashMap<>();
        for (Cell cell : cells) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0)
                        continue;
                    Cell neighbor = new Cell(cell.x() + x, cell.y() + y);
                    neighborCounts.put(neighbor, neighborCounts.getOrDefault(neighbor, 0) + 1);
                }
            }
        }
        HashSet<Cell> nextGeneration = new HashSet<>();
        for (Map.Entry<Cell, Integer> entry : neighborCounts.entrySet()) {
            Cell cell = entry.getKey();
            int count = entry.getValue();

            if (cells.contains(cell)) {
                if (count == 2 || count == 3)
                    nextGeneration.add(cell);
            } else {
                if (count == 3)
                    nextGeneration.add(cell);
            }
        }
        this.cells = nextGeneration;
    }

    HashSet<Cell> getCells() {
        return cells;
    }

    void addCell(Cell cell) {
        cells.add(cell);
    }

    void setCells(HashSet<Cell> cells) {
        this.cells = cells;
    }

    public boolean hasCell(int x, int y) {
        return cells.contains(new Cell(x, y));
    }

    public void removeCell(Cell cell) {
        cells.remove(cell);
    }

}
