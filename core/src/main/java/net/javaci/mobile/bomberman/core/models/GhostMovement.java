package net.javaci.mobile.bomberman.core.models;

import net.javaci.mobile.bomberman.core.view.GameScreen;

public class GhostMovement {

    private GameScreen.Direction direction;
    private int distance;

    public GhostMovement(GameScreen.Direction direction, int distance) {
        this.direction = direction;
        this.distance = distance;
    }

    public GameScreen.Direction getDirection() {
        return direction;
    }

    public void setDirection(GameScreen.Direction direction) {
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
