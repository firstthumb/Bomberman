package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    public LabyrinthWidget getLabyrinthWidget() {
        return labyrinthWidget;
    }

    private LabyrinthWidget labyrinthWidget;
    private Direction previousFlingDirection;
    private boolean isPreferedControlGamePad = true;

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
        Group group = (Group) findActor("labyrinth");
        group.addActor(labyrinthWidget);
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

                @Override
                public void onCaught(List<String> players) {
                    gameServer.caughtPlayer(ghostModel.getId(), players);
                }
            });
            world.putGhostEmptyPlace(ghostModel);
            world.addGhostModel(ghostModel);

            GhostWidget ghostWidget = new GhostWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), ghostModel);
            getGameObjectsGroup().addActor(ghostWidget);
        }
    }

    public boolean existPlayerOnWorld(String playerName) {
        return world.getPlayerModel(playerName) != null;
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
        //stage.addActor(bombermanWidget);
        Group group = (Group) findActor("gameObjectsGroup");
        group.addActor(bombermanWidget);
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

        Vector2 gameAreaPos = getStageBuilder().getResolutionHelper().getGameAreaPosition();
        getLabyrinthGroup().setPosition(-gameAreaPos.x, -gameAreaPos.y);
        getGameObjectsGroup().setPosition(-gameAreaPos.x, -gameAreaPos.y);

        prepareGamePad();

        prepareBombButton();
        
        prepareSettingsButton();

        initializeBeforeGamePanel();
        
        prepareStatsWidgets();

        prepareInputProcessor();
        
        prepareDisconnectPopup();

        prepareRoomOwnerLeftPopup();
    }

    private void prepareDisconnectPopup() {
        Group group = (Group)findActor("disconnectPopup");
        Button button = (Button) group.findActor("backToLobbyButton");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToLobby();
            }
        });
    }

    private void prepareRoomOwnerLeftPopup() {
        Group group = (Group)findActor("ownerLeftPopup");
        Button button = (Button) group.findActor("backToLobbyButton");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToLobby();
            }
        });
    }

    public void backToLobby() {
        game.backToPreviousScreen();
    }

    private void prepareStatsWidgets() {
        Group player = (Group) findActor("player1stats");
        Vector2 pos = labyrinthWidget.getWallPosition(3, 12);
        player.findActor("name").setPosition(pos.x, pos.y);
        pos = labyrinthWidget.getWallPosition(4, 12);
        player.findActor("life").setPosition(pos.x, pos.y);

        player = (Group) findActor("player2stats");
        pos = labyrinthWidget.getWallPosition(7, 12);
        player.findActor("name").setPosition(pos.x, pos.y);
        pos = labyrinthWidget.getWallPosition(8, 12);
        player.findActor("life").setPosition(pos.x, pos.y);

        player = (Group) findActor("player3stats");
        pos = labyrinthWidget.getWallPosition(11, 12);
        player.findActor("name").setPosition(pos.x, pos.y);
        pos = labyrinthWidget.getWallPosition(12, 12);
        player.findActor("life").setPosition(pos.x, pos.y);

        player = (Group) findActor("player4stats");
        pos = labyrinthWidget.getWallPosition(15, 12);
        player.findActor("name").setPosition(pos.x, pos.y);
        pos = labyrinthWidget.getWallPosition(16, 12);
        player.findActor("life").setPosition(pos.x, pos.y);

    }

    public void updateStats() {
        updateStats(1);
        updateStats(2);
        updateStats(3);
        updateStats(4);
    }

    public void updateStats(int playerIndex) {
        Group playerGroup = (Group) findActor("player" + playerIndex + "stats");
        PlayerModel playerModel = world.getPlayerModelAtIndex(playerIndex);
        if (playerModel != null) {
            ((Label)playerGroup.findActor("life")).setText(playerModel.getLifeCount() + "");
        }
    }

    private Group getGameObjectsGroup() {
        return (Group) findActor("gameObjectsGroup");
    }

    private Group getLabyrinthGroup() {
        return (Group) findActor("labyrinth");
    }

    private void prepareSettingsButton() {
        Button settingsButton = findButton("settingsButton");
        settingsButton.setVisible(true);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("settings button clicked.");
                final Group settingsPanel = (Group) findActor("settingsPanel");
                settingsPanel.remove();
                getRoot().addActor(settingsPanel);
                settingsPanel.setVisible( ! settingsPanel.isVisible());
                Button settingsGamePadButton = (Button) settingsPanel.findActor("settingsGamePad");
                settingsGamePadButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        isPreferedControlGamePad = true;
                        findActor("gamePad").setVisible(isPreferedControlGamePad);
                        settingsPanel.setVisible(false);
                    }
                });

                Button settingsSwipeButton = (Button) settingsPanel.findActor("settingsSwipe");
                settingsSwipeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        isPreferedControlGamePad = false;
                        findActor("gamePad").setVisible(isPreferedControlGamePad);
                        settingsPanel.setVisible(false);
                    }
                });
            }
        });
        Vector2 pos = labyrinthWidget.getWallPosition(LabyrinthModel.NUM_COLS-1, LabyrinthModel.NUM_ROWS-1);
        settingsButton.setPosition(pos.x, pos.y);
    }

    private void prepareInputProcessor() {
        GestureDetector swipeGestureDetector = new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean fling(final float velocityX, final float velocityY, int button) {
                if (isPreferedControlGamePad) {
                    return false;
                }
                final String username = UserSession.getInstance().getUsername();
                final PlayerModel playerModel = world.getPlayerModel(username);
                final Direction flingDirection = getDirection(velocityX, velocityY);
                if (previousFlingDirection != null) {
                    onMoveEnd(username, previousFlingDirection);
                    playerModel.setStateChangeListener(new PlayerModel.StateChangeListener() {
                        @Override
                        public void onStateChange(PlayerModel.State newState) {
                            if (newState == PlayerModel.State.STANDING_DOWN
                                    || newState == PlayerModel.State.STANDING_UP
                                    || newState == PlayerModel.State.STANDING_LEFT
                                    || newState == PlayerModel.State.STANDING_RIGHT ) {
                                System.out.println("STATE CHANGED : " + newState);
                                if (previousFlingDirection != null) {
                                    System.out.println("STATE CHANGED sending move start: " + flingDirection);
                                    onMoveStart(username, flingDirection);
                                    playerModel.setStateChangeListener(null);
                                }
                            }
                        }
                    });
                } else {
                    onMoveStart(username, flingDirection);
                }
                previousFlingDirection = flingDirection;
                return true;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                if (previousFlingDirection != null) {
                    onMoveEnd(UserSession.getInstance().getUsername(), previousFlingDirection);
                    previousFlingDirection = null;
                    return true;
                }
                return false;
            }

            private Direction getDirection(float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    //horizontal
                    if (velocityX > 0) {
                        return  Direction.RIGHT;
                    } else {
                        return Direction.LEFT;
                    }
                } else {
                    //vertical
                    if (velocityY > 0) {
                        return Direction.DOWN;
                    } else {
                        return Direction.UP;
                    }
                }
            }
        });
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, swipeGestureDetector);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initializeBeforeGamePanel() {
        Group panel = (Group) findActor("beforeGamePanel");
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
        bombButton.setVisible(false);
        bombButton.setWidth(bombButton.getWidth() * 2f);
        bombButton.setHeight(bombButton.getHeight() * 2f);
        bombButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScreenMediator.onBombButtonClicked();
                game.getAudioManager().dropBomb();
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
        gamePad.setVisible(false);

        findButton("gamePadUpButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(UserSession.getInstance().getUsername(), Direction.UP);
                previousFlingDirection = null;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(UserSession.getInstance().getUsername(), Direction.UP);
            }
        });

        findButton("gamePadDownButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(UserSession.getInstance().getUsername(), Direction.DOWN);
                previousFlingDirection = null;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(UserSession.getInstance().getUsername(), Direction.DOWN);
            }
        });

        findButton("gamePadRightButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(UserSession.getInstance().getUsername(), Direction.RIGHT);
                previousFlingDirection = null;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(UserSession.getInstance().getUsername(), Direction.RIGHT);
            }
        });

        findButton("gamePadLeftButton").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onMoveStart(UserSession.getInstance().getUsername(), Direction.LEFT);
                previousFlingDirection = null;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onMoveEnd(UserSession.getInstance().getUsername(), Direction.LEFT);
            }
        });
    }

    public void onOpponentDropBomb(BombModel bombModel) {
        addBombToScreen(bombModel);
    }

    public void onMoveStart(String username, Direction direction) {
        if (UserSession.getInstance().getUsername().equals(username)) {
            gameScreenMediator.move(direction);
        }
        world.movePlayer(username, direction);
    }

    public void onMoveEnd(String username, Direction direction) {
        if (UserSession.getInstance().getUsername().equals(username)) {
            gameScreenMediator.moveEnd(direction);
        }
        world.stopPlayer(username);
    }

    public void onCreateGame(byte[][] grid, List<GhostModel> ghostModels) {
        findActor("beforeGamePanel").remove();
        labyrinthModel.setGrid(grid);
        world.initialize(labyrinthModel, getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());

        LabyrinthWidget labyrinthWidget = new LabyrinthWidget(world.getLabyrinthModel(), getStageBuilder().getResolutionHelper(), getStageBuilder().getAssets());
        getLabyrinthGroup().addActor(labyrinthWidget);

        world.addGhostModels(ghostModels);

        for (GhostModel ghostModel : world.getGhostModels().values()) {
            ghostModel.setWidth(world.getGridWidth());
            ghostModel.setHeight(world.getGridHeight());
            GhostWidget ghostWidget = new GhostWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), ghostModel);
            getGameObjectsGroup().addActor(ghostWidget);
        }
    }


    public void addBombToScreen(BombModel bombModel) {
        BombWidget bombWidget = new BombWidget(getStageBuilder().getAssets().getTextureAtlas("Common.atlas"), bombModel);
        getGameObjectsGroup().addActor(bombWidget);
    }

    public void renderBombExplosion(BombModel bombModel) {
        game.getAudioManager().boom();
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
        if (!existPlayerOnWorld(playerName)) {
            int gameIndex = world.getNextGameIndex();
            addPlayerModelToWorld(playerName, gameIndex);
            final Group panel = (Group) findActor("beforeGamePanel");
            if (gameIndex == 1) {
                Label label = (Label) panel.findActor("roomOwner");
                label.setText("Player 1 (Room Owner) : " + playerName);
                label.setVisible(true);
            } else {
                Label playerJoinedLabel = (Label) panel.findActor("player" + gameIndex + "joined");
                playerJoinedLabel.setText("Player " + gameIndex + " joined room. " + playerName);
                playerJoinedLabel.setVisible(true);
            }
            if (UserSession.getInstance().isServer()) {
                Button startButton = (Button) panel.findActor("startGameButton");
                startButton.setVisible(true);
                startButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        panel.remove();
                        gameServer.createGame();
                        game.getAudioManager().playStartGame();
                    }
                });

            }
        } else {
            Log.e("Player already in room");
        }
    }

    public void onCurrentPlayerDead() {
        isPreferedControlGamePad = true;
        findActor("gamePad").setVisible(false);
        findActor("bombButton").setVisible(false);
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
        findActor("ownerLeftPopup").setVisible(true);
    }

    public void onDisconnected()  {
        findActor("disconnectPopup").setVisible(true);
    }

    public void onGameFinished() {
        Log.d("Game finished");
    }

    @Override
    public void dispose() {
        super.dispose();
        if (gameScreenMediator.getNetworkListenerAdapter() != null) {
            game.getClient().removeNetworkListener(gameScreenMediator.getNetworkListenerAdapter());
        }
        RoomModel roomModel = UserSession.getInstance().getRoom();
        if (roomModel != null) {
            game.getClient().leaveRoom(roomModel.getId());
            if (UserSession.getInstance().isOwnerRoom()) {
                game.getClient().deleteRoom(roomModel.getId());
            }
        }
        else {
            Log.e("Room Model is NULL. Cannot delete room from Server");
        }
    }

    public void resetPreviousFlingDirection() {
        this.previousFlingDirection = null;
    }

    public void startGame() {
        Actor gamePad = findActor("gamePad");
        gamePad.setVisible(isPreferedControlGamePad);

        Button bombButton = findButton("bombButton");
        bombButton.setVisible(true);

        game.getAudioManager().playStartGame();

    }
}
