package org.airhockey.server;

import org.airhockey.model.Paddle;
import org.airhockey.model.Puck;
import org.airhockey.protocol.GameUpdate;
import org.airhockey.storage.JsonStorage;

import java.net.InetSocketAddress;
import java.util.*;

public class GameSession {
    private final int width = 800;
    private final int height = 600;

    private final Paddle paddleLeft;
    private final Paddle paddleRight;
    private final Puck puck;

    private int scoreLeft = 0;
    private int scoreRight = 0;

    private final Map<String, InetSocketAddress> players = new HashMap<>();
    private boolean gameStarted = false;
    private long gameStartTime = 0;

    public GameSession() {
        paddleLeft = new Paddle(30, height / 2f);
        paddleRight = new Paddle(width - 30, height / 2f);
        puck = new Puck(width / 2f, height / 2f);
    }

    public String addPlayer(InetSocketAddress addr) {//IP + порт
        if (!players.containsKey("LEFT")) {
            players.put("LEFT", addr);
            System.out.println("Левый игрок подключился. Ждем второго...");
            checkIfGameCanStart();
            return "LEFT";
        }
        if (!players.containsKey("RIGHT")) {
            players.put("RIGHT", addr);
            System.out.println("Правый игрок подключился!");
            checkIfGameCanStart();
            return "RIGHT";
        }
        return "FULL";
    }

    public Map<String, InetSocketAddress> getPlayers() {
        return players;
    }

    public Collection<InetSocketAddress> getPlayerAddresses() {
        return players.values();
    }

    public int getConnectedPlayers() {
        return players.size();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    private void checkIfGameCanStart() {
        if (players.size() == 2 && !gameStarted) {//если количество игроков =2 и игра не начата то
            gameStarted = true;//делаем переменную тру
            gameStartTime = System.currentTimeMillis();// засекаем начало игры
            scoreLeft = 0;//значения счета по умолчанию
            scoreRight = 0;
            resetPuck();//сброс шайбы в центр
            System.out.println("Игра началась! Оба игрока подключены.");
        }
    }


    public void movePlayer(String player, String direction) {//значения берутся из GameWindow, а там берется из NetworkClient
        if (!gameStarted) return;//если игра не началась, то выходим, не двигаем

        float speed = 8f;//8 пикселей за 1 вызов метода

        if (player.equals("LEFT")) {//левая
            if (direction.equals("UP")) paddleLeft.y -= speed;//из за обратной системы координат
            if (direction.equals("DOWN")) paddleLeft.y += speed;
            paddleLeft.clamp(0, height);//границы за которые нельзя переходить
        }

        if (player.equals("RIGHT")) {//правая
            if (direction.equals("UP")) paddleRight.y -= speed;
            if (direction.equals("DOWN")) paddleRight.y += speed;
            paddleRight.clamp(0, height);
        }
    }


    public void update(float dt) {
        if (!gameStarted) return;

        puck.update(dt);

        if (puck.y < 0 || puck.y > height) {// если координаты шайбы за верхней границей или за нижней то
            puck.vy = -puck.vy;//меняем направление на противоположное
        }

        if (puck.x < 0) {
            scoreRight++;
            checkWin();
            resetPuck();
        }

        if (puck.x > width) {
            scoreLeft++;
            checkWin();//проверка выиграл ли кто-то
            resetPuck();//сброс шайбы
        }

        checkPaddleCollision();//если ударилась об ракетку то меняем направление
    }


    private void checkWin() {
        if (scoreLeft >= 7 || scoreRight >= 7) {//если у кого-то 7 очков то печатаем победителя
            String winner = (scoreLeft > scoreRight) ? "LEFT" : "RIGHT";
            System.out.println("Матч окончен! Победитель: " + winner);

            JsonStorage.saveMatch(winner, scoreLeft, scoreRight);//сохраняем матч в файл(победитель и счет каждого)

            scoreLeft = 0;
            scoreRight = 0;

            resetPuck();//сброс шайбы
        }
    }

    private void resetPuck() {
        // ставим шайбу в центр поля
        puck.x = width / 2f;
        puck.y = height / 2f;

        //маф рандом - от 0 до 1, если больше 0.5, то вправо, если меньше, то влево
        puck.vx = (Math.random() > 0.5 ? 1 : -1) * 150;
        // Результат: +150 или -150 пикселей/секунду

        //от -0.5 до +0.5, от -100 до +100 пикселей/секунду
        puck.vy = (float) ((Math.random() - 0.5) * 200);
        // Приведение к float нужно, потому что Math.random() возвращает double
    }


    private void checkPaddleCollision() {
        if (Math.abs(puck.x - paddleLeft.x) < Paddle.WIDTH && //если коорд Ш - коорд Р по x < чем половина ракетки
                Math.abs(puck.y - paddleLeft.y) < Paddle.HEIGHT / 2) {// и если коорд Ш - коорд Р по y < чем пол ракетки
            puck.vx = Math.abs(puck.vx);//то направление - вправо
        }

        if (Math.abs(puck.x - paddleRight.x) < Paddle.WIDTH&&
                Math.abs(puck.y - paddleRight.y) < Paddle.HEIGHT / 2) {
            puck.vx = -Math.abs(puck.vx);
        }
    }

    public GameUpdate toUpdate() {
        return new GameUpdate(
                puck.x, puck.y,
                paddleLeft.x, paddleLeft.y,
                paddleRight.x, paddleRight.y,
                scoreLeft, scoreRight,
                gameStarted,
                players.size()
        );
    }


}