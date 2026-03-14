package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    static JFrame frame;
    static int gameSpeed = 1000;
    private static int cellSize = 30;
    static CellCollection cells = new CellCollection();
    static boolean ctrlPressed = false;

    static boolean isPlaying = false;

    static boolean isMouseRightPressed = false;

    public static void main(String[] args) {
        init();
    }

    static void init() {
        Point cameraPosition = new Point(0, 0);
        frame = new JFrame();
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(isPlaying ? Color.BLACK : Color.RED);
                if (cellSize > 5) {
                    for (int x = cameraPosition.x % cellSize; x <= getWidth(); x += cellSize) {
                        g.drawLine(x, 0, x, getHeight());
                    }

                    for (int y = cameraPosition.y % cellSize; y <= getHeight(); y += cellSize) {
                        g.drawLine(0, y, getWidth(), y);
                    }
                }
                g.setColor(Color.ORANGE);
                for (CellCollection.Cell cell : cells.getCells()) {
                    g.fillRect(cell.x() * cellSize + cameraPosition.x, cell.y() * cellSize + cameraPosition.y, cellSize, cellSize);
                }
            }
        };
        Point lastPoint = new Point();
        Timer uiTimer = new Timer(7, e -> {
            if (isMouseRightPressed) {
                Point mousePoint = MouseInfo.getPointerInfo().getLocation();

                int dx = mousePoint.x - lastPoint.x;
                int dy = mousePoint.y - lastPoint.y;

                cameraPosition.x += dx;
                cameraPosition.y += dy;
                lastPoint.setLocation(mousePoint);

            }
            panel.repaint();
        });

        Timer runTimer = new Timer(gameSpeed, e -> {
            if (isPlaying) {
                cells.nextStep();
            }
        });
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint.setLocation(MouseInfo.getPointerInfo().getLocation());
                if (e.getButton() == MouseEvent.BUTTON3) {
                    isMouseRightPressed = true;
                }
                if (e.getButton() == MouseEvent.BUTTON1 && !isPlaying) {
                    int dx = e.getX() - cameraPosition.x;
                    int dy = e.getY() - cameraPosition.y;

                    int cellX = Math.floorDiv(dx, cellSize);
                    int cellY = Math.floorDiv(dy, cellSize);

                    CellCollection.Cell target = new CellCollection.Cell(cellX, cellY);
                    if (cells.hasCell(cellX, cellY)) {
                        cells.removeCell(target);
                    } else {
                        cells.addCell(target);
                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    isMouseRightPressed = false;
                }
            }
        });
        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!ctrlPressed) {
                    gameSpeed += (gameSpeed > 500 ? 1 : 50) * e.getWheelRotation();
                    if (gameSpeed < 0) {
                        gameSpeed = 0;
                    }
                    runTimer.setDelay(gameSpeed);
                } else {
                    cellSize -= e.getWheelRotation();
                    if (cellSize < 2) {
                        cellSize = 1;
                    }
                }
            }
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    isPlaying = !isPlaying;
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlPressed = false;
                }
            }
        });
        panel.setFocusable(true);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Крутая игра");
        frame.setVisible(true);

        uiTimer.start();
        runTimer.start();

    }
}