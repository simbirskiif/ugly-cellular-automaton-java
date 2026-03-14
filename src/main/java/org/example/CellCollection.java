package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CellCollection {
    Set<Cell> cells = new HashSet<Cell>();

    record Cell(int x, int y) {
    }


    public CellCollection() {
    }

    void nextStep() {
        Map<Cell, Integer> neighborCounts = new HashMap<>();
        for (Cell cell : cells) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    Cell neighbor = new Cell(cell.x() + dx, cell.y() + dy);
                    neighborCounts.put(neighbor, neighborCounts.getOrDefault(neighbor, 0) + 1);
                }
            }
        }
        Set<Cell> nextGeneration = new HashSet<>();
        for (Map.Entry<Cell, Integer> entry : neighborCounts.entrySet()) {
            Cell cell = entry.getKey();
            int count = entry.getValue();

            if (cells.contains(cell)) {
                if (count == 2 || count == 3) nextGeneration.add(cell);
            } else {
                if (count == 3) nextGeneration.add(cell);
            }
        }
        this.cells = nextGeneration;
    }


    Set<Cell> getCells() {
        return cells;
    }

    void addCell(Cell cell) {
        cells.add(cell);
    }

    void setCells(Set<Cell> cells) {
        this.cells = cells;
    }
    public boolean hasCell(int x, int y) {
        return cells.contains(new Cell(x, y));
    }

    public void removeCell(Cell cell) {
        cells.remove(cell);
    }

}
