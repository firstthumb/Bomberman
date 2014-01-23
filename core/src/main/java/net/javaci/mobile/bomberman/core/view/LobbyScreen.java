package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.javaci.mobile.bomberman.core.mediator.LobbyScreenMediator;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.util.Log;
import net.javaci.mobile.bomberman.core.view.widget.GameListWidgetConfig;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;

import java.util.List;

public class LobbyScreen extends BomberManScreen {
    private static float PERIODIC_REQUEST_INTERVAL = 10;

    private Table gameTable;
    private GameListWidgetConfig config;
    private NetworkListenerAdapter networkListenerAdapter;
    private LobbyScreenMediator lobbyScreenMediator;
    private float periodicRequestTimeCounter = 0;

    public LobbyScreen(AbstractGame game, BomberManMediator mediator) {
        super(game, mediator);

        this.lobbyScreenMediator = ((LobbyScreenMediator)mediator);

        config = (GameListWidgetConfig)findActor("gameListPanelWidgetConfiguration");
        initialize();
    }

    public void initialize() {
        gameTable = new Table();
        ((TextButton)findActor("createButton")).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createNewGame();
            }
        });
        (((Group)findActor("connectionErrorPopup")).findActor("reconnectButton")).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                displayLoadingWidget();
                game.getClient().connect();
//                game.getClient().addNetworkListener(networkListenerAdapter);
                Group popup = (Group) findActor("connectionErrorPopup");
                if (popup != null) {
                    popup.setVisible(false);
                }
            }
        });
        initializeGameList();
        networkListenerAdapter = new NetworkListenerAdapter() {
            @Override
            public void onConnected() {
                updateUserInfo();

                game.getClient().listRooms();
            }

            @Override
            public void onDisconnected() {
                removeLoadingWidget();
                onConnectionError();
            }

            @Override
            public void onRoomListReceived(List<RoomModel> rooms) {
                reloadGameListPanel(rooms);
                removeLoadingWidget();
            }

            @Override
            public void onRoomListRequestFailed() {
                Log.d("Room list failed");
                removeLoadingWidget();
            }

            @Override
            public void onJoinRoomSuccess(String roomId) {
                Log.d("Joined room successfully");
                removeLoadingWidget();
                game.switchScreen(BomberManGame.ScreenType.PLAY, null);
            }

            @Override
            public void onJoinRoomFailed() {
                Log.d("Failed to join room");
                UserSession.getInstance().setRoom(null);
                removeLoadingWidget();
            }

            @Override
            public void onRoomCreated(RoomModel room) {
                Log.d("Created room successfully. Room : " + room.getName());
                UserSession.getInstance().setRoom(room);
                game.getClient().joinRoom(room.getId());
            }

            @Override
            public void onCreateRoomFailed() {
                Log.d("Create room failed");
                removeLoadingWidget();
            }
        };
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        sendPeriodicRequestsIfRequired(delta);
    }

    private void sendPeriodicRequestsIfRequired(float delta) {
        periodicRequestTimeCounter += delta;
        if(periodicRequestTimeCounter >= PERIODIC_REQUEST_INTERVAL) {
            game.getClient().listRooms();
            periodicRequestTimeCounter = 0;
        }
    }

    @Override
    public void show() {
        super.show();

        displayLoadingWidget();
        game.getClient().connect();
        game.getClient().addNetworkListener(networkListenerAdapter);
    }

    @Override
    public void dispose() {
        super.dispose();
        game.getClient().removeNetworkListener(networkListenerAdapter);
    }

    private void createNewGame() {
        displayLoadingWidget();
        // TODO : create random room name using player name
        game.getClient().createRoom(UserSession.getInstance().getUsername() + "_Room");
    }

    private void joinGame(RoomModel model) {
        displayLoadingWidget();
        UserSession.getInstance().setRoom(model);
        game.getClient().joinRoom(model.getId());
    }

    private void reloadGameListPanel(List<RoomModel> rooms) {
        gameTable.clearChildren();
        if (rooms != null) {
            for (RoomModel roomModel : rooms) {
                try {
                    Group group = createGameListItem(roomModel);
                    gameTable.row();
                    gameTable.add(group)
                            .width(config.getPositionMultFloat("rowWidth"))
                            .height(config.getPositionMultFloat("rowHeight"));
                } catch (Exception exc) {}
            }
        }
    }

    public void initializeGameList() {
        Group gameList = (Group) findActor("gameList");
        gameTable.align(Align.top);
        gameTable.setSize(100, 100);

        ScrollPane scrollPane = new ScrollPane(this.gameTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setSize(
                config.getPositionMultFloat("width"),
                config.getPositionMultFloat("height"));
        scrollPane.setPosition(
                config.getPositionMultFloat("x"),
                config.getPositionMultFloat("y"));
        gameList.addActor(scrollPane);
    }

    public Group createGameListItem(final RoomModel model) throws Exception {
        Group group = getStageBuilder().buildGroup("GameListItem.xml");

        final Label label = ((Label)group.findActor("roomName"));
        label.setText(model.getName());
        final TextButton button = ((TextButton)group.findActor("joinGameButton"));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                log("Join Room : " + model.getName());
                joinGame(model);
            }
        });

        return group;
    }

    public void updateUserInfo() {
        ((Label)findActor("usernameLabel")).setText(UserSession.getInstance().getUsername());
    }

    public void onConnectionError() {
        Group popup = (Group) findActor("connectionErrorPopup");
        if (popup != null) {
            popup.setVisible(true);
        }
    }
}
