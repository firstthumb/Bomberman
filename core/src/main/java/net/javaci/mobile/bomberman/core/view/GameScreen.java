package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.javaci.mobile.bomberman.core.view.widget.BombermanWidget;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;


public class GameScreen extends BomberManScreen {

    private BombermanWidget bombermanWidget;

    static enum Direction {
        UP, DOWN, RIGHT, LEFT
    }

    public GameScreen(AbstractGame game, BomberManMediator mediator) {
        super(game, mediator);
    }

    @Override
    public void show() {
        super.show();
        setGamePadListeners();
        bombermanWidget = new BombermanWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), 2);
        stage.addActor(bombermanWidget);
    }

    private void setGamePadListeners() {
        findButton("gamePadUpButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(Direction.UP);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(Direction.UP);
            }
        });

        findButton("gamePadDownButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(Direction.DOWN);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(Direction.DOWN);
            }
        });

        findButton("gamePadRightButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(Direction.RIGHT);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(Direction.RIGHT);
            }
        });

        findButton("gamePadLeftButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(Direction.LEFT);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(Direction.LEFT);
            }
        });
    }

    private void onMoveStart(Direction direction) {
        switch (direction) {
            case UP:
                bombermanWidget.moveUp();
                break;
            case DOWN:
                bombermanWidget.moveDown();
                break;
            case LEFT:
                bombermanWidget.moveLeft();
                break;
            case RIGHT:
                bombermanWidget.moveRight();
                break;
            default:
                break;
        }

    }

    private void onMoveEnd(Direction direction) {
        bombermanWidget.stop();
    }


}
