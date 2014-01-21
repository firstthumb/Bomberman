package net.javaci.mobile.bomberman.core.models;

import com.badlogic.gdx.math.Vector2;

public class PlayerModel {
    private float speed = 100f;
    private String playerName;
    private float x;
    private float y;
    private float width;
    private float height;
    private State state = State.STANDING_DOWN;

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

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

    public void setPosition(Vector2 position) {
        this.x = position.x;
        this.y = position.y;
    }

    public static enum State {
        STANDING_UP, STANDING_DOWN, STANDING_RIGHT, STANDING_LEFT, WALKING_UP, WALKING_DOWN, WALKING_RIGHT, WALKING_LEFT
    }

    public float getOriginX() {
        return this.x + this.width * 0.5f;
    }

    public float getOriginY() {
        return this.y + this.height * 0.5f;
    }
}
