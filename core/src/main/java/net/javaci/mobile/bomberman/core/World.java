package net.javaci.mobile.bomberman.core;

import net.javaci.mobile.bomberman.core.models.LabyrinthModel;
import net.javaci.mobile.bomberman.core.models.PlayerModel;
import net.javaci.mobile.bomberman.core.view.GameScreen;

import java.util.HashMap;
import java.util.Map;

public class World {
    private Map<String, PlayerModel> playerModels = new HashMap<String, PlayerModel>();
    private LabyrinthModel labyrinthModel;

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

    public void addPlayerModel(PlayerModel playerModel) {
        playerModels.put(playerModel.getPlayerName(), playerModel);
    }

    public PlayerModel getPlayerModel(String playerName) {
        return this.playerModels.get("playerModel");
    }
}
