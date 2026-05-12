package org.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import static java.awt.event.InputEvent.CTRL_MASK;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

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
                    g.fillRect(cell.x() * cellSize + cameraPosition.x, cell.y() * cellSize + cameraPosition.y, cellSize,
                            cellSize);
                }
            }
        };
        Action saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFileDialog(cells.getCells());
                System.out.println("save");
            }
        };
        Action loadAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFromFileDialog();
            }
        };
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL_MASK);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlS, "saveAction");
        panel.getActionMap().put("saveAction", saveAction);

        KeyStroke ctrlL = KeyStroke.getKeyStroke(KeyEvent.VK_L, CTRL_MASK);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlL, "loadAction");
        panel.getActionMap().put("loadAction", loadAction);

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
                    gameSpeed += (gameSpeed > 500 ? 50 : 1) * e.getWheelRotation();
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
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setTitle("Крутая игра");
        frame.setVisible(true);

        uiTimer.start();
        runTimer.start();

    }

    public static void saveToFileDialog(HashSet<CellCollection.Cell> cells) {
        JFileChooser file = new JFileChooser();
        int user = file.showSaveDialog(null);
        if (user != JFileChooser.APPROVE_OPTION)
            return;
        try {
            ObjectOutputStream stream = new ObjectOutputStream(
                    new FileOutputStream(file.getSelectedFile().getAbsolutePath()));
            stream.writeObject(cells);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка сохранения файла: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void loadFromFileDialog() {
        JFileChooser file = new JFileChooser();
        int user = file.showOpenDialog(null);

        if (user != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (ObjectInputStream stream = new ObjectInputStream(
                new FileInputStream(file.getSelectedFile().getAbsolutePath()))) {
            cells.setCells((HashSet<CellCollection.Cell>) stream.readObject());

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки файла: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}