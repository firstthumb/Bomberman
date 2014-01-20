package net.javaci.mobile.bomberman.core.view;

import com.badlogic.gdx.scenes.scene2d.Group;
import net.javaci.mobile.bomberman.core.mediator.BomberManMediator;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;

public class LobbyScreen extends BomberManScreen {

    public LobbyScreen(AbstractGame game, BomberManMediator mediator) {
        super(game, mediator);

        initialize();
    }

    public void initialize() {
        initializeGameList();
    }

    public void initializeGameList() {
        Group gameListPanel = (Group) findActor("gameList");


    }
}
