package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class DeadGhostWidget extends Actor {

    private Animation deadAnimation;

    private float elapsedTime;
    private TextureRegion currentFrame;
    private float x;
    private float y;
    private int type;

    public DeadGhostWidget(TextureAtlas atlas, float x, float y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        prepareDeadAnimation(atlas);
    }

    private void prepareDeadAnimation(TextureAtlas atlas) {
        TextureRegion frames[] = new TextureRegion[3];
        frames[0] = atlas.findRegion(generateDeadImageName(type, 1));
        frames[1] = atlas.findRegion(generateDeadImageName(type, 2));
        frames[2] = atlas.findRegion(generateDeadImageName(type, 3));
        deadAnimation = new Animation(0.35f, frames);
    }

    private String generateDeadImageName(int type, int frameIndex) {
        return "ghost" + type + "dead" + frameIndex;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float deltaTime = Gdx.graphics.getDeltaTime();
        elapsedTime += deltaTime;

        currentFrame = deadAnimation.getKeyFrame(elapsedTime, false);
        if (deadAnimation.isAnimationFinished(elapsedTime)) {
            this.remove();
        }

        batch.draw(
                currentFrame,
                x,
                y);
    }
}
