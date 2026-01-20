package org.airhockey.client;

import com.google.gson.Gson;
import org.airhockey.protocol.*;

import javax.swing.*;
import java.net.*;

public class NetworkClient {
    private final Gson gson = new Gson();
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private volatile GameUpdate lastUpdate = null;
    private String playerSide = "";

    public NetworkClient(String ip, int port) throws Exception {
        this.serverPort = port;
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(ip);

        sendConnect();
        new Thread(this::listen).start();//параллельно принимает сетевые сообщения
    }

    private void sendConnect() {
        Message msg = new Message(MessageType.CONNECT, "", "");//пока неизвестны отправитель и доп данные
        send(msg);// Отправляем на сервер
    }

    public void sendMove(String direction) {
        if (playerSide.isEmpty()) return;//не двигаемся пока сторона не назначена
        Message msg = new Message(MessageType.MOVE, playerSide, direction);
        send(msg);// Отправляем UDP пакет на сервер
    }

    private void send(Message msg) {
        try {
            byte[] data = gson.toJson(msg).getBytes();//Object → JSON → Bytes
            DatagramPacket packet = new DatagramPacket(
                    data, data.length, serverAddress, serverPort);//создание udp пакета для отправки байты(data), количество байтов, куда, на какой порт
            socket.send(packet);// Кидаем пакет в сеть
        } catch (Exception e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }

    private void listen() {
        try {
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);//готовим пакет для получения данных
                socket.receive(packet);//ждет пока пакет не придет а потом кладет его в packet

                String json = new String(packet.getData(), 0, packet.getLength());//0-начать сначала, взять только packet.getLength()
                Message msg = gson.fromJson(json, Message.class);

                switch (msg.type) {
                    case ACCEPT -> {
                        if ("FULL".equals(msg.sender)) {//если есть уже 2 игрока
                            JOptionPane.showMessageDialog(null,
                                    "Сервер заполнен! Максимум 2 игрока.");
                            System.exit(0);//полностью завершаем программу
                        }
                        playerSide = msg.sender;
                        System.out.println("Вы играете за: " + playerSide);
                    }
                    case UPDATE -> {
                        lastUpdate = gson.fromJson(msg.payload, GameUpdate.class);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка соединения: " + e.getMessage());
        }
    }

    public GameUpdate getLastUpdate() {
        return lastUpdate;
    }

    public String getPlayerSide() {
        return playerSide;
    }
}