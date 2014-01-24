package net.javaci.mobile.bomberman.core.server;

import net.javaci.mobile.bomberman.core.GameFactory;
import net.javaci.mobile.bomberman.core.World;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.models.BombModel;
import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.GhostMovement;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.protocol.*;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.util.Log;
import net.javaci.mobile.bomberman.core.view.GameScreen;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private ScheduledExecutorService executorService;
    private boolean isGameStarted = false;
    private boolean isDisposed = false;

    private static int WAIT_MOVE_GHOST_IN_SECOND = 3;
    private static Map<Integer, GhostMovement> ghostMovements = new HashMap<Integer, GhostMovement>();

    static {
        int count = 0;
        for (int i=0; i<4; i++) {
            switch (i) {
                case 0:
                    for (int j=1; j<10; j++) {
                        ghostMovements.put(count++, new GhostMovement(GameScreen.Direction.UP, j));
                    }
                    break;
                case 1:
                    for (int j=1; j<10; j++) {
                        ghostMovements.put(count++, new GhostMovement(GameScreen.Direction.DOWN, j));
                    }
                    break;
                case 2:
                    for (int j=1; j<10; j++) {
                        ghostMovements.put(count++, new GhostMovement(GameScreen.Direction.RIGHT, j));
                    }
                    break;
                case 3:
                    for (int j=1; j<10; j++) {
                        ghostMovements.put(count++, new GhostMovement(GameScreen.Direction.LEFT, j));
                    }
                    break;

            }

        }
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
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isDisposed && isGameStarted) {
                    int waitingGhosts = 0;
                    for (GhostModel ghostModel : GameServer.this.world.getGhostModels().values().toArray(new GhostModel[0])) {
                        Log.d("Ghost State : " + ghostModel.getState());
                        if (ghostModel.getState() == GhostModel.State.STANDING_DOWN || ghostModel.getState() == GhostModel.State.STANDING_UP || ghostModel.getState() == GhostModel.State.STANDING_LEFT || ghostModel.getState() == GhostModel.State.STANDING_RIGHT) {
                            waitingGhosts++;
                            moveGhost(ghostModel.getId());
                        }
                    }
                    Log.d("Total Waiting Ghosts : " + waitingGhosts);
                }
            }
        }, WAIT_MOVE_GHOST_IN_SECOND, WAIT_MOVE_GHOST_IN_SECOND, TimeUnit.SECONDS);
    }

    private void handleStartMoveCommand(MoveCommand command) {

    }

    public void startGame() {
        StartGameCommand startGameCommand = new StartGameCommand();
        startGameCommand.setFromUser(UserSession.getInstance().getUsername());
        networkInterface.sendMessage(startGameCommand.serialize());

        for (GhostModel ghostModel : world.getGhostModels().values()) {
            moveGhost(ghostModel.getId());
        }

        isGameStarted = true;
    }

    public void createGame() {
        CreateGameCommand createGameCommand = new CreateGameCommand();
        createGameCommand.setFromUser(UserSession.getInstance().getUsername());
        createGameCommand.setGhostModels(new ArrayList<GhostModel>(world.getGhostModels().values()));
        createGameCommand.setGrid(world.getLabyrinthModel().getGrid());
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
                }
                while (numTry > 0 && movement != null && !movement.movable(ghostModel, world.getLabyrinthModel().getGrid()));

                MoveGhostCommand command = new MoveGhostCommand();
                command.setFromUser(UserSession.getInstance().getUsername());
                command.setId(ghostId);
                command.setGridX(ghostModel.getGridX());
                command.setGridY(ghostModel.getGridY());
                command.setDirection(movement.getDirection().toString());
                command.setDistance(movement.getDistance());
                networkInterface.sendMessage(command.serialize());
            }
        }, rand.nextInt(WAIT_MOVE_GHOST_IN_SECOND), TimeUnit.SECONDS);
    }

    private GhostMovement getGhostMovement() {
        return ghostMovements.get(rand.nextInt(ghostMovements.size()));
    }

    public void sendBombExplosion(BombModel bombModel, World world) {
        ExplodeBombCommand explodeBombCommand = new ExplodeBombCommand();
        explodeBombCommand.setFromUser(UserSession.getInstance().getUsername());
        explodeBombCommand.setId(bombModel.getId());
        explodeBombCommand.setGridX(bombModel.getGridX());
        explodeBombCommand.setGridY(bombModel.getGridY());
        List<String> explodedPlayers = world.getExplodedPlayerNames(bombModel);
        explodeBombCommand.setExplodedPlayers(explodedPlayers);
        List<Integer> explodedGhosts = world.getExplodedGhosts(bombModel);
        explodeBombCommand.setExplodedGhosts(explodedGhosts);
        networkInterface.sendMessage(explodeBombCommand.serialize());
    }

    public void caughtPlayer(int ghostId, List<String> players) {
        GhostCaughtCommand ghostCaughtCommand = new GhostCaughtCommand();
        ghostCaughtCommand.setFromUser(UserSession.getInstance().getUsername());
        ghostCaughtCommand.setId(ghostId);
        ghostCaughtCommand.setCaughtPlayers(players);
        networkInterface.sendMessage(ghostCaughtCommand.serialize());
    }

    public void dispose() {
        isDisposed = true;
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
