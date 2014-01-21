package net.javaci.mobile.bomberman.core.models;

public class PlayerModel {
    private float speed = 60f;
    private String playerName;
    private float x;
    private float y;
    private State state = State.STANDING_DOWN;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public static enum State {
        STANDING_UP, STANDING_DOWN, STANDING_RIGHT, STANDING_LEFT, WALKING_UP, WALKING_DOWN, WALKING_RIGHT, WALKING_LEFT
    }
}
