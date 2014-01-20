package net.javaci.mobile.bomberman.core.server;

import net.javaci.mobile.bomberman.core.mediator.GameScreenMediator;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.appwarp.AppWarpClient;
import net.javaci.mobile.bomberman.core.net.protocol.Command;
import net.javaci.mobile.bomberman.core.net.protocol.CommandFactory;
import net.javaci.mobile.bomberman.core.net.protocol.MoveCommand;
import net.javaci.mobile.bomberman.core.net.protocol.StartGameCommand;

public class GameServer {
    private NetworkInterface networkInterface;
    private GameScreenMediator gameScreenMediator;
    private CommandFactory commandFactory = new CommandFactory();

    public void initialize(AppWarpClient networkInterface, GameScreenMediator gameScreenMediator) {
        this.networkInterface = networkInterface;
        this.gameScreenMediator = gameScreenMediator;

        this.networkInterface.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onMessageReceived(String from, String message) {
                Command command = commandFactory.createCommand(message);
                switch (command.getCommand()) {
                    case Command.MOVE_START:
                        handleStartMoveCommand((MoveCommand) command);
                        break;
                    default:
                        break;
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


}
