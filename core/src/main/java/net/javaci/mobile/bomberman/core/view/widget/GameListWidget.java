package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class GameListWidget extends WidgetGroup {
    private ScrollPane scrollPane;
    private Table table;

    private Skin skin;

    public GameListWidget() {
        super();

//        skin = new Skin(Gdx.files.internal("images/800x480/uiskin.json"));

        scrollPane = new ScrollPane(generateTable());
        scrollPane.setPosition(200, 200);
        table.add(scrollPane).height(215).padBottom(30);
    }

    private Table generateTable() {
        Table itemTable = new Table();
//        Image genericItem = new Image(new Texture(Gdx.files.internal("data/ArkadaslarimEdit1.png")));
        itemTable.setSize(100, 215);

        for (int i = 0; i < 5; i++) {
            itemTable.row();

            GameWidget widget;
            if(i%2==0){
                widget = new GameWidget(skin);
            }else{
                widget = new GameWidget(skin);
            }
            itemTable.add(widget).width(widget.getWidth()).height(widget.getHeight());
        }

        return itemTable;
    }
}
