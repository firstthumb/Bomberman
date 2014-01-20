package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.esotericsoftware.tablelayout.Cell;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;
import net.javaci.mobile.bomberman.core.view.widget.GameListWidgetConfig;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;

import java.util.List;

public class LobbyScreen extends BomberManScreen {

    private Table gameTable;
    private GameListWidgetConfig config;
    private NetworkListenerAdapter networkListenerAdapter;

    public LobbyScreen(AbstractGame game, BomberManMediator mediator) {
        super(game, mediator);

        config = (GameListWidgetConfig)findActor("gameListPanelWidgetConfiguration");
        initialize();
    }

    public void initialize() {
        gameTable = new Table();
        ((TextButton)findActor("quitButton")).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        initializeGameList();
        networkListenerAdapter = new NetworkListenerAdapter() {
            @Override
            public void onConnected() {
                game.getClient().listRooms();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onRoomListReceived(List<RoomModel> rooms) {
                reloadGameListPanel(rooms);
                removeLoadingWidget();
            }

            @Override
            public void onRoomListRequestFailed() {

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
                System.out.println("Clicked : " + model.getName());
            }
        });

        return group;
    }
}
