package net.javaci.mobile.bomberman.core;

import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.LabyrinthModel;
import net.javaci.mobile.bomberman.core.models.PlayerModel;
import net.javaci.mobile.bomberman.core.view.GameScreen;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
    private Map<String, PlayerModel> playerModels = new HashMap<String, PlayerModel>();
    private LabyrinthModel labyrinthModel;
    private ResolutionHelper resolutionHelper;
    private Map<Integer, GhostModel> ghostModels = new HashMap<Integer, GhostModel>();

    public void setLabyrinthModel(LabyrinthModel labyrinthModel) {
        this.labyrinthModel = labyrinthModel;
    }

    public void update(float deltaTime) {
        updatePlayerModels(deltaTime);
    }

    private void updatePlayerModels(float deltaTime) {
        for (PlayerModel playerModel : playerModels.values()) {
            updatePlayerModel(playerModel, deltaTime);
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

    private boolean isGridPositionEmpty(byte[][] grid, float unitWidth, float unitHeight, float x, float y) {
        int gridX = (int) ((x - resolutionHelper.getGameAreaPosition().x) / unitWidth);
        int gridY = (int) ((y - resolutionHelper.getGameAreaPosition().y) / unitHeight);
        return grid[gridX][gridY] == LabyrinthModel.EMPTY;
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
}
