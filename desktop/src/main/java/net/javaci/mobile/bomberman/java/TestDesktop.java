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
        config.width = 800;
        config.height = 480;
		config.useGL20 = true;

        BomberManGame game = new BomberManGame();
        game.initialize(config.width, config.height);

		new LwjglApplication(game, config);

//        AppWarpClient client = new AppWarpClient("ilkinulas");
//        client.connect(new NetworkInterface.ConnectionListener() {
//            @Override
//            public void onConnected() {
//                System.out.println("on connected");
//            }
//
//            @Override
//            public void onDisconnected() {
//                System.out.println("on disconnected");
//            }
//
//            @Override
//            public void onConnectionFailure(Exception e) {
//                e.printStackTrace();
//                System.out.println("on connection failure");
//            }
//        });
	}
}
