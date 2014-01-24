package net.javaci.mobile.bomberman.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.javaci.mobile.bomberman.core.BomberManGame;
import net.javaci.mobile.bomberman.core.Test;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.appwarp.AppWarpClient;

public class TestDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
		config.useGL20 = true;

        BomberManGame game = new BomberManGame();
        game.initialize(config.width, config.height);

		new LwjglApplication(game, config);
	}
}
