package net.javaci.mobile.bomberman.core.models;

public class GhostModel {
    private static int ID = 1;

    // http://strategywiki.org/wiki/Bomberman/How_to_play
    public static enum Type {
        BALLOOM(1), MINVO(2), KONDORIA(3);

        int value;
        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum State {
        WALKING, DEAD
    }

    public static GhostModel createGhostModel() {
        return new GhostModel(ID++);
    }

    private int id;
    private float speed = 60f;
    private float x;
    private float y;
    private Type type = Type.BALLOOM;
    private State state = State.WALKING;

    public GhostModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
