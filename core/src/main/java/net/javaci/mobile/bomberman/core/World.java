package net.javaci.mobile.bomberman.core;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.javaci.mobile.bomberman.core.models.BombModel;
import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.LabyrinthModel;
import net.javaci.mobile.bomberman.core.models.PlayerModel;
import net.javaci.mobile.bomberman.core.session.UserSession;
import net.javaci.mobile.bomberman.core.util.Log;
import net.javaci.mobile.bomberman.core.view.GameScreen;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World implements BombModel.BombListener {
    private Map<String, PlayerModel> playerModels = new HashMap<String, PlayerModel>();
    private LabyrinthModel labyrinthModel;
    private ResolutionHelper resolutionHelper;
    private Map<Integer, GhostModel> ghostModels = new HashMap<Integer, GhostModel>();
    private List<BombModel> bombList = new ArrayList<BombModel>();
    private AssetsInterface assetsInterface;

    public void setLabyrinthModel(LabyrinthModel labyrinthModel) {
        this.labyrinthModel = labyrinthModel;
    }

    public void update(float deltaTime) {
        updatePlayerModels(deltaTime);
        updateBombs(deltaTime);
        updateGhostModels(deltaTime);
    }

    private void updateBombs(float deltaTime) {
        BombModel [] array = new BombModel[this.bombList.size()];
        //concurrent modification exception almamak icin.
        this.bombList.toArray(array);
        for (BombModel bomb: array) {
            bomb.update(deltaTime);
        }
    }

    private void updateGhostModels(float deltaTime) {
        for (GhostModel ghostModel : ghostModels.values()) {
            updateGhostModel(ghostModel, deltaTime);
        }
    }

    private void updatePlayerModels(float deltaTime) {
        for (PlayerModel playerModel : playerModels.values()) {
            updatePlayerModel(playerModel, deltaTime);
        }
    }

    private void updateGhostModel(GhostModel ghostModel, float deltaTime) {
        float x = ghostModel.getX();
        float y = ghostModel.getY();
        float speed = ghostModel.getSpeed();
        boolean ghostCanMove = checkGhostCanMove(ghostModel);
        if (ghostCanMove) {
            switch (ghostModel.getState()) {
                case WALKING_UP:
                    ghostModel.setY(y + deltaTime * speed);
                    break;
                case WALKING_DOWN:
                    ghostModel.setY(y - deltaTime * speed);
                    break;
                case WALKING_RIGHT:
                    ghostModel.setX(x + deltaTime * speed);
                    break;
                case WALKING_LEFT:
                    ghostModel.setX(x - deltaTime * speed);
                    break;
            }

            int gridX = getGridX(ghostModel.getX());
            int gridY = getGridY(ghostModel.getY());
            ghostModel.setGridX(gridX);
            ghostModel.setGridY(gridY);
            if (ghostModel.getTargetGridX() == gridX && ghostModel.getTargetGridY() == gridY) {
                stopGhost(ghostModel.getId());
            }
        }
        else {
            stopGhost(ghostModel.getId());
        }
    }

    private void updatePlayerModel(PlayerModel playerModel, float deltaTime) {
        float x = playerModel.getX();
        float y = playerModel.getY();
        float speed = playerModel.getSpeed();
        boolean playerCanMove = checkPlayerCanMove(playerModel);
        if (playerCanMove) {
            switch (playerModel.getState()) {
                case WALKING_UP:
                    playerModel.setY(y + deltaTime * speed);
                    break;
                case WALKING_DOWN:
                    playerModel.setY(y - deltaTime * speed);
                    break;
                case WALKING_RIGHT:
                    playerModel.setX(x + deltaTime * speed);
                    break;
                case WALKING_LEFT:
                    playerModel.setX(x - deltaTime * speed);
                    break;
            }
        }
    }

    private boolean checkPlayerCanMove(PlayerModel playerModel) {
        byte[][] grid = labyrinthModel.getGrid();
        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;

        switch (playerModel.getState()) {
            case WALKING_UP: {
                float x = playerModel.getOriginX();
                float y = playerModel.getY() + playerModel.getHeight();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
            case WALKING_DOWN: {
                float x = playerModel.getOriginX();
                float y = playerModel.getY();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
            case WALKING_RIGHT: {
                float x = playerModel.getX() + playerModel.getWidth();
                float y = playerModel.getOriginY();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
            case WALKING_LEFT:{
                float x = playerModel.getX();
                float y = playerModel.getOriginY();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
        }
        return true;
    }

    private boolean checkGhostCanMove(GhostModel ghostModel) {
        byte[][] grid = labyrinthModel.getGrid();
        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;

        switch (ghostModel.getState()) {
            case WALKING_UP: {
                float x = ghostModel.getOriginX();
                float y = ghostModel.getY() + ghostModel.getHeight();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
            case WALKING_DOWN: {
                float x = ghostModel.getOriginX();
                float y = ghostModel.getY();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
            case WALKING_RIGHT: {
                float x = ghostModel.getX() + ghostModel.getWidth();
                float y = ghostModel.getOriginY();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
            case WALKING_LEFT:{
                float x = ghostModel.getX();
                float y = ghostModel.getOriginY();
                return isGridPositionEmpty(grid, unitWidth, unitHeight, x, y);
            }
        }
        return true;
    }

    private boolean isGridPositionEmpty(byte[][] grid, float unitWidth, float unitHeight, float x, float y) {
        int gridX = (int) ((x - resolutionHelper.getGameAreaPosition().x) / unitWidth);
        int gridY = (int) ((y - resolutionHelper.getGameAreaPosition().y) / unitHeight);
        return grid[gridX][gridY] == LabyrinthModel.EMPTY;
    }

    public int getGridX(float x) {
        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        return (int) Math.round((double)Math.round(x - resolutionHelper.getGameAreaPosition().x) / Math.round(unitWidth));
    }

    public int getGridY(float y) {
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;
        return (int) Math.round((double)Math.round(y - resolutionHelper.getGameAreaPosition().y) / Math.round(unitHeight));
    }

    public void moveGhost(int ghostId, int gridX, int gridY, GameScreen.Direction direction, int distance) {
        GhostModel model = ghostModels.get(ghostId);
        if (model == null) {
            Log.e("Cannot find Ghost Model : " + ghostId);
            return;
        }

        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;

        model.setX(resolutionHelper.getGameAreaPosition().x + unitWidth * gridX);
        model.setY(resolutionHelper.getGameAreaPosition().y + unitHeight * gridY);
        model.setTargetGridX(gridX);
        model.setTargetGridY(gridY);
        switch (direction) {
            case UP:
                model.setState(GhostModel.State.WALKING_UP);
                model.setTargetGridY(gridY + distance);
                break;
            case DOWN:
                model.setState(GhostModel.State.WALKING_DOWN);
                model.setTargetGridY(gridY - distance);
                break;
            case RIGHT:
                model.setState(GhostModel.State.WALKING_RIGHT);
                model.setTargetGridX(gridX + distance);
                break;
            case LEFT:
                model.setState(GhostModel.State.WALKING_LEFT);
                model.setTargetGridX(gridX - distance);
                break;
        }
    }

    public void stopGhost(int ghostId) {
        GhostModel ghost = ghostModels.get(ghostId);
        switch (ghost.getState()) {
            case WALKING_UP:
                ghost.setState(GhostModel.State.STANDING_UP);
                callGhostStopListener(ghost);
                break;
            case WALKING_DOWN:
                ghost.setState(GhostModel.State.STANDING_DOWN);
                callGhostStopListener(ghost);
                break;
            case WALKING_RIGHT:
                ghost.setState(GhostModel.State.STANDING_RIGHT);
                callGhostStopListener(ghost);
                break;
            case WALKING_LEFT:
                ghost.setState(GhostModel.State.STANDING_LEFT);
                callGhostStopListener(ghost);
                break;
        }
    }

    private void callGhostStopListener(GhostModel ghostModel) {
        if (UserSession.getInstance().isOwnerRoom()) {
            if (ghostModel.getListener() != null) {
                ghostModel.getListener().onStop();
            }
        }
    }

    public void movePlayer(String playerName, GameScreen.Direction direction) {
        PlayerModel player = playerModels.get(playerName);
        switch (direction) {
            case UP:
                player.setState(PlayerModel.State.WALKING_UP);
                break;
            case DOWN:
                player.setState(PlayerModel.State.WALKING_DOWN);
                break;
            case RIGHT:
                player.setState(PlayerModel.State.WALKING_RIGHT);
                break;
            case LEFT:
                player.setState(PlayerModel.State.WALKING_LEFT);
                break;
        }
    }

    public void stopPlayer(String playerName) {
        PlayerModel player = playerModels.get(playerName);
        switch (player.getState()) {
            case WALKING_UP:
                player.setState(PlayerModel.State.STANDING_UP);
                break;
            case WALKING_DOWN:
                player.setState(PlayerModel.State.STANDING_DOWN);
                break;
            case WALKING_RIGHT:
                player.setState(PlayerModel.State.STANDING_RIGHT);
                break;
            case WALKING_LEFT:
                player.setState(PlayerModel.State.STANDING_LEFT);
                break;
        }
    }

    public void addGhostModels(List<GhostModel> ghostModels) {
        for (GhostModel ghostModel : ghostModels) {
            this.ghostModels.put(ghostModel.getId(), ghostModel);
        }
    }

    public void addGhostModel(GhostModel ghostModel) {
        ghostModels.put(ghostModel.getId(), ghostModel);
    }

    public Map<Integer, GhostModel> getGhostModels() {
        return ghostModels;
    }

    public void addPlayerModel(PlayerModel playerModel) {
        playerModels.put(playerModel.getPlayerName(), playerModel);
    }

    public PlayerModel getPlayerModel(String playerName) {
        return this.playerModels.get("playerModel");
    }

    public void setResolutionHelper(ResolutionHelper resolutionHelper) {
        this.resolutionHelper = resolutionHelper;
    }

    public LabyrinthModel getLabyrinthModel() {
        return labyrinthModel;
    }

    public BombModel  playerDroppedBomb(String username) {

        PlayerModel playerModel = playerModels.get(username);
        BombModel bombModel = new BombModel();
        bombModel.setOwner(username);
        bombModel.setRemainingSeconds(5);

        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;
        int gridX = (int) ((playerModel.getOriginX() - resolutionHelper.getGameAreaPosition().x) / unitWidth);
        int gridY = (int) ((playerModel.getOriginY() - resolutionHelper.getGameAreaPosition().y) / unitHeight);

        bombModel.setX(gridX * unitWidth + resolutionHelper.getGameAreaPosition().x);
        bombModel.setY(gridY * unitHeight + resolutionHelper.getGameAreaPosition().y);
        //Center bomb
        TextureRegion bombTexture = this.assetsInterface.getTextureAtlas("Common.atlas").findRegion("bomb1");
        float xOffSet = (unitWidth - bombTexture.getRegionWidth()) * 0.5f;
        float yOffSet = (unitHeight - bombTexture.getRegionHeight()) * 0.5f;
        bombModel.setX(bombModel.getX() + xOffSet);
        bombModel.setY(bombModel.getY() + yOffSet);

        bombModel.addBombListener(this);
        bombList.add(bombModel);
        return bombModel;
    }

    @Override
    public void onBombExploded(BombModel bombModel) {
        this.bombList.remove(bombModel);
        System.out.println("There are " + bombList.size() + " bombs on screen.");
    }

    public void setAssetsInterface(AssetsInterface assetsInterface) {
        this.assetsInterface = assetsInterface;
    }

    public List<Vector2> calculateBombExplosionCells(BombModel bombModel) {
        List<Vector2> cells = new ArrayList<Vector2>();
        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;
        int gridX = (int) ((bombModel.getOriginX()  - resolutionHelper.getGameAreaPosition().x) / unitWidth);
        int gridY = (int) ((bombModel.getOriginY() - resolutionHelper.getGameAreaPosition().y) / unitHeight);
        byte[][] grid = labyrinthModel.getGrid();
        //gridX, gridY etrafindaki patlayacak cell'leri bul. bossa iki cell, brick varsa 1 cell, wall varsa 0 cell. patlar.
        //left
        if (addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX + 1, gridY) ) {
            addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX + 2, gridY);
        } else {
            addToListIfCellTypeIs(LabyrinthModel.BRICK, cells, grid, gridX + 1, gridY);
        }

        //right
        if (addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX - 1, gridY) ) {
            addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX - 2, gridY);
        } else {
            addToListIfCellTypeIs(LabyrinthModel.BRICK, cells, grid, gridX - 1, gridY);
        }

        //top
        if (addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX, gridY + 1)) {
            addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX, gridY + 2);
        } else {
            addToListIfCellTypeIs(LabyrinthModel.BRICK, cells, grid, gridX, gridY + 1);
        }

        //down
        if (addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX, gridY - 1)) {
            addToListIfCellTypeIs(LabyrinthModel.EMPTY, cells, grid, gridX, gridY - 2);
        } else {
            addToListIfCellTypeIs(LabyrinthModel.BRICK, cells, grid, gridX, gridY - 1);
        }
        cells.add(new Vector2(gridX, gridY));
        return cells;
    }

    public List<Vector2> convertCellIndexToScreenCoordinates(List<Vector2> cells) {
        List<Vector2> list = new ArrayList<Vector2>();
        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;
        for (Vector2 cellIndex : cells) {
            int indexX = (int)cellIndex.x;
            int indexY = (int)cellIndex.y;
            Vector2 v = new Vector2(
                    indexX * unitWidth + resolutionHelper.getGameAreaPosition().x,
                    indexY * unitHeight+ resolutionHelper.getGameAreaPosition().y);
            list.add(v);
        }
        return list;
    }

    private boolean addToListIfCellTypeIs(byte cellType,  List<Vector2> list, byte [][] grid, int x, int y) {
        if (x >= LabyrinthModel.NUM_COLS || y >= LabyrinthModel.NUM_ROWS || x<0 || y <0){
            return false;
        }
        if (grid[x][y] == cellType) {
            list.add(new Vector2(x, y));
            return true;
        }
        return false;
    }
}
