package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class LifePanel extends JPanel implements Runnable {

    private static final long serialVersionUID = -7705111475296001684L;

    private Thread simThread = null;
    private LifeModel life = null;

    // Затримка між криками симуляції
    private int updateDelay = 100;

    // Розмір клітинки на екрані (8 виглядає як оптимальний, 10 теж ок
    private int cellSize = 8;

    // Проміжок між клітинками
    private int cellGap = 1;

    // Колір "мертвої" клітинки
    private static final Color c0 = new Color(0xCCCCCC);

    // Колір "живої" клітинки
    private static final Color c1 = new Color(0x38761D);

    public LifePanel() {
        setBackground(Color.BLACK);

        // редактор поля гри
        MouseAdapter mouse_adaptor = new MouseAdapter() {
            private boolean pressedLeft = false; // функція відповідає за натискання лівої кнопки миші
            private boolean pressedRight = false; // функція відповідає за натискання правої кнопки миші

            @Override
            public void mouseDragged(MouseEvent e) {
                setCell(e);
            }

            @Override
            public void mousePressed(MouseEvent mouse_event) {
                if (mouse_event.getButton() == MouseEvent.BUTTON1) {
                    pressedLeft = true;
                    pressedRight = false;
                    setCell(mouse_event);
                } else if (mouse_event.getButton() == MouseEvent.BUTTON3) {
                    pressedLeft = false;
                    pressedRight = true;
                    setCell(mouse_event);
                }
            }

            @Override
            public void mouseReleased(MouseEvent status) {
                if (status.getButton() == MouseEvent.BUTTON1) {
                    pressedLeft = false;
                } else if (status.getButton() == MouseEvent.BUTTON3) {
                    pressedRight = false;
                }
            }

            //param status - встановити чи виділити клітинку
            private void setCell(MouseEvent status) {
                if (life != null) {
                    synchronized (life) {

                        // розраховуємо координати клітинки, на яку вказує курсор миші
                        int x = status.getX() / (cellSize + cellGap);
                        int y = status.getY() / (cellSize + cellGap);
                        if (x >= 0 && y >= 0 && x < life.getWidth() && y < life.getHeight()) {
                            if (pressedLeft == true) {
                                life.setCell(x, y, (byte) 1);
                                repaint();
                            }
                            if (pressedRight == true) {
                                life.setCell(x, y, (byte) 0);
                                repaint();
                            }
                        }
                    }
                }
            }
        };
        addMouseListener(mouse_adaptor);
        addMouseMotionListener(mouse_adaptor);
    }

    public LifeModel getLifeModel() {
        return life;
    }

    public void initialize(int width, int height) {
        life = new LifeModel(width, height);
    }

    public void setUpdateDelay(int updateDelay) {
        this.updateDelay = updateDelay;
    }

    // Запуск симуляції гри
    public void startSimulation() {
        if (simThread == null) {
            simThread = new Thread(this);
            simThread.start();
        }
    }

    // Зупинка симуляції гри
    public void stopSimulation() {
        simThread = null;
    }

    public boolean isSimulating() {
        return simThread != null;
    }

    @Override
    public void run() {
        repaint();
        while (simThread != null) {
            try {
                Thread.sleep(updateDelay);
            } catch (InterruptedException e) {
            }
            // синхронізація - використовується для того, щом метод paintComponent не забігав на єкран та компонентів поля, що змінюється в даний момент
            synchronized (life) {
                life.simulate();
            }
            repaint();
        }
        repaint();
    }

    // Повертаємо розмір панелі з урахуванням розміру поля і клітинок
    @Override
    public Dimension getPreferredSize() {
        if (life != null) {
            Insets b = getInsets();
            return new Dimension((cellSize + cellGap) * life.getWidth() + cellGap + b.left + b.right,
                    (cellSize + cellGap) * life.getHeight() + cellGap + b.top + b.bottom);
        } else
            return new Dimension(100, 100);
    }

    // Графічний інтерфейс - відображення єкрану
    @Override
    protected void paintComponent(Graphics gui) {
        if (life != null) {
            synchronized (life) {
                super.paintComponent(gui);
                Insets b = getInsets();
                for (int y = 0; y < life.getHeight(); y++) {
                    for (int x = 0; x < life.getWidth(); x++) {
                        byte c = life.getCell(x, y);
                        gui.setColor(c == 1 ? c1 : c0);
                        gui.fillRect(b.left + cellGap + x * (cellSize + cellGap), b.top + cellGap + y
                                * (cellSize + cellGap), cellSize, cellSize);
                    }
                }
            }
        }
    }
}

