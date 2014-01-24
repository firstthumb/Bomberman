package net.javaci.mobile.bomberman.core.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BombModel extends GameObjectModel {

    public static interface BombListener {
        public void onBombExploded(BombModel bombModel);
    }

    public static enum State {
        COUNT_DOWN, EXPLODE
    }

    private float remainingSeconds = 5;
    private String owner;
    private State state = State.COUNT_DOWN;
    private Set<BombListener> bombListeners = new HashSet<BombListener>();
    private int id;
    private int gridX;
    private int gridY;
    private boolean activated;
    private float activationTime;

    public BombModel(int id) {
        this.id = id;
    }

    public void addBombListener(BombListener listener) {
        this.bombListeners.add(listener);
    }

    public void update(float deltaTime) {
        this.remainingSeconds -= deltaTime;
        if (remainingSeconds <= 0) {
            this.state = State.EXPLODE;
            for (Iterator<BombListener> iterator = bombListeners.iterator(); iterator.hasNext(); ) {
                BombListener bombListener = iterator.next();
                bombListener.onBombExploded(this);
            }
            this.bombListeners.clear();
        }
    }

    public State getState() {
        return this.state;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public float getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(float remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public int getGridX() {
        return gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getId() {
        return id;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public float getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(float activationTime) {
        this.activationTime = activationTime;
    }

    @Override
    public float getOriginX() {
        return x + (width * 0.5f);
    }

    @Override
    public float getOriginY() {
        return y + (height * 0.5f);
    }
}
