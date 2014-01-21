package net.javaci.mobile.bomberman.core.models;

public abstract class GameObjectModel {
    protected float x;
    protected float y;
    protected float width;
    protected float height;

    public abstract float getOriginX();

    public abstract float getOriginY();

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
}
