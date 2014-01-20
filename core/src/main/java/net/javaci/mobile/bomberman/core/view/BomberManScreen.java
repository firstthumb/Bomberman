package net.javaci.mobile.bomberman.core.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.Constants;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.AbstractScreen;
import net.peakgames.libgdx.stagebuilder.core.widgets.LoadingWidget;

public class BomberManScreen extends AbstractScreen {
    private static final String NULL_STRING = "null";
    protected BomberManGame game;
    protected BomberManMediator mediator;
    private LoadingWidget loadingWidget;


    public BomberManScreen(AbstractGame game, BomberManMediator mediator) {
        super(game);
        this.game = (BomberManGame) game;
        this.mediator = mediator;

        setBackKeyListener();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

    }

    @Override
    public void unloadAssets() {
        game.getAssetsInterface().unloadAssets(this.getClass().getSimpleName());
    }

    @Override
    public void onStageReloaded() {
    }

    @Override
    public void show() {
        super.show();
        mediator.onScreenShowInternal();
        removeLoadingWidget();
    }

    @Override
    public void hide() {
        super.hide();
    }

    private void setBackKeyListener() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    //TODO play sound
                    boolean backButtonHandled = handleBackButton();
                    if (!backButtonHandled) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (game.getNumberScreens() > 1) {
                                    game.backToPreviousScreen();
                                }
                            }
                        });
                    }
                }
                return true;
            }
        });
    }

    /**
     * Screen'ler back button'u kendisi handle etmek isterse bu metodu override etmeleri gerekir.
     *
     * @return true donerse back button handle islemi screen'e birakilir.
     */
    public boolean handleBackButton() {
        return false;
    }

    public void removeLoadingWidget() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (loadingWidget != null) {
                    loadingWidget.hide();
                    loadingWidget.remove();
                }
            }
        });

    }

    public void displayLoadingWidget(long timeout) {
        if (loadingWidget != null) {
            loadingWidget.hide();
            loadingWidget.remove();
        }
        loadingWidget = new LoadingWidget(game.getAssetsInterface(),
                game.getResolutionHelper(),
                0.5f,
                true,
                game.getLocalizationService().getString("loading"),
                Constants.FONT_23,
                Constants.LOADING_WIDGET_BACKGROUND,
                Constants.LOADING_WIDGET_BAR,
                Constants.LOADING_BAR_ATLAS);
        if (timeout > 0) {
            loadingWidget.setTimeoutDuration(timeout);
        }
        loadingWidget.show();
        stage.addActor(loadingWidget);
    }

    public void displayLoadingWidget() {
        displayLoadingWidget(Constants.LOADING_WIDGET_TIMEOUT);
    }

    @Override
    public boolean isResizable() {
        //TODO Screen orientation destegi eklerken portrait olabilecek ekranlarda bu metodu override edip true donmemiz gerekecek.
        return false;
    }

}
