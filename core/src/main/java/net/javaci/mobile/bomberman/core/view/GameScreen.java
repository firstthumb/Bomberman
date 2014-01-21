package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.World;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.LabyrinthModel;
import net.javaci.mobile.bomberman.core.models.PlayerModel;
import net.javaci.mobile.bomberman.core.server.GameServer;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.view.widget.BombermanWidget;
import net.javaci.mobile.bomberman.core.view.widget.GhostWidget;
import net.javaci.mobile.bomberman.core.view.widget.LabyrinthWidget;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;

import java.util.List;


public class GameScreen extends BomberManScreen {

    private World world = new World();
    private GameScreenMediator gameScreenMediator;
    private GameServer gameServer;

    public GameScreen(AbstractGame game, BomberManMediator mediator) {
        super(game, mediator);
        this.gameScreenMediator = (GameScreenMediator) mediator;
    }

    public void initializeGame() {
        LabyrinthModel labyrinthModel = new LabyrinthModel();
        labyrinthModel.generateBricks();
        world.setLabyrinthModel(labyrinthModel);

        PlayerModel playerModel = new PlayerModel();
        playerModel.setPlayerName(UserSession.getInstance().getUsername());
        world.addPlayerModel(playerModel);
        // TODO: user join notification

        BombermanWidget bombermanWidget = new BombermanWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), 1, playerModel);
        stage.addActor(bombermanWidget);

        LabyrinthWidget labyrinthWidget = new LabyrinthWidget(labyrinthModel, getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());
        stage.addActor(labyrinthWidget);

        GhostModel ghostModel = GhostModel.createGhostModel();
        world.addGhostModel(ghostModel);

        GhostWidget ghostWidget = new GhostWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), ghostModel);
        stage.addActor(ghostWidget);
    }

    @Override
    public void show() {
        super.show();

        if (UserSession.getInstance().isOwnerRoom()) {
            initializeGame();
            gameServer = new GameServer(world);
            gameServer.initialize(game.getClient(), gameScreenMediator);
        }
        else {
            // TODO : wait server response to create game screen
        }

        prepareGamePad();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        world.update(delta);
    }

    private void prepareGamePad() {
        Actor gamePad = findActor("gamePad");
        gamePad.remove();
        stage.addActor(gamePad);

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

        findButton("startGameButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameServer.createGame();
            }
        });
    }

    public void onMoveStart(String username, Direction direction) {
        if (BomberManGame.username.equals(username)) {
            gameScreenMediator.move(direction);
        }
        world.movePlayer(username, direction);
    }

    public void onMoveEnd(String username, Direction direction) {
        if (BomberManGame.username.equals(username)) {
            gameScreenMediator.moveEnd(direction);
        }
        world.stopPlayer(username);
    }

    public void onCreateGame(LabyrinthModel labyrinthModel, List<GhostModel> ghostModels) {
        world.setLabyrinthModel(labyrinthModel);

        LabyrinthWidget labyrinthWidget = new LabyrinthWidget(world.getLabyrinthModel(), getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());
        stage.addActor(labyrinthWidget);

        world.addGhostModels(ghostModels);

        for (GhostModel ghostModel : world.getGhostModels().values()) {
            GhostWidget ghostWidget = new GhostWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), ghostModel);
            stage.addActor(ghostWidget);
        }
    }

    public static enum Direction {
        UP, DOWN, RIGHT, LEFT
    }


}
