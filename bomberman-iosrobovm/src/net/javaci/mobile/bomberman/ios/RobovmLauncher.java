package net.javaci.mobile.bomberman.ios;

import net.javaci.mobile.bomberman.core.BomberManGame;

import org.robovm.cocoatouch.foundation.NSAutoreleasePool;
import org.robovm.cocoatouch.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;


public class RobovmLauncher extends IOSApplication.Delegate {

	@Override
	protected IOSApplication createApplication() {
		IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		config.orientationLandscape = true;
		config.orientationPortrait = false;

		BomberManGame game = new BomberManGame();
        //game.initialize(960, 640); //iphone 3gs
        game.initialize(1136, 640); //iphone 5
        //game.initialize(2048, 1536); //ipad retina
        
		return new IOSApplication(game, config);
	}
	
	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, RobovmLauncher.class);
		pool.drain();
	}

}
