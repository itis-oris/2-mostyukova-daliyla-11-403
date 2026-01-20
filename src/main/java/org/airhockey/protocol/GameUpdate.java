package org.airhockey.protocol;

public class GameUpdate {
    public float puckX;
    public float puckY;

    public float leftX;
    public float leftY;

    public float rightX;
    public float rightY;

    public int scoreLeft;
    public int scoreRight;

    public boolean gameStarted;
    public int connectedPlayers;

    public GameUpdate(float puckX, float puckY,
                      float leftX, float leftY,
                      float rightX, float rightY,
                      int scoreLeft, int scoreRight,
                      boolean gameStarted, int connectedPlayers) {

        this.puckX = puckX;
        this.puckY = puckY;

        this.leftX = leftX;
        this.leftY = leftY;

        this.rightX = rightX;
        this.rightY = rightY;

        this.scoreLeft = scoreLeft;
        this.scoreRight = scoreRight;
        this.gameStarted = gameStarted;
        this.connectedPlayers = connectedPlayers;
    }
}