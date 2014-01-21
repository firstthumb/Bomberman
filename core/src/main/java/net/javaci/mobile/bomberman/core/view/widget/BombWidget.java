package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import net.javaci.mobile.bomberman.core.models.BombModel;

public class BombWidget extends Actor {

    private final TextureAtlas atlas;
    private BombModel bombModel;
    private Animation bombAnimationBeforeExplosion;
    private float elapsedTime;

    public BombWidget(TextureAtlas atlas, BombModel bombModel) {
        this.atlas = atlas;
        this.bombModel = bombModel;
        prepareBombAnimationBeforeExplosion();
        this.bombModel.setWidth(bombAnimationBeforeExplosion.getKeyFrame(0).getRegionWidth());
        this.bombModel.setHeight(bombAnimationBeforeExplosion.getKeyFrame(0).getRegionHeight());
    }

    private void prepareBombAnimationBeforeExplosion() {
        TextureRegion frames[] = new TextureRegion[6];
        frames[0] = atlas.findRegion("bomb1");
        frames[1] = atlas.findRegion("bomb2");
        frames[2] = atlas.findRegion("bomb3");
        frames[3] = atlas.findRegion("bomb4");
        frames[4] = atlas.findRegion("bomb5");
        frames[5] = atlas.findRegion("bomb6");
        this.bombAnimationBeforeExplosion = new Animation(0.50f, frames);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (bombModel.getState() == BombModel.State.EXPLODE) {
            this.remove();
        } else {
            float deltaTime = Gdx.graphics.getDeltaTime();
            elapsedTime += deltaTime;
            TextureRegion currentFrame = bombAnimationBeforeExplosion.getKeyFrame(elapsedTime, true);
            batch.draw(currentFrame, bombModel.getX(), bombModel.getY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        }
    }

}
