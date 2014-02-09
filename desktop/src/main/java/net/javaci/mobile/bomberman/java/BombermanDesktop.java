package net.javaci.mobile.bomberman.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.javaci.mobile.bomberman.core.BomberManGame;

public class BombermanDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 480;
		config.useGL20 = true;

        BomberManGame game = new BomberManGame();
        game.initialize(config.width, config.height);

		new LwjglApplication(game, config);
	}
}
