package org.airhockey.storage;

public class MatchRecord {
    private String winner;
    private int leftScore;
    private int rightScore;
    private long time;

    public MatchRecord(String winner, int leftScore, int rightScore) {
        this.winner = winner;
        this.leftScore = leftScore;
        this.rightScore = rightScore;
        this.time = System.currentTimeMillis();
    }

    public String getWinner() { return winner; }
    public int getLeftScore() { return leftScore; }
    public int getRightScore() { return rightScore; }
    public long getTime() { return time; }
}