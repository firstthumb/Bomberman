package net.javaci.mobile.bomberman.core.mediator;

import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.view.BomberManScreen;
import net.javaci.mobile.bomberman.core.view.GameScreen;

public class GameScreenMediator extends BomberManMediator {

    public GameScreenMediator(BomberManGame game) {
        super(game);
    }

    @Override
    public BomberManScreen createScreen() {
        this.screen = new GameScreen(this.game, this);
        return screen;
    }
}
