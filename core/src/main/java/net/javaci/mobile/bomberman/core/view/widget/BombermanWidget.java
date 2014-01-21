package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import net.javaci.mobile.bomberman.core.models.PlayerModel;

public class BombermanWidget extends WidgetGroup {
    private PlayerModel playerModel;
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
    private int bombermanIndex;

    public BombermanWidget(TextureAtlas atlas, int bombermanIndex, PlayerModel playerModel) {
        this.bombermanIndex = bombermanIndex;
        this.playerModel = playerModel;
        prepareStandingTextures(atlas);
        prepareWalkUpAnimation(atlas);
        prepareWalkDownAnimation(atlas);
        prepareWalkRightAnimation(atlas);
        prepareWalkLeftAnimation(atlas);
        this.playerModel.setWidth(downStand.getRegionWidth());
        this.playerModel.setHeight(downStand.getRegionHeight());
    }

    private String generateUpImageName(int bombermanIndex, int frameIndex) {
        return "girl" + bombermanIndex + "up" + frameIndex;
    }

    private String generateDownImageName(int bombermanIndex, int frameIndex) {
        return "girl" + bombermanIndex + "down" + frameIndex;
    }

    private String generateRightImageName(int bombermanIndex, int frameIndex) {
        return "girl" + bombermanIndex + "right" + frameIndex;
    }

    private String generateLeftImageName(int bombermanIndex, int frameIndex) {
        return "girl" + bombermanIndex + "left" + frameIndex;
    }

    private void prepareStandingTextures(TextureAtlas atlas) {
        upStand = atlas.findRegion(generateUpImageName(this.bombermanIndex, 1));
        downStand = atlas.findRegion(generateDownImageName(this.bombermanIndex, 1));
        rightStand = atlas.findRegion(generateRightImageName(this.bombermanIndex, 1));
        leftStand = atlas.findRegion(generateLeftImageName(this.bombermanIndex, 1));
    }

    private void prepareWalkRightAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion(generateRightImageName(this.bombermanIndex, 1));
        frames[1] = atlas.findRegion(generateRightImageName(this.bombermanIndex, 2));
        frames[2] = atlas.findRegion(generateRightImageName(this.bombermanIndex, 3));
        walkRightAnimation = new Animation(0.15f, frames);
    }

    private void prepareWalkLeftAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion(generateLeftImageName(this.bombermanIndex, 1));
        frames[1] = atlas.findRegion(generateLeftImageName(this.bombermanIndex, 2));
        frames[2] = atlas.findRegion(generateLeftImageName(this.bombermanIndex, 3));
        walkLeftAnimation = new Animation(0.15f, frames);
    }

    private void prepareWalkUpAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion(generateUpImageName(this.bombermanIndex, 1));
        frames[1] = atlas.findRegion(generateUpImageName(this.bombermanIndex, 2));
        frames[2] = atlas.findRegion(generateUpImageName(this.bombermanIndex, 3));
        walkUpAnimation = new Animation(0.15f, frames);
    }

    private void prepareWalkDownAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion(generateDownImageName(this.bombermanIndex, 1));
        frames[1] = atlas.findRegion(generateDownImageName(this.bombermanIndex, 2));
        frames[2] = atlas.findRegion(generateDownImageName(this.bombermanIndex, 3));
        walkDownAnimation = new Animation(0.15f, frames);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float deltaTime = Gdx.graphics.getDeltaTime();
        elapsedTime += deltaTime;

        switch (playerModel.getState()) {
            case WALKING_UP:
                currentFrame = walkUpAnimation.getKeyFrame(elapsedTime, true);
                break;
            case WALKING_DOWN:
                currentFrame = walkDownAnimation.getKeyFrame(elapsedTime, true);
                break;
            case WALKING_RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(elapsedTime, true);
                break;
            case WALKING_LEFT:
                currentFrame = walkLeftAnimation.getKeyFrame(elapsedTime, true);
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
        batch.draw(currentFrame, playerModel.getX(), playerModel.getY());
    }
}
