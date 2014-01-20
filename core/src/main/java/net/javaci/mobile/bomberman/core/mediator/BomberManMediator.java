package net.javaci.mobile.bomberman.core.mediator;

import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.view.BomberManScreen;

public abstract class BomberManMediator {

    protected BomberManGame game;
    protected BomberManScreen screen;

    public BomberManMediator(BomberManGame game) {
        this.game = game;
    }

    public abstract BomberManScreen createScreen();

    public final void onScreenShowInternal() {
        onScreenShow();
    }

    /**
     * Override this method if you want to be notified when the screen is shown.
     */
    protected void onScreenShow() {}


}
