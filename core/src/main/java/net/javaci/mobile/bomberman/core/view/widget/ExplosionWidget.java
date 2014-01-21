package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import net.javaci.mobile.bomberman.core.models.BombModel;

public class ExplosionWidget extends Actor {

    private final TextureAtlas atlas;
    private Animation explosionAnimation;
    private float elapsedTime;

    public ExplosionWidget(TextureAtlas atlas) {
        this.atlas = atlas;
        prepareAnimation();
    }

    private void prepareAnimation() {
        int numFrames = 9;
        TextureRegion frames[] = new TextureRegion[numFrames];
        for (int i=0; i<numFrames; i++) {
            frames[i] = atlas.findRegion("explosion" + (i+1));
        }
        this.explosionAnimation = new Animation(0.08f, frames);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float deltaTime = Gdx.graphics.getDeltaTime();
        elapsedTime += deltaTime;
        TextureRegion currentFrame = explosionAnimation.getKeyFrame(elapsedTime, false);
        batch.draw(currentFrame, getX(), getY());
        if (explosionAnimation.isAnimationFinished(elapsedTime)) {
            this.remove();
        }
    }

}
