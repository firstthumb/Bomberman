package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.GameFactory;
import net.javaci.mobile.bomberman.core.World;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.models.BombModel;
import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.LabyrinthModel;
import net.javaci.mobile.bomberman.core.models.PlayerModel;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;
import net.javaci.mobile.bomberman.core.server.GameServer;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.util.Log;
import net.javaci.mobile.bomberman.core.view.widget.*;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;

import java.util.List;


public class GameScreen extends BomberManScreen {

    private World world = new World();
    private GameScreenMediator gameScreenMediator;
    private GameServer gameServer;
    private LabyrinthModel labyrinthModel;
    private LabyrinthWidget labyrinthWidget;

    public GameScreen(AbstractGame game, BomberManMediator mediator) {
        super(game, mediator);
        this.gameScreenMediator = (GameScreenMediator) mediator;
        this.gameScreenMediator.setRoom(UserSession.getInstance().getRoom());
        initializeDefaultGameBoard();
    }

    public void initializeDefaultGameBoard() {
        labyrinthModel = new LabyrinthModel();
        world.initialize(labyrinthModel, getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());
        labyrinthWidget = new LabyrinthWidget(labyrinthModel, getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());
        stage.getRoot().addActorAt(0, labyrinthWidget);
    }

    public void initializeGameOnServer() {
        GameFactory.GameModel gameModel = GameFactory.getGameModel(gameScreenMediator.getLevel());

        labyrinthModel.generateBricks(gameModel.numBricks);
        PlayerModel playerModel = addPlayerModelToWorld(UserSession.getInstance().getUsername(), 1);
        // TODO: user join notification



        for (int i=0; i<gameModel.numGhosts; i++) {
            final GhostModel ghostModel = GhostModel.createGhostModel(Math.random() > 0.5f ? GhostModel.Type.BALLOOM : GhostModel.Type.MINVO);
            ghostModel.setWidth(world.getGridWidth());
            ghostModel.setHeight(world.getGridHeight());
            ghostModel.setListener(new GhostModel.GhostListener() {
                @Override
                public void onStop() {
                    gameServer.moveGhost(ghostModel.getId());
                }
            });
            world.putGhostEmptyPlace(ghostModel);
            world.addGhostModel(ghostModel);

            GhostWidget ghostWidget = new GhostWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), ghostModel);
            stage.addActor(ghostWidget);
        }
    }

    private PlayerModel addPlayerModelToWorld(String username, int gameIndex) {
        PlayerModel playerModel = new PlayerModel();
        playerModel.setPlayerName(username);
        playerModel.setPosition(labyrinthWidget.getPlayerInitialPosition(gameIndex));
        playerModel.setWidth(world.getGridWidth());
        playerModel.setHeight(world.getGridHeight());
        playerModel.setGameIndex(gameIndex);
        world.addPlayerModel(playerModel);
        BombermanWidget bombermanWidget = new BombermanWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), gameIndex, playerModel);
        stage.addActor(bombermanWidget);
        return playerModel;
    }

    @Override
    public void show() {
        super.show();
        addBackground();
        
        if (UserSession.getInstance().isServer()) {
            initializeGameOnServer();
            gameServer = new GameServer(world);
            gameServer.initialize(game.getClient(), gameScreenMediator);
            gameScreenMediator.setGameServer(gameServer);
        }
        else {
            // TODO : wait server response to create game screen
        }

        prepareGamePad();

        prepareBombButton();

        initializeBeforeGamePanel();
    }

    private void initializeBeforeGamePanel() {
        Group panel = (Group) findActor("beforeGamePanel");
        panel.remove();
        getRoot().addActor(panel);
        ((Label)panel.findActor("roomName")).setText(UserSession.getInstance().getRoom().getName());
        if (UserSession.getInstance().isServer()) {
            setBeforeGamePanelTitle("Waiting for players...");
            Label label = (Label)panel.findActor("roomOwner");
            label.setText("Player 1 (Room Owner) : " + UserSession.getInstance().getUsername());
            label.setVisible(true);
        } else {
            setBeforeGamePanelTitle("Waiting for room owner to start the game...");
        }

    }

    private void setBeforeGamePanelTitle(String title) {
        Group panel = (Group) findActor("beforeGamePanel");
        Label label = (Label)panel.findActor("title");
        label.setText(title);
    }

    private void prepareBombButton() {
        Button bombButton = findButton("bombButton");
        bombButton.remove();
        stage.addActor(bombButton);
        bombButton.setWidth(bombButton.getWidth() * 2f);
        bombButton.setHeight(bombButton.getHeight() * 2f);
        /*
        Color color = bombButton.getColor();
        color.a = 0.5f;
        */
        bombButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScreenMediator.onBombButtonClicked();
            }
        });
    }

    private void addBackground() {
        Image bg = new Image(createBgTexture());
        bg.setPosition(0, 0);
        bg.setSize(getStageBuilder().getResolutionHelper().getScreenWidth(), getStageBuilder().getResolutionHelper().getScreenHeight());
        stage.getRoot().addActorAt(0, bg);
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
    }

    public void onOpponentDropBomb(int id, int gridX, int gridY, String owner) {
        BombModel bombModel = world.dropBomb(id, gridX, gridY, owner);
        bombModel.addBombListener(new BombModel.BombListener() {
            @Override
            public void onBombExploded(BombModel bombModel) {
                renderBombExplosion(bombModel);
            }
        });
        addBombToScreen(bombModel);
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
        findActor("beforeGamePanel").remove();
        world.initialize(labyrinthModel, getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());

        LabyrinthWidget labyrinthWidget = new LabyrinthWidget(world.getLabyrinthModel(), getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());
        stage.addActor(labyrinthWidget);

        world.addGhostModels(ghostModels);

        for (GhostModel ghostModel : world.getGhostModels().values()) {
            ghostModel.setWidth(world.getGridWidth());
            ghostModel.setHeight(world.getGridHeight());
            GhostWidget ghostWidget = new GhostWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), ghostModel);
            stage.addActor(ghostWidget);
        }
    }


    public void addBombToScreen(BombModel bombModel) {
        BombWidget bombWidget = new BombWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), bombModel);
        stage.addActor(bombWidget);
    }

    public void renderBombExplosion(BombModel bombModel) {
        List<Vector2> cellIndexes = world.calculateBombExplosionCells(bombModel);
        List<Vector2> coords = world.convertCellIndexToScreenCoordinates(cellIndexes);

        for (Vector2 pos : coords) {
            ExplosionWidget explosionWidget = new ExplosionWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"));
            explosionWidget.setX(pos.x);
            explosionWidget.setY(pos.y);
            stage.addActor(explosionWidget);
        }

        for (Vector2 cell : cellIndexes) {
            labyrinthModel.getGrid()[(int)cell.x][(int)cell.y] = LabyrinthModel.EMPTY;
        }
    }

    public void onPlayerJoinedRoom(String playerName) {
        int gameIndex = world.getNextGameIndex();
        addPlayerModelToWorld(playerName, gameIndex);
        final Group panel = (Group) findActor("beforeGamePanel");
        if (gameIndex == 1) {
            Label label = (Label)panel.findActor("roomOwner");
            label.setText("Player 1 (Room Owner) : " + playerName);
            label.setVisible(true);
        } else {
            Label playerJoinedLabel = (Label)panel.findActor("player" + gameIndex+ "joined");
            playerJoinedLabel.setText("Player " + gameIndex + " joined room. " + playerName);
            playerJoinedLabel.setVisible(true);
        }
        if (UserSession.getInstance().isServer()) {
            Button startButton = (Button)panel.findActor("startGameButton");
            startButton.setVisible(true);
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    panel.remove();
                    gameServer.createGame();
                }
            });

        }
    }

    public static enum Direction {
        UP, DOWN, RIGHT, LEFT
    }

    public World getWorld() {
        return this.world;
    }

    private Texture createBgTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("787878"));
        pixmap.fill();
        Texture texture = new Texture(pixmap); // TODO dispose texture
        pixmap.dispose();
        return texture;
    }

    public void onMoveGhost(int ghostId, int gridX, int gridY, String direction, int distance) {
        world.moveGhost(ghostId, gridX, gridY, Direction.valueOf(direction), distance);
    }

    public void onOwnerLeft() {
        game.backToPreviousScreen();
        // TODO : Show popup information

        displayInfoPopup("Game Owner left");
    }

    public void onGameFinished() {
        Log.d("Game finished");
    }

    @Override
    public void dispose() {
        super.dispose();
        RoomModel roomModel = UserSession.getInstance().getRoom();
        if (roomModel != null) {
            game.getClient().leaveRoom(roomModel.getId());
            //game.getClient().deleteRoom(roomModel.getId());
        }
        else {
            Log.e("Room Model is NULL. Cannot delete room from Server");
        }
    }
}
