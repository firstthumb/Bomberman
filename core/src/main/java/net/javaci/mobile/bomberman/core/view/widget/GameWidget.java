package net.javaci.mobile.bomberman.core.view.widget;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class GameWidget extends WidgetGroup {

    public GameWidget(Skin skin) {
        super();

        Label roomName = new Label("Room", skin);
        roomName.setPosition(40, 21.5f);
        roomName.setAlignment(Align.left);
        this.addActor(roomName);
    }
}
