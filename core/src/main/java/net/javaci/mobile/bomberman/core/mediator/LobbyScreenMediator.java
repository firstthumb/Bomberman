package net.javaci.mobile.bomberman.core.mediator;

import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.view.BomberManScreen;
import net.javaci.mobile.bomberman.core.view.LobbyScreen;

public class LobbyScreenMediator extends BomberManMediator {

    private LobbyScreen lobbyScreen;

    public LobbyScreenMediator(BomberManGame game) {
        super(game);
    }

    @Override
    public BomberManScreen createScreen() {
        this.screen = new LobbyScreen(this.game, this);
        this.lobbyScreen = (LobbyScreen) this.screen;
        return screen;
    }

    @Override
    protected void onScreenShow() {
        super.onScreenShow();

    }
}
