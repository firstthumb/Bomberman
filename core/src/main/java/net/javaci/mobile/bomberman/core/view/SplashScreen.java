package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.Constants;
import net.javaci.mobile.bomberman.core.mediator.LobbyScreenMediator;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetLoaderListener;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;

public class SplashScreen implements Screen {

    private static final float ANIMATION_DURATION = 1.5f;// seconds
    private static final float SPLASH_LOGO_MIN_SEEN_DURATION = 2000; //miliseconds
    private BomberManGame game;
    private SpriteBatch spriteBatch;
    private Texture logo;
    private float logoX;
    private float logoY;
    private AssetManager assetManager;
    private boolean hideSplashScreen = false;
    private float elapsedTime;
    private float logoMoveAnimationDistance;
    private float originalLogoX;
    private long startTime;
    private ShapeRenderer shapeRenderer;
    private float screenWidth;
    private float screenHeight;
    private Color progressBarColor;

    public SplashScreen(AbstractGame game) {
        this.game = (BomberManGame) game;
        this.assetManager = game.getAssetsInterface().getAssetMAnager();
        this.shapeRenderer = new ShapeRenderer();
        this.screenHeight = Gdx.graphics.getHeight();
        this.screenWidth = Gdx.graphics.getWidth();
        this.progressBarColor = Color.valueOf("B02B2C");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(logo, logoX, logoY);
        spriteBatch.end();
        this.assetManager.update();
        if (hideSplashScreen && splashLogoSeenEnough()) {
            elapsedTime = elapsedTime + delta;
            float percentage = elapsedTime / ANIMATION_DURATION;
            percentage = Interpolation.swing.apply(percentage);

            if (elapsedTime > ANIMATION_DURATION) {
                LobbyScreenMediator mediator = new LobbyScreenMediator(this.game);
                game.setScreen(mediator.createScreen());
                percentage = 1;
            }
            logoX = originalLogoX + logoMoveAnimationDistance * percentage;
        }

        renderAssetManagerProgress();
    }

    private void renderAssetManagerProgress() {
        float progress = assetManager.getProgress();
        if(progress < 1) {
            float width = this.screenWidth * progress;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(this.progressBarColor);
            shapeRenderer.rect((this.screenWidth- width) * 0.5f, 0, width , this.screenHeight * 0.01f);
            shapeRenderer.end();
        }

    }

    private boolean splashLogoSeenEnough() {
        return (System.currentTimeMillis() - startTime) > SPLASH_LOGO_MIN_SEEN_DURATION;
    }

    @Override
    public void show() {
        AssetsInterface assets = game.getAssetsInterface();
        spriteBatch = new SpriteBatch();
        loadLogo();
        String assetsKey = "SplashScreen";
        loadFonts(assets, assetsKey);
        assets.addAssetConfiguration(assetsKey, Constants.COMMON_ATLAS, TextureAtlas.class);
        assets.addAssetConfiguration(assetsKey, Constants.SKIN_ATLAS, TextureAtlas.class);
        assets.addAssetConfiguration(assetsKey, Constants.LOADING_BAR_ATLAS, TextureAtlas.class);
        assets.loadAssetsAsync(assetsKey, new AssetLoaderListener() {
            @Override
            public void onAssetsLoaded() {
                hideSplashScreen = true;
            }
        });
        startTime = System.currentTimeMillis();
    }

    private void loadFonts(AssetsInterface assets, String assetsKey) {
        assets.addAssetConfiguration(assetsKey, "26pt.fnt", BitmapFont.class);
        assets.addAssetConfiguration(assetsKey, "40pt_title.fnt", BitmapFont.class);
        assets.addAssetConfiguration(assetsKey, "large.fnt", BitmapFont.class);
        assets.addAssetConfiguration(assetsKey, "normal.fnt", BitmapFont.class);
        assets.addAssetConfiguration(assetsKey, "small.fnt", BitmapFont.class);
        assets.addAssetConfiguration(assetsKey, "pixelfont_26.fnt", BitmapFont.class);
    }

    private void loadLogo() {
        String path = getImagesPath();
        logo = new Texture(Gdx.files.internal(path + "/splash_logo.png"));
        logoX = (Gdx.graphics.getWidth() - logo.getWidth()) * 0.5f;
        logoY = (Gdx.graphics.getHeight() - logo.getHeight()) * 0.5f;
        logoMoveAnimationDistance = Gdx.graphics.getWidth() - logoX;
        originalLogoX = logoX;
    }

    private String getImagesPath() {
        Vector2 res = game.getBestResolution();
        return "images/" + (int) res.x + "x" + (int) res.y + "/";
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        logo.dispose();
        shapeRenderer.dispose();
        Gdx.app.log("HeartsPlus", "SplashScreen disposed.");
    }
}
