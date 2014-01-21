package net.javaci.mobile.bomberman.core.mediator;

import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.protocol.*;
import net.javaci.mobile.bomberman.core.server.GameServer;
import net.javaci.mobile.bomberman.core.util.Log;
import net.javaci.mobile.bomberman.core.view.BomberManScreen;
import net.javaci.mobile.bomberman.core.view.GameScreen;

public class GameScreenMediator extends BomberManMediator {

    private GameServer gameServer;
    private NetworkInterface networkInterface;
    private CommandFactory commandFactory = new CommandFactory();
    private GameScreen gameScreen;

    public GameScreenMediator(BomberManGame game, NetworkInterface networkInterface) {
        super(game);
        this.networkInterface = networkInterface;
        networkInterface.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onMessageReceived(String from, String message) {
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

                    default:
                        break;
                }
            }
        });
    }

    private void handleCreateGameCommand(CreateGameCommand command) {
        if (BomberManGame.username.equals(command.getFromUser())) {
            return;
        }

        gameScreen.onCreateGame(command.getLabyrinthModel(), command.getGhostModels());
    }

    private void handleMoveStartCommand(MoveCommand command) {
        if (BomberManGame.username.equals(command.getFromUser())) {
            return;
        }
        gameScreen.onMoveStart(command.getFromUser(), GameScreen.Direction.valueOf(command.getDirection()));
    }

    private void handleMoveEndCommand(MoveEndCommand command) {
        if (BomberManGame.username.equals(command.getFromUser())) {
            return;
        }
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
        moveCommand.setFromUser(BomberManGame.username);
        networkInterface.sendMessage(moveCommand.serialize());
    }

    public void moveEnd(GameScreen.Direction direction) {
        MoveEndCommand moveEndCommand = new MoveEndCommand();
        moveEndCommand.setDirection(direction.toString());
        moveEndCommand.setFromUser(BomberManGame.username);
        networkInterface.sendMessage(moveEndCommand.serialize());
    }
}
