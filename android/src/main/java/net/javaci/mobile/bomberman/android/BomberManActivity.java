package net.javaci.mobile.bomberman.android;

import android.os.Bundle;
import android.util.DisplayMetrics;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import net.javaci.mobile.bomberman.core.BomberManGame;

public class BomberManActivity extends AndroidApplication {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useGL20 = true;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        BomberManGame game = new BomberManGame();
        game.initialize(metrics.widthPixels, metrics.heightPixels);

        initialize(game, config);
    }
}
