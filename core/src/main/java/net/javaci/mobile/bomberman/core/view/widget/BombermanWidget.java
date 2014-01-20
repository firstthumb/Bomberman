package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class BombermanWidget extends WidgetGroup {
    private static final float SPEED = 40f; //10 pixels seconds.
    private State state = State.STANDING_DOWN;
    private Animation walkUpAnimation;
    private Animation walkDownAnimation;
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private TextureRegion upStand;
    private TextureRegion downStand;
    private TextureRegion leftStand;
    private TextureRegion rightStand;
    private TextureRegion currentFrame;
    private float elapsedTime;

    public BombermanWidget(TextureAtlas atlas) {
        prepareStandingTextures(atlas);
        prepareWalkUpAnimation(atlas);
        prepareWalkDownAnimation(atlas);
        prepareWalkRightAnimation(atlas);
        prepareWalkLeftAnimation(atlas);
    }

    private void prepareStandingTextures(TextureAtlas atlas) {
        upStand = atlas.findRegion("girl1up1");
        downStand = atlas.findRegion("girl1down1");
        rightStand = atlas.findRegion("girl1right1");
        leftStand = atlas.findRegion("girl1left1");
    }

    private void prepareWalkRightAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion("girl1right1");
        frames[1] = atlas.findRegion("girl1right2");
        frames[2] = atlas.findRegion("girl1right3");
        walkRightAnimation = new Animation(0.15f, frames);
    }

    private void prepareWalkLeftAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion("girl1left1");
        frames[1] = atlas.findRegion("girl1left2");
        frames[2] = atlas.findRegion("girl1left3");
        walkLeftAnimation = new Animation(0.15f, frames);
    }

    private void prepareWalkUpAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion("girl1up1");
        frames[1] = atlas.findRegion("girl1up2");
        frames[2] = atlas.findRegion("girl1up3");
        walkUpAnimation = new Animation(0.15f, frames);
    }

    private void prepareWalkDownAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion("girl1down1");
        frames[1] = atlas.findRegion("girl1down2");
        frames[2] = atlas.findRegion("girl1down3");
        walkDownAnimation = new Animation(0.15f, frames);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float deltaTime = Gdx.graphics.getDeltaTime();
        elapsedTime += deltaTime;

        switch (state) {
            case WALKING_UP:
                currentFrame = walkUpAnimation.getKeyFrame(elapsedTime, true);
                setY(getY() + deltaTime * SPEED);
                break;
            case WALKING_DOWN:
                currentFrame = walkDownAnimation.getKeyFrame(elapsedTime, true);
                setY(getY() - deltaTime * SPEED);
                break;
            case WALKING_RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(elapsedTime, true);
                setX(getX() + deltaTime * SPEED);
                break;
            case WALKING_LEFT:
                currentFrame = walkLeftAnimation.getKeyFrame(elapsedTime, true);
                setX(getX() - deltaTime * SPEED);
                break;
            case STANDING_UP:
                currentFrame = upStand;
                break;
            case STANDING_DOWN:
                currentFrame = downStand;
                break;
            case STANDING_RIGHT:
                currentFrame = rightStand;
                break;
            case STANDING_LEFT:
                currentFrame = leftStand;
                break;
            default:
                break;
        }

        batch.draw(currentFrame, getX(), getY());
    }

    public void moveUp() {
        state = State.WALKING_UP;
    }

    public void moveDown() {
        state = State.WALKING_DOWN;
    }

    public void moveRight() {
        state = State.WALKING_RIGHT;
    }

    public void moveLeft() {
        state = State.WALKING_LEFT;
    }

    public void stop() {
        switch (state) {
            case WALKING_DOWN:
                state = State.STANDING_DOWN;
                break;
            case WALKING_UP:
                state = State.STANDING_UP;
                break;
            case WALKING_LEFT:
                state = State.STANDING_LEFT;
                break;
            case WALKING_RIGHT:
                state = State.STANDING_RIGHT;
                break;
            default:
                state = State.STANDING_DOWN;
                break;
        }

    }

    enum State {
        STANDING_UP, STANDING_DOWN, STANDING_RIGHT, STANDING_LEFT, WALKING_UP, WALKING_DOWN, WALKING_LEFT, WALKING_RIGHT
    }
}
