package org.airhockey.model;

//Хранит состояние ракетки и ограничивает её движение в пределах поля.
public class Paddle {
    public float x, y;
    public static final float WIDTH = 20f;
    public static final float HEIGHT = 80f;

    public Paddle(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void clamp(float min, float max) {
        if (y < min) y = min;// Не выходит за верхнюю границу
        if (y > max) y = max;// Не выходит за нижнюю границу
    }
}