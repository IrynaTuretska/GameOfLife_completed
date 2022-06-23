package com.company;


// «Гра́ життя́» — клітинний автомат, вигаданий англійським математиком Джоном Конвеєм 1970 року.
/* Місце дії гри — «всесвіт» — являє собою площину, поділену на клітинки. Кожна клітинка може перебувати в одному з двох станів:
   бути живою або бути мертвою. Клітинка має вісім сусідів. Розподіл живих клітинок на початку гри називається першим поколінням.
   Кожне наступне покоління утворюється на основі попереднього за наведеними нижче правилами. */

/* Правила:
   * якщо в живої клітини два чи три живих сусіди – то вона лишається жити;
   * якщо в живої клітини один чи немає живих сусідів – то вона помирає від «самотності»;
   * якщо в живої клітини чотири та більше живих сусідів – вона помирає від «перенаселення»;
   * якщо в мертвої клітини рівно три живих сусіди – то вона оживає.
 */


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LifeMain extends JFrame {

    private static final long serialVersionUID = 3400265056061021539L;

    private LifePanel lifePanel = null;
    private JButton button1 = null;
    private JButton button2 = null;
    private JSlider slider = null;

    public LifeMain(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        lifePanel = new LifePanel();


        // розміри поля гри

        lifePanel.initialize(100, 70);
        add(lifePanel);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);

        button1 = new JButton("Запустити");
        toolBar.add(button1);
        button2 = new JButton("Очистити поле");
        toolBar.add(button2);

        // бігнуток для зміни швидкості руху клітин
        // зміна швидкусті симуляції клітинного автомату

        slider = new JSlider(1, 200);
        slider.setValue(50);
        lifePanel.setUpdateDelay(slider.getValue());
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lifePanel.setUpdateDelay(slider.getValue());
            }
        });

        toolBar.addSeparator();
        toolBar.add(new JLabel("Швидко"));
        toolBar.add(slider);
        toolBar.add(new JLabel("Повільно"));

        // запуск.зупинка симуляції; зміна надпису на кнопці (якось дуже дивно працює - переробити)

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lifePanel.isSimulating()) {
                    lifePanel.stopSimulation();
                    button1.setText("Запустити");
                } else {
                    lifePanel.startSimulation();
                    button1.setText("Зупинити");
                }
            }
        });


        // функція очитки поля гри

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (lifePanel.getLifeModel()) {
                    lifePanel.getLifeModel().clear();
                    lifePanel.repaint();
                }
            }
        });
        button1.setMaximumSize(new Dimension(100, 50));
        button2.setMaximumSize(new Dimension(100, 50));
        slider.setMaximumSize(new Dimension(300, 50));
        pack();
        setVisible(true);
    }

    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LifeMain("Game Of Life");
            }
        });
    }
}
