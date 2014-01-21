package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import net.javaci.mobile.bomberman.core.models.LabyrinthModel;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;

public class LabyrinthWidget extends Actor {
    private LabyrinthModel labyrinthModel;
    private ResolutionHelper resolutionHelper;
    private AssetsInterface assets;
    private Vector2 gameAreaBounds;
    private Vector2 gameAreaPosition;
    private TextureRegion wall;
    private TextureRegion brick;
    private int numCols;
    private int numRows;

    public LabyrinthWidget(LabyrinthModel labyrinthModel, ResolutionHelper resolutionHelper, AssetsInterface assets) {
        this.labyrinthModel = labyrinthModel;
        this.resolutionHelper = resolutionHelper;
        this.assets = assets;
        this.gameAreaBounds = resolutionHelper.getGameAreaBounds();
        this.gameAreaPosition = resolutionHelper.getGameAreaPosition();
        this.numCols = labyrinthModel.getGrid().length;
        this.numRows =  labyrinthModel.getGrid()[0].length;
        TextureAtlas atlas = assets.getTextureAtlas("Common.atlas");
        wall = atlas.findRegion("wall");
        brick = atlas.findRegion("brick");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float width = gameAreaBounds.x / numCols;
        float height = gameAreaBounds.y / numRows;
        drawWalls(batch, width, height);
    }

    private void drawWalls(Batch batch, float width, float height) {
        for (int i = 0; i < numCols; i++) {
            float x = width * i + gameAreaPosition.x;
            for (int j = 0; j < numRows; j++) {
                float y = j * height + gameAreaPosition.y;
                if (labyrinthModel.getGrid()[i][j] == LabyrinthModel.WALL) {
                    batch.draw(wall, x, y, width, height);
                } else if (labyrinthModel.getGrid()[i][j] == LabyrinthModel.BRICK) {
                    batch.draw(brick, x, y, width, height);
                }
            }
        }
    }

    public Vector2 getPlayerInitialPosition(int playerIndex) {
        float width = gameAreaBounds.x / numCols;
        float height = gameAreaBounds.y / numRows;
        Vector2 position = new Vector2();
        switch (playerIndex) {
            case 1:
                position.set(gameAreaPosition.x + width, gameAreaPosition.y + height);
                break;
            case 2:
                //TODO position.set(gameAreaPosition.x + width, gameAreaPosition.y + height);
                break;
            case 3:
                //TODO position.set(gameAreaPosition.x + width, gameAreaPosition.y + height);
                break;
            case 4:
                //TODO position.set(gameAreaPosition.x + width, gameAreaPosition.y + height);
                break;
        }
        return position;
    }

}
