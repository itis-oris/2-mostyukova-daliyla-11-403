package org.airhockey.server;

import org.airhockey.protocol.GameUpdate;
import org.airhockey.protocol.Message;
import org.airhockey.protocol.MessageType;
import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class AirHockeyServer {
    private final DatagramSocket socket;  // UDP сокет для сети
    private final GameSession session;    // Игровая логика (состояние)
    private final Gson gson = new Gson(); // Для работы с JSON

    public AirHockeyServer(int port) throws Exception {
        socket = new DatagramSocket(port);//открываем порт
        session = new GameSession();//новая игроваяя сессия
        System.out.println("Сервер запущен на порту " + port);
    }

    public void start() {
        Thread t = new Thread(this::listen);
        t.start();//отдельный поток потому что listen блокирует

        new Thread(() -> {
            long prev = System.nanoTime();
            while (true) {
                long now = System.nanoTime();
                float dt = (now - prev) / 1_000_000_000f;//время между кадрами
                prev = now;

                session.update(dt);

                GameUpdate u = session.toUpdate();//обновление состояния
                Message m = new Message(MessageType.UPDATE, "", gson.toJson(u));
                broadcast(m);//рассылка всем игрокам

                try {
                    Thread.sleep(16);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    private void listen() {
        try {
            while (true) {
                byte[] buf = new byte[2048];
                DatagramPacket p = new DatagramPacket(buf, buf.length);
                socket.receive(p);//от NetworkClient

                String json = new String(p.getData(), 0, p.getLength());
                Message msg = gson.fromJson(json, Message.class);

                // IP адрес и порт того, кто отправил сообщение
                InetSocketAddress addr = new InetSocketAddress(p.getAddress(), p.getPort());
                handle(msg, addr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(Message msg, InetSocketAddress addr) {
        switch (msg.type) {
            case CONNECT -> {
                String side = session.addPlayer(addr);
                System.out.println("CONNECT " + addr + " -> " + side);

                if ("FULL".equals(side)) {
                    send(addr, new Message(MessageType.ACCEPT, "FULL", ""));
                } else {
                    send(addr, new Message(MessageType.ACCEPT, side, ""));
                    if (session.getConnectedPlayers() == 2) {
                        System.out.println("Оба игрока подключены, игра начинается!");
                    }
                }
            }

            case MOVE -> {
                if (session.isGameStarted()) {  // Проверяем, началась ли игра
                    session.movePlayer(msg.sender, msg.payload);
                }
            }
        }
    }

    private void send(InetSocketAddress addr, Message msg) {
        try {
            // превращаем объект Message в JSON, затем в байты
            byte[] data = gson.toJson(msg).getBytes();
            // создаем UDP-пакет
            DatagramPacket p = new DatagramPacket(
                    data, data.length,
                    addr.getAddress(), addr.getPort());

            //отправляем пакет через сокет
            socket.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcast(Message msg) {
        // один раз превращаем сообщение в байты
        byte[] data = gson.toJson(msg).getBytes();

        //для каждого подключенного игрока
        for (InetSocketAddress addr : session.getPlayerAddresses()) {
            try {
                //создаем пакет с теми же данными, но разным адресатам
                DatagramPacket p = new DatagramPacket(
                        data, data.length,
                        addr.getAddress(), addr.getPort());

                //отправляем каждому
                socket.send(p);
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) throws Exception {
        AirHockeyServer server = new AirHockeyServer(8888);
        server.start();
    }
}