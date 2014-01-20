package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.view.widget.BombermanWidget;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;

import java.util.HashMap;
import java.util.Map;


public class GameScreen extends BomberManScreen {

    Map<String, BombermanWidget> playerWidgets = new HashMap<String, BombermanWidget>();
    private GameScreenMediator gameScreenMediator;

    public static enum Direction {
        UP, DOWN, RIGHT, LEFT
    }

    public GameScreen(AbstractGame game, BomberManMediator mediator) {
        super(game, mediator);
        this.gameScreenMediator = (GameScreenMediator) mediator;
    }

    @Override
    public void show() {
        super.show();
        setGamePadListeners();
        playerWidgets.put("1", new BombermanWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), 1));
        playerWidgets.put("2", new BombermanWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), 2));
        stage.addActor(playerWidgets.get("1"));
        stage.addActor(playerWidgets.get("2"));
    }

    private void setGamePadListeners() {
        findButton("gamePadUpButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(BomberManGame.username, Direction.UP);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(BomberManGame.username, Direction.UP);
            }
        });

        findButton("gamePadDownButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(BomberManGame.username, Direction.DOWN);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(BomberManGame.username, Direction.DOWN);
            }
        });

        findButton("gamePadRightButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(BomberManGame.username, Direction.RIGHT);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(BomberManGame.username, Direction.RIGHT);
            }
        });

        findButton("gamePadLeftButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(BomberManGame.username, Direction.LEFT);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(BomberManGame.username, Direction.LEFT);
            }
        });
    }

    public void onMoveStart(String username, Direction direction) {
        if (BomberManGame.username.equals(username)) {
            gameScreenMediator.move(direction);
        }
        switch (direction) {
            case UP:
                playerWidgets.get(username).moveUp();
                break;
            case DOWN:
                playerWidgets.get(username).moveDown();
                break;
            case LEFT:
                playerWidgets.get(username).moveLeft();
                break;
            case RIGHT:
                playerWidgets.get(username).moveRight();
                break;
            default:
                break;
        }
    }

    public void onMoveEnd(String username, Direction direction) {
        if (BomberManGame.username.equals(username)) {
            gameScreenMediator.moveEnd(direction);
        }
        playerWidgets.get(username).stop();
    }


}
