package org.airhockey.client;

import org.airhockey.server.AirHockeyServer;
import javax.swing.*;
import java.net.InetAddress;

public class AirHockeyClient {
    public static void main(String[] args) throws Exception {
        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();// свой айпи

            String ip = JOptionPane.showInputDialog(null, //диалог подключения
                    "Ваш IP адрес: " + localIP +
                            "\n\nВведите IP сервера:\n(оставьте пустым чтобы создать сервер)",
                    "Air Hockey - Подключение",
                    JOptionPane.QUESTION_MESSAGE);

            if (ip == null) return;//если отмена то выход

            if (ip.isBlank()) {//если создать сервер
                String serverIP = InetAddress.getLocalHost().getHostAddress();
                JOptionPane.showMessageDialog(null,
                        "Сервер создан!\n" +
                                "IP: " + serverIP + "\n" +
                                "Порт: 8888\n\n" +
                                "Сообщите этот IP другим игрокам",
                        "Сервер запущен",
                        JOptionPane.INFORMATION_MESSAGE);

                new Thread(() -> {//AirHockeyServer.start() блокирует выполнение бесконечными циклами.
                    // Без отдельного потока программа зависла бы.
                    try {
                        new AirHockeyServer(8888).start();//запуск сервера в отдельном окне
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();//заупск потока
                ip = "localhost";
            }

            NetworkClient net = new NetworkClient(ip, 8888);//подключение к серверу

            SwingUtilities.invokeLater(() -> {//Swing требует, чтобы все операции с интерфейсом выполнялись в специальном потоке (Event Dispatch Thread)
                new GameWindow(net).setVisible(true);//стздание и показ окна
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Ошибка: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}