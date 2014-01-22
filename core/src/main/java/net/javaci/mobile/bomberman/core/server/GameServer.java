package net.javaci.mobile.bomberman.core.server;

import net.javaci.mobile.bomberman.core.GameFactory;
import net.javaci.mobile.bomberman.core.World;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.GhostMovement;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.protocol.*;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.util.Log;
import net.javaci.mobile.bomberman.core.view.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private ScheduledExecutorService executorService;

    private static int WAIT_MOVE_GHOST_IN_SECOND = 5;
    private static Map<Integer, GhostMovement> ghostMovements = new HashMap<Integer, GhostMovement>();

    static {
        ghostMovements.put(0, new GhostMovement(GameScreen.Direction.RIGHT, 8));
        ghostMovements.put(1, new GhostMovement(GameScreen.Direction.LEFT, 5));
        ghostMovements.put(2, new GhostMovement(GameScreen.Direction.UP, 2));
        ghostMovements.put(3, new GhostMovement(GameScreen.Direction.LEFT, 7));
        ghostMovements.put(4, new GhostMovement(GameScreen.Direction.RIGHT, 3));
        ghostMovements.put(5, new GhostMovement(GameScreen.Direction.UP, 10));
        ghostMovements.put(6, new GhostMovement(GameScreen.Direction.RIGHT, 11));
        ghostMovements.put(7, new GhostMovement(GameScreen.Direction.LEFT, 3));
        ghostMovements.put(8, new GhostMovement(GameScreen.Direction.UP, 4));
        ghostMovements.put(9, new GhostMovement(GameScreen.Direction.DOWN, 6));
        ghostMovements.put(10, new GhostMovement(GameScreen.Direction.RIGHT, 8));
    }

    private Random rand = new Random();

    private NetworkInterface networkInterface;
    private GameScreenMediator gameScreenMediator;
    private CommandFactory commandFactory = new CommandFactory();
    private World world;

    public GameServer(World world) {
        this.world = world;
    }

    public void initialize(NetworkInterface networkInterface, GameScreenMediator gameScreenMediator) {
        this.networkInterface = networkInterface;
        this.gameScreenMediator = gameScreenMediator;

        this.networkInterface.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onMessageReceived(String from, String message) {
                Command command = commandFactory.createCommand(message);
                if (command != null) {
                    switch (command.getCommand()) {
                        case Command.MOVE_START:
                            handleStartMoveCommand((MoveCommand) command);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        GameFactory.GameModel gameModel = GameFactory.getGameModel(gameScreenMediator.getLevel());
        executorService = Executors.newScheduledThreadPool(gameModel.numGhosts);
    }

    private void handleStartMoveCommand(MoveCommand command) {

    }

    public void startGame() {
        StartGameCommand startGameCommand = new StartGameCommand();
        networkInterface.sendMessage(startGameCommand.serialize());

        for (GhostModel ghostModel : world.getGhostModels().values()) {
            moveGhost(ghostModel.getId());
        }
    }

    public void createGame() {
        CreateGameCommand createGameCommand = new CreateGameCommand();
        createGameCommand.setFromUser(UserSession.getInstance().getUsername());
        createGameCommand.setGhostModels(new ArrayList<GhostModel>(world.getGhostModels().values()));
        createGameCommand.setLabyrinthModel(world.getLabyrinthModel());
        String serializedMessage = createGameCommand.serialize();
        for (String message : createGameCommand.splitMessage(serializedMessage)) {
            networkInterface.sendMessage(message);
        }

        startGame();
    }

    public void moveGhost(final int ghostId) {
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                GhostModel ghostModel = world.getGhostModels().get(ghostId);
                if (ghostModel == null) {
                    return;
                }

                int numTry = 3;
                GhostMovement movement;
                do {
                    movement = getGhostMovement();
                    numTry--;
                } while (numTry > 0 && movement != null && !movement.movable(ghostModel, world.getLabyrinthModel().getGrid()));

                Log.e("Movable  : " + movement.movable(ghostModel, world.getLabyrinthModel().getGrid()));

                MoveGhostCommand command = new MoveGhostCommand();
                command.setFromUser(UserSession.getInstance().getUsername());
                command.setId(ghostId);
                command.setGridX(ghostModel.getGridX());
                command.setGridY(ghostModel.getGridY());
                command.setDirection(movement.getDirection().toString());
                command.setDistance(movement.getDistance());
                networkInterface.sendMessage(command.serialize());
            }
        }, WAIT_MOVE_GHOST_IN_SECOND, TimeUnit.SECONDS);
    }

    private GhostMovement getGhostMovement() {
        return ghostMovements.get(rand.nextInt(ghostMovements.size()));
    }
}
