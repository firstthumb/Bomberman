package net.javaci.mobile.bomberman.html;

import net.javaci.mobile.bomberman.core.Test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class TestHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new Test();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}
