package net.javaci.mobile.bomberman.core.models;

import com.badlogic.gdx.math.Vector2;

public class PlayerModel extends GameObjectModel {
    private float speed = 150f;
    private String playerName;
    private State state = State.STANDING_DOWN;
    private int targetGridX = -1;
    private int targetGridY = -1;
    //oyunu kuran 1, oyuna ilk katilan oyuncu 2, ikinci katilan 3, son katilan 4 degerini alir.
    private int gameIndex;
    private boolean caught;
    private int lifeCount = 3;
    private StateChangeListener stateChangeListener;

    public void setStateChangeListener(StateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    public int getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
    }

    public int getTargetGridY() {
        return targetGridY;
    }

    public void setTargetGridY(int targetGridY) {
        this.targetGridY = targetGridY;
    }

    public int getTargetGridX() {
        return targetGridX;
    }

    public void setTargetGridX(int targetGridX) {
        this.targetGridX = targetGridX;
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
        if (this.stateChangeListener != null) {
            this.stateChangeListener.onStateChange(this.state);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPosition(Vector2 position) {
        this.x = position.x;
        this.y = position.y;
    }

    public float getOriginX() {
        return this.x + this.width * 0.5f;
    }

    public float getOriginY() {
        return this.y + this.height * 0.5f;
    }

    public boolean isCaught() {
        return caught;
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }

    public void decrementLifeCount() {
        this.lifeCount = this.lifeCount - 1;
    }

    public void setLifeCount(int x) {
        this.lifeCount = x;
    }

    public int getLifeCount() {
        return this.lifeCount;
    }

    public static enum State {
        STANDING_UP, STANDING_DOWN, STANDING_RIGHT, STANDING_LEFT,
        WALKING_UP, WALKING_DOWN, WALKING_RIGHT, WALKING_LEFT,
        STOPPING_UP, STOPPING_DOWN, STOPPING_RIGHT, STOPPING_LEFT, DEAD
    }

    @Override
    public String toString() {
        return "PlayerModel{" +
                "playerName='" + playerName + '\'' +
                ", state=" + state +
                ", lifeCount=" + lifeCount +
                ", gameIndex=" + gameIndex +
                '}';
    }

    public static interface StateChangeListener {
        public void onStateChange(State newState);
    }
}
