package org.airhockey.client;

import javax.swing.*;
import java.awt.event.*;

public class GameWindow extends JFrame {
    public GameWindow(NetworkClient net) {
        setTitle("Air Hockey");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// Центрировать окно на экране

        GamePanel panel = new GamePanel(net);// Панель где рисуется игра
        add(panel); // Добавляем её в окно

        setFocusable(true);// Окно может получать фокус (для клавиш)
        requestFocusInWindow(); // Запрашиваем фокус сразу

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> net.sendMove("UP");
                    case KeyEvent.VK_DOWN -> net.sendMove("DOWN");
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Игра");
        JMenuItem historyItem = new JMenuItem("История матчей");
        JMenuItem exitItem = new JMenuItem("Выход");

        historyItem.addActionListener(e -> new HistoryWindow().setVisible(true));
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(historyItem);//история
        gameMenu.addSeparator();//разделительная линия
        gameMenu.add(exitItem);//выход
        menuBar.add(gameMenu);//добавляем меню в панель
        setJMenuBar(menuBar);//установка панель меню в окно
    }
}