package net.javaci.mobile.bomberman.core.models;

public class GhostModel extends GameObjectModel {
    public interface GhostListener {
        public void onStop();
    }

    private GhostListener listener;

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
        STANDING_UP, STANDING_DOWN, STANDING_RIGHT, STANDING_LEFT, WALKING_UP, WALKING_DOWN, WALKING_RIGHT, WALKING_LEFT, DEAD
    }

    public static GhostModel createGhostModel() {
        return new GhostModel(ID++);
    }

    private int id;
    private float speed = 60f;
    private int targetGridX;
    private int targetGridY;
    private int gridX;
    private int gridY;
    private Type type = Type.BALLOOM;
    private State state = State.STANDING_DOWN;

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

    public float getOriginX() {
        return this.x + this.width * 0.5f;
    }

    public float getOriginY() {
        return this.y + this.height * 0.5f;
    }

    public int getTargetGridX() {
        return targetGridX;
    }

    public void setTargetGridX(int targetGridX) {
        this.targetGridX = targetGridX;
    }

    public int getTargetGridY() {
        return targetGridY;
    }

    public void setTargetGridY(int targetGridY) {
        this.targetGridY = targetGridY;
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

    public void setListener(GhostListener listener) {
        this.listener = listener;
    }

    public GhostListener getListener() {
        return listener;
    }
}