package net.javaci.mobile.bomberman.core.server;

import net.javaci.mobile.bomberman.core.World;
import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.appwarp.AppWarpClient;
import net.javaci.mobile.bomberman.core.net.protocol.*;
import net.javaci.mobile.bomberman.core.session.UserSession;

import java.util.ArrayList;

public class GameServer {
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
        CreateGameCommand createGameCommand = new CreateGameCommand();
        createGameCommand.setFromUser(UserSession.getInstance().getUsername());
        createGameCommand.setGhostModels(new ArrayList<GhostModel>(world.getGhostModels().values()));
        createGameCommand.setLabyrinthModel(world.getLabyrinthModel());
        String serializedMessage = createGameCommand.serialize();
        for (String message : createGameCommand.splitMessage(serializedMessage)) {
            networkInterface.sendMessage(message);
        }
    }
}
