package net.javaci.mobile.bomberman.core;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.appwarp.AppWarpClient;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.view.SplashScreen;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BomberManGame extends AbstractGame {

    public NetworkInterface client = new AppWarpClient(UserSession.getInstance().getUsername());
    public static String username = UserSession.getInstance().getUsername();

    public static enum ScreenType {
        SPLASH, PLAY, LOBBY
    }

    @Override
    public List<Vector2> getSupportedResolutions() {
        List<Vector2> supportedScreenResolutions = new LinkedList<Vector2>();
        supportedScreenResolutions.add(new Vector2(1280, 800));
        return supportedScreenResolutions;
    }

    @Override
    public LocalizationService getLocalizationService() {
        return new LocalizationService() {
            @Override
            public String getString(String s) {
//                return localizationManager.getString(s);
                return "";
            }

            @Override
            public String getString(String s, Object... args) {
//                return localizationManager.getString(s, args);
                return "";
            }
        };
    }

    @Override
    public void create() {
        Screen splashScreen = new SplashScreen(this);
        setScreen(splashScreen);
        Gdx.input.setCatchBackKey(true);
        configureAssetLoader();
    }


    private void configureAssetLoader() {
        AssetsInterface assets = getAssetsInterface();
//        assets.addAssetConfiguration(LobbyScreen.class.getSimpleName(), Constants.LOBBY_ATLAS, TextureAtlas.class);
    }

    public void switchScreen(final ScreenType screenType, final Map<String, String> parameters) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                displayLoadingWidget();
                switch (screenType) {
                    case PLAY:
                        GameScreenMediator mediator = new GameScreenMediator(BomberManGame.this, client);
                        addScreen(mediator.createScreen());
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void displayLoadingWidget() {
        Screen screen = getScreen();
        if (screen instanceof BomberManGame) {
            ((BomberManGame)screen).displayLoadingWidget();
        }
    }

    private void removeLoadingWidget() {
        Screen screen = getScreen();
        if (screen instanceof BomberManGame) {
            ((BomberManGame)screen).removeLoadingWidget();
        }
    }

    private String getScreenResolution() {
        return Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight();
    }

    public NetworkInterface getClient() {
        return client;
    }
}
