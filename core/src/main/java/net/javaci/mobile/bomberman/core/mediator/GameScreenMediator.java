package net.javaci.mobile.bomberman.core.mediator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.GameFactory;
import net.javaci.mobile.bomberman.core.models.BombModel;
import net.javaci.mobile.bomberman.core.models.PlayerModel;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;
import net.javaci.mobile.bomberman.core.net.protocol.*;
import net.javaci.mobile.bomberman.core.server.GameServer;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.util.Log;
import net.javaci.mobile.bomberman.core.view.BomberManScreen;
import net.javaci.mobile.bomberman.core.view.GameScreen;

import java.util.List;

public class GameScreenMediator extends BomberManMediator {

    private GameServer gameServer;
    private NetworkInterface networkInterface;
    private NetworkListenerAdapter networkListenerAdapter;
    private CommandFactory commandFactory = new CommandFactory();
    private GameScreen gameScreen;
    private GameFactory.GameModel gameModel;
    private RoomModel room;
    private int level = 1;

    public GameScreenMediator(BomberManGame game, NetworkInterface networkInterface) {
        super(game);
        this.gameModel = GameFactory.getGameModel(getLevel());
        this.networkInterface = networkInterface;
        this.networkListenerAdapter = new NetworkListenerAdapter() {
            @Override
            public void onMessageReceived(String from, final String message) {
                Gdx.app.postRunnable( new Runnable() {
                    @Override
                    public void run() {
                        Command command = commandFactory.createCommand(message);
                        if (command == null) {
                            Log.d("Waiting split message");
                            return;
                        }
                        switch (command.getCommand()) {
                            case Command.CREATE_GAME:
                                handleCreateGameCommand((CreateGameCommand)command);
                                break;
                            case Command.MOVE_START:
                                handleMoveStartCommand((MoveCommand) command);
                                break;
                            case Command.MOVE_END:
                                handleMoveEndCommand((MoveEndCommand) command);
                                break;
                            case Command.MOVE_GHOST:
                                handleMoveGhostCommand((MoveGhostCommand) command);
                                break;
                            case Command.GAME_END:
                                handleGameEndCommand((GameEndCommand) command);
                                break;
                            case Command.DROP_BOMB:
                                handleDropBombCommand((DropBombCommand) command);
                                break;
                            case Command.EXPLODE_BOMB:
                                handleExplodeBombCommand((ExplodeBombCommand) command);
                                break;
                            case Command.CAUGHT_GHOST:
                                handleGhostCaughtCommand((GhostCaughtCommand) command);
                                break;
                            case Command.START_GAME:
                                handleStartGameCommand((StartGameCommand) command);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }

            @Override
            public void onPlayerLeftRoom(RoomModel room, String playerName) {
                super.onPlayerLeftRoom(room, playerName);
                Log.d("Player : " + playerName + " left Room : " + room.getId());
                if (GameScreenMediator.this.room.equals(room) && !playerName.equals(UserSession.getInstance().getUsername())) {
                    GameScreenMediator.this.onPlayerLeftRoom(playerName);
                }
            }

            @Override
            public void onPlayerJoinedRoom(RoomModel room, String playerName) {
                super.onPlayerJoinedRoom(room, playerName);
                if (GameScreenMediator.this.room.equals(room) && !playerName.equals(UserSession.getInstance().getUsername())) {
                    GameScreenMediator.this.onPlayerJoinedRoom(playerName);
                }
            }

            @Override
            public void onRoomInfoReceived(String[] players, String data) {
                if (players != null) {
                    for (String player: players) {
                        GameScreenMediator.this.onPlayerJoinedRoom(player);
                    }
                }
            }

            @Override
            public void onDisconnected() {
                gameScreen.onDisconnected();
            }
        };
        networkInterface.addNetworkListener(networkListenerAdapter);
    }

    public NetworkListenerAdapter getNetworkListenerAdapter() {
        return networkListenerAdapter;
    }

    private void handleStartGameCommand(StartGameCommand command) {
        gameScreen.startGame();
    }

    private void handleGhostCaughtCommand(GhostCaughtCommand command) {
        List<String> caughtPlayers = command.getCaughtPlayers();
        if (caughtPlayers != null) {
            for (String  caughtPlayer : caughtPlayers) {
                if (gameScreen.getWorld().canRespawn(caughtPlayer)) {
                    PlayerModel playerModel = gameScreen.getWorld().getPlayerModel(caughtPlayer);
                    Vector2 playerInitialPosition = gameScreen.getLabyrinthWidget().getPlayerInitialPosition(playerModel.getGameIndex());
                    gameScreen.getWorld().respawnPlayerAndDecrementLife(caughtPlayer, playerInitialPosition);
                }
                else {
                    if (UserSession.getInstance().isServer() && gameScreen.getWorld().isGameEnd()) {
                        // TODO: Send Game End Command
                        GameEndCommand gameEndCommand = new GameEndCommand();
                        gameEndCommand.setFromUser(UserSession.getInstance().getUsername());
                        gameEndCommand.setReason(GameEndCommand.GameEndReason.GAME_END);
                        networkInterface.sendMessage(gameEndCommand.serialize());
                    }

                    gameScreen.getWorld().killPlayer(caughtPlayer);
                    if (caughtPlayer.equals(UserSession.getInstance().getUsername())) {
                        gameScreen.onCurrentPlayerDead();
                    }
                }
                gameScreen.updateStats();
                gameScreen.resetPreviousFlingDirection();
                if (caughtPlayer.equals(UserSession.getInstance().getUsername())) {
                    game.getAudioManager().playJustDied();
                } else {
                    game.getAudioManager().playOpponentDying();
                }
            }
        }
    }


    @Override
    protected void onScreenShow() {
        super.onScreenShow();
        if ( ! UserSession.getInstance().isServer()) {
            networkInterface.getRoomInfo(UserSession.getInstance().getRoom().getId());
        }
    }

    private void handleExplodeBombCommand(ExplodeBombCommand command) {
        List<String> explodedPlayers = command.getExplodedPlayers();
        if (explodedPlayers != null) {
            for (String explodedPlayer : explodedPlayers) {
                if (gameScreen.getWorld().canRespawn(explodedPlayer)) {
                    PlayerModel playerModel = gameScreen.getWorld().getPlayerModel(explodedPlayer);
                    Vector2 playerInitialPosition = gameScreen.getLabyrinthWidget().getPlayerInitialPosition(playerModel.getGameIndex());
                    gameScreen.getWorld().respawnPlayerAndDecrementLife(explodedPlayer, playerInitialPosition);
                }
                else {
                    if (UserSession.getInstance().isServer() && gameScreen.getWorld().isGameEnd()) {
                        // TODO: Send Game End Command
                        GameEndCommand gameEndCommand = new GameEndCommand();
                        gameEndCommand.setFromUser(UserSession.getInstance().getUsername());
                        gameEndCommand.setReason(GameEndCommand.GameEndReason.GAME_END);
                        networkInterface.sendMessage(gameEndCommand.serialize());
                    }

                    gameScreen.getWorld().killPlayer(explodedPlayer);
                    if (explodedPlayer.equals(UserSession.getInstance().getUsername())) {
                        gameScreen.onCurrentPlayerDead();
                    }
                }
                gameScreen.updateStats();
                gameScreen.resetPreviousFlingDirection();
                if (explodedPlayer.equals(UserSession.getInstance().getUsername())) {
                    game.getAudioManager().playJustDied();
                } else {
                    game.getAudioManager().playOpponentDying();
                }

            }
        }

        List<Integer> explodedGhosts = command.getExplodedGhosts();
        if (explodedGhosts != null) {
            for (Integer ghostId : explodedGhosts) {
                gameScreen.getWorld().killGhost(ghostId);
            }
        }
    }

    private void handleDropBombCommand(DropBombCommand command) {
        if (command.getFromUser().equals(UserSession.getInstance().getUsername())) {
            return;
        }
        BombModel bombModel = gameScreen.getWorld().dropBomb(command.getId(), command.getGridX(), command.getGridY(), command.getFromUser());
        bombModel.addBombListener(new BombModel.BombListener() {
            @Override
            public void onBombExploded(BombModel bombModel) {
                if (UserSession.getInstance().isServer()) {
                    gameServer.sendBombExplosion(bombModel, gameScreen.getWorld());
                }
                gameScreen.renderBombExplosion(bombModel);
            }
        });
        game.getAudioManager().dropBomb();
        gameScreen.onOpponentDropBomb(bombModel);
    }

    private void handleGameEndCommand(GameEndCommand command) {
        networkInterface.leaveRoom(room.getId());

        if (command.getReason() == GameEndCommand.GameEndReason.OWNER_LEFT) {
            game.getClient().deleteRoom(room.getId());
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    gameScreen.onOwnerLeft();
                }
            });
        }
        else if (command.getReason() == GameEndCommand.GameEndReason.GAME_END) {
            gameScreen.onGameFinished();
        }
    }

    private void handleMoveGhostCommand(MoveGhostCommand command) {
        gameScreen.onMoveGhost(command.getId(), command.getGridX(), command.getGridY(), command.getDirection(), command.getDistance());
    }

    private void handleCreateGameCommand(CreateGameCommand command) {
        if (UserSession.getInstance().getUsername().equals(command.getFromUser())) {
            return;
        }

        gameScreen.onCreateGame(command.getGrid(), command.getGhostModels());
    }

    private void handleMoveStartCommand(MoveCommand command) {
        if (UserSession.getInstance().getUsername().equals(command.getFromUser())) {
            return;
        }
        gameScreen.onMoveStart(command.getFromUser(), GameScreen.Direction.valueOf(command.getDirection()));
    }

    private void handleMoveEndCommand(MoveEndCommand command) {
        if (UserSession.getInstance().getUsername().equals(command.getFromUser())) {
            return;
        }
        gameScreen.getWorld().setPlayerTargetPosition(command.getFromUser(), command.getGridX(), command.getGridY());
        gameScreen.onMoveEnd(command.getFromUser(), GameScreen.Direction.valueOf(command.getDirection()));
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    @Override
    public BomberManScreen createScreen() {
        this.screen = new GameScreen(this.game, this);
        this.gameScreen = (GameScreen) screen;
        return screen;
    }

    public void move(GameScreen.Direction direction) {
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setDirection(direction.toString());
        moveCommand.setFromUser(UserSession.getInstance().getUsername());
        Vector2 positionGrid = gameScreen.getWorld().getPlayerGridPosition(UserSession.getInstance().getUsername());
        moveCommand.setGridX((int)positionGrid.x);
        moveCommand.setGridY((int)positionGrid.y);
        networkInterface.sendMessage(moveCommand.serialize());
    }

    public void moveEnd(GameScreen.Direction direction) {
        MoveEndCommand moveEndCommand = new MoveEndCommand();
        moveEndCommand.setDirection(direction.toString());
        moveEndCommand.setFromUser(UserSession.getInstance().getUsername());
        Vector2 targetPosition = gameScreen.getWorld().getTargetGridPosition(UserSession.getInstance().getUsername());
        moveEndCommand.setGridX((int)targetPosition.x);
        moveEndCommand.setGridY((int)targetPosition.y);
        networkInterface.sendMessage(moveEndCommand.serialize());
    }

    public void onBombButtonClicked() {
        //TODO check if player can drop bomb.
        if (gameScreen.getWorld().canUserDropBomb(UserSession.getInstance().getUsername(), gameModel.numBomb)) {
            BombModel bombModel = gameScreen.getWorld().playerDroppedBomb(UserSession.getInstance().getUsername());
            bombModel.addBombListener(new BombModel.BombListener() {
                @Override
                public void onBombExploded(BombModel bombModel) {
                    if (UserSession.getInstance().isServer()) {
                        gameServer.sendBombExplosion(bombModel, gameScreen.getWorld());
                    }
                    gameScreen.renderBombExplosion(bombModel);
                }
            });
            DropBombCommand dropBombCommand = new DropBombCommand();
            dropBombCommand.setFromUser(UserSession.getInstance().getUsername());
            dropBombCommand.setId(bombModel.getId());
            dropBombCommand.setGridX(bombModel.getGridX());
            dropBombCommand.setGridY(bombModel.getGridY());
            game.getClient().sendMessage(dropBombCommand.serialize());
            gameScreen.addBombToScreen(bombModel);
        }
    }

    public void onPlayerLeftRoom(String playerName) {
        if (playerName != null && playerName.equals(room.getOwner())) {
            onGameOwnerLeft();
        }
        else {
            // TODO : remove player character from stage
        }
    }

    public void onPlayerJoinedRoom(String playerName) {
        gameScreen.onPlayerJoinedRoom(playerName);
    }

    private void onGameOwnerLeft() {
        GameEndCommand gameEndCommand = new GameEndCommand();
        gameEndCommand.setFromUser(UserSession.getInstance().getUsername());
        gameEndCommand.setReason(GameEndCommand.GameEndReason.OWNER_LEFT);
        networkInterface.sendMessage(gameEndCommand.serialize());
    }

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
