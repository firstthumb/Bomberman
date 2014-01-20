package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.Gdx;
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

    private Table gameTable;
    private GameListWidgetConfig config;
    private NetworkListenerAdapter networkListenerAdapter;
    private LobbyScreenMediator lobbyScreenMediator;

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
        initializeGameList();
        networkListenerAdapter = new NetworkListenerAdapter() {
            @Override
            public void onConnected() {
                updateUserInfo();

                game.getClient().listRooms();
            }

            @Override
            public void onDisconnected() {
                Log.d("Disconnected");
                removeLoadingWidget();
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
                removeLoadingWidget();
            }

            @Override
            public void onRoomCreated(RoomModel room) {
                Log.d("Created room successfully. Room : " + room.getName());
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
        label.setText("Room : " + model.getName());
        final TextButton button = ((TextButton)group.findActor("joinGameButton"));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                log("Join Room : " + model.getName());
                displayLoadingWidget();
                game.getClient().joinRoom(model.getId());
            }
        });

        return group;
    }

    public void updateUserInfo() {
        ((Label)findActor("usernameLabel")).setText(UserSession.getInstance().getUsername());
    }
}
