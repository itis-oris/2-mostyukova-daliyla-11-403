package org.airhockey.model;

public class Puck {
    public float x, y;
    public float vx = 150;// Скорость по X
    public float vy = 150;// Скорость по Y

    public Puck(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
        // Шайба двигается: позиция += скорость × время
        x += vx * dt; // Движение по горизонтали, vx = пикселей/секунду, dt = секунды
        y += vy * dt; // Движение по вертикали
    }// dt = 0.016f (примерно, если 60 FPS) время между кадрами в секундах
}//С dt: скорость постоянна на любом FPS