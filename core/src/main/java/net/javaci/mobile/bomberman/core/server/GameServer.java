package net.javaci.mobile.bomberman.core.server;

import net.javaci.mobile.bomberman.core.World;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.GhostMovement;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.protocol.*;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.view.GameScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    private static int WAIT_MOVE_GHOST_IN_SECOND = 2;
    private static Map<Integer, GhostMovement> ghostMovements = new HashMap<Integer, GhostMovement>();

    static {
        ghostMovements.put(0, new GhostMovement(GameScreen.Direction.RIGHT, 2));
        ghostMovements.put(1, new GhostMovement(GameScreen.Direction.LEFT, 2));
        ghostMovements.put(2, new GhostMovement(GameScreen.Direction.UP, 2));
        ghostMovements.put(3, new GhostMovement(GameScreen.Direction.DOWN, 2));
    }

    private int ghostMoveIndex = 0;

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
    }

    private void handleStartMoveCommand(MoveCommand command) {

    }

    public void startGame() {
        StartGameCommand startGameCommand = new StartGameCommand();
        networkInterface.sendMessage(startGameCommand.serialize());
    }

    public void createGame() {
//        CreateGameCommand createGameCommand = new CreateGameCommand();
//        createGameCommand.setFromUser(UserSession.getInstance().getUsername());
//        createGameCommand.setGhostModels(new ArrayList<GhostModel>(world.getGhostModels().values()));
//        createGameCommand.setLabyrinthModel(world.getLabyrinthModel());
//        String serializedMessage = createGameCommand.serialize();
//        for (String message : createGameCommand.splitMessage(serializedMessage)) {
//            networkInterface.sendMessage(message);
//        }

        GhostMovement movement = getGhostMovement();

        MoveGhostCommand command = new MoveGhostCommand();
        command.setFromUser(UserSession.getInstance().getUsername());
        command.setId(1);
        command.setGridX(1);
        command.setGridY(1);
        command.setDirection(movement.getDirection().toString());
        command.setDistance(movement.getDistance());
        networkInterface.sendMessage(command.serialize());
    }
    public void moveGhost(final int ghostId) {
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                GhostModel ghostModel = world.getGhostModels().get(ghostId);
                if (ghostModel == null) {
                    return;
                }

                GhostMovement movement = getGhostMovement();

                MoveGhostCommand command = new MoveGhostCommand();
                command.setFromUser(UserSession.getInstance().getUsername());
                command.setId(1);
                command.setGridX(ghostModel.getGridX());
                command.setGridY(ghostModel.getGridY());
                command.setDirection(movement.getDirection().toString());
                command.setDistance(movement.getDistance());
                networkInterface.sendMessage(command.serialize());
            }
        }, WAIT_MOVE_GHOST_IN_SECOND, TimeUnit.SECONDS);
    }

    private GhostMovement getGhostMovement() {
        GhostMovement movement = ghostMovements.get(ghostMoveIndex++);
        if (movement == null) {
            ghostMoveIndex = 0;
            return getGhostMovement();
        }

        return movement;
    }


}
