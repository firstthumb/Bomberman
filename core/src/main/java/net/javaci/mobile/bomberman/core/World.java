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

import java.util.*;

public class World implements BombModel.BombListener {
    private Random rand = new Random();
    private Map<String, PlayerModel> playerModels = new HashMap<String, PlayerModel>();
    private LabyrinthModel labyrinthModel;
    private ResolutionHelper resolutionHelper;
    private Map<Integer, GhostModel> ghostModels = new HashMap<Integer, GhostModel>();
    private List<BombModel> bombList = new ArrayList<BombModel>();
    private AssetsInterface assetsInterface;
    private float gridWidth;
    private float gridHeight;


    public void initialize(LabyrinthModel labyrinthModel, ResolutionHelper resolutionHelper, AssetsInterface assets) {
        this.labyrinthModel = labyrinthModel;
        this.resolutionHelper = resolutionHelper;
        this.assetsInterface = assets;
        this.gridWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        this.gridHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;
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
        float speed = ghostModel.getSpeed();
        boolean ghostCanMove = checkGhostCanMove(ghostModel);
        if (ghostCanMove) {
            switch (ghostModel.getState()) {
                case WALKING_UP:
                    if (ghostModel.getTargetGridY() >= 0) {
                        float diff = getGridOriginY(ghostModel.getTargetGridY()) - ghostModel.getOriginY();
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            stopGhost(ghostModel.getId());
                        }
                        ghostModel.setY(ghostModel.getY() + distance);
                    }
                    break;
                case WALKING_DOWN:
                    if (ghostModel.getTargetGridY() >= 0) {
                        float diff = ghostModel.getOriginY() - getGridOriginY(ghostModel.getTargetGridY());
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            stopGhost(ghostModel.getId());
                        }
                        ghostModel.setY(ghostModel.getY() - distance);
                    }
                    break;
                case WALKING_RIGHT:
                    if (ghostModel.getTargetGridX() >= 0) {
                        float diff = getGridOriginX(ghostModel.getTargetGridX()) - ghostModel.getOriginX();
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            stopGhost(ghostModel.getId());
                        }
                        ghostModel.setX(ghostModel.getX() + distance);
                    }
                    break;
                case WALKING_LEFT:
                    if (ghostModel.getTargetGridX() >= 0) {
                        float diff = ghostModel.getOriginX() - getGridOriginX(ghostModel.getTargetGridX());
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            stopGhost(ghostModel.getId());
                        }
                        ghostModel.setX(ghostModel.getX() - distance);
                    }
                    break;
            }
        }
        else {
            stopGhost(ghostModel.getId());;
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
                case STOPPING_UP: {
                    int gridY = getGridY(playerModel.getOriginY());
                    float centerGridY = getGridOriginY(gridY);
                    if (playerModel.getOriginY() > centerGridY) {
                        playerModel.setTargetGridY(clamp(1, (gridY + 1), LabyrinthModel.NUM_ROWS - 2));
                    } else {
                        playerModel.setTargetGridY(gridY);
                    }
                    if (playerModel.getTargetGridY() >= 0) {
                        float diff = getGridOriginY(playerModel.getTargetGridY()) - playerModel.getOriginY();
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            playerModel.setTargetGridY(-1);
                            playerModel.setState(PlayerModel.State.STANDING_UP);
                        }
                        playerModel.setY(playerModel.getY() + distance);
                    }
                    break;
                }
                case WALKING_DOWN:
                    playerModel.setY(y - deltaTime * speed);
                    break;
                case STOPPING_DOWN: {
                    int gridY = getGridY(playerModel.getOriginY());
                    float centerGridY = getGridOriginY(gridY);
                    if (playerModel.getOriginY() < centerGridY) {
                        playerModel.setTargetGridY(Math.max((gridY - 1), 1));
                    } else {
                        playerModel.setTargetGridY(gridY);
                    }
                    if (playerModel.getTargetGridY() >= 0) {
                        float diff = playerModel.getOriginY() - getGridOriginY(playerModel.getTargetGridY());
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            playerModel.setTargetGridY(-1);
                            playerModel.setState(PlayerModel.State.STANDING_DOWN);
                        }
                        playerModel.setY(playerModel.getY() - distance);
                    }
                    break;
                }
                case WALKING_RIGHT:
                    playerModel.setX(x + deltaTime * speed);
                    break;
                case STOPPING_RIGHT: {
                    int gridX = getGridX(playerModel.getOriginX());
                    float centerGridX = getGridOriginX(gridX);
                    if (playerModel.getOriginX() > centerGridX) {
                        playerModel.setTargetGridX(clamp(1, (gridX + 1), LabyrinthModel.NUM_COLS - 2));
                    } else {
                        playerModel.setTargetGridX(gridX);
                    }
                    if (playerModel.getTargetGridX() >= 0) {
                        float diff = getGridOriginX(playerModel.getTargetGridX()) - playerModel.getOriginX();
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            playerModel.setTargetGridX(-1);
                            playerModel.setState(PlayerModel.State.STANDING_RIGHT);
                        }
                        playerModel.setX(playerModel.getX() + distance);
                    }
                    break;
                }
                case WALKING_LEFT:
                    playerModel.setX(x - deltaTime * speed);
                    break;
                case STOPPING_LEFT:{
                    int gridX = getGridX(playerModel.getOriginX());
                    float centerGridX = getGridOriginX(gridX);
                    if (playerModel.getOriginX() < centerGridX) {
                        playerModel.setTargetGridX(clamp(1, (gridX - 1), LabyrinthModel.NUM_COLS - 2));
                    } else {
                        playerModel.setTargetGridX(gridX);
                    }
                    if (playerModel.getTargetGridX() >= 0) {
                        float diff = playerModel.getOriginX() - getGridOriginX(playerModel.getTargetGridX());
                        float distance = deltaTime * speed;
                        if (distance >= diff) {
                            distance = diff;
                            playerModel.setTargetGridX(-1);
                            playerModel.setState(PlayerModel.State.STANDING_LEFT);
                        }
                        playerModel.setX(playerModel.getX() - distance);
                    }
                    break;
                }
            }
        }  else {
            switch (playerModel.getState()) {
                case STOPPING_UP:
                    playerModel.setState(PlayerModel.State.STANDING_UP);
                    break;
                case STOPPING_DOWN:
                    playerModel.setState(PlayerModel.State.STANDING_DOWN);
                    break;
                case STOPPING_RIGHT:
                    playerModel.setState(PlayerModel.State.STANDING_RIGHT);
                    break;
                case STOPPING_LEFT:
                    playerModel.setState(PlayerModel.State.STANDING_LEFT);
                    break;
            }
        }
    }

    private float getGridOriginX(int gridX) {
        return gridWidth * gridX + gridWidth * 0.5f + resolutionHelper.getGameAreaPosition().x;
    }

    private float getGridOriginY(int gridY) {
        return gridHeight * gridY + gridHeight * 0.5f + resolutionHelper.getGameAreaPosition().y;
    }

    private boolean checkPlayerCanMove(PlayerModel playerModel) {
        byte[][] grid = labyrinthModel.getGrid();
        switch (playerModel.getState()) {
            case WALKING_UP:
            case STOPPING_UP:{
                int gridX = getGridX(playerModel.getOriginX());
                int gridY = getGridY(playerModel.getY() + playerModel.getHeight() + 1);
                return isGridPositionEmpty(grid, gridX, gridY);
            }
            case WALKING_DOWN:
            case STOPPING_DOWN:{
                int gridX = getGridX(playerModel.getOriginX());
                int gridY = getGridY(playerModel.getY() -  1);
                return isGridPositionEmpty(grid, gridX, gridY);
            }
            case WALKING_RIGHT:
            case STOPPING_RIGHT:{
                int gridX = getGridX(playerModel.getX() + playerModel.getWidth() + 1);
                int gridY = getGridY(playerModel.getOriginY());
                return isGridPositionEmpty(grid, gridX, gridY);
            }
            case WALKING_LEFT:
            case STOPPING_LEFT:{
                int gridX = getGridX(playerModel.getX() - 1);
                int gridY = getGridY(playerModel.getOriginY());
                return isGridPositionEmpty(grid, gridX, gridY);
            }
        }
        return true;
    }

    private boolean checkGhostCanMove(GhostModel ghostModel) {
        byte[][] grid = labyrinthModel.getGrid();

        switch (ghostModel.getState()) {
            case WALKING_UP: {
                int gridX = getGridX(ghostModel.getOriginX());
                int gridY = getGridY(ghostModel.getY() + ghostModel.getHeight() + 1);
                return isGridPositionEmpty(grid, gridX, gridY);
            }
            case WALKING_DOWN: {
                int gridX = getGridX(ghostModel.getOriginX());
                int gridY = getGridY(ghostModel.getY() - 1);
                return isGridPositionEmpty(grid, gridX, gridY);
            }
            case WALKING_RIGHT: {
                int gridX = getGridX(ghostModel.getX() + ghostModel.getWidth() + 1);
                int gridY = getGridY(ghostModel.getOriginY());
                return isGridPositionEmpty(grid, gridX, gridY);
            }
            case WALKING_LEFT:{
                int gridX = getGridX(ghostModel.getX() - 1);
                int gridY = getGridY(ghostModel.getOriginY());
                return isGridPositionEmpty(grid, gridX, gridY);
            }
        }
        return true;
    }

    private boolean isGridPositionEmpty(byte[][] grid, int gridX, int gridY) {
        if (gridX >= LabyrinthModel.NUM_COLS || gridY >= LabyrinthModel.NUM_ROWS || gridX<0 || gridY <0){
            return false;
        }
        return grid[gridX][gridY] == LabyrinthModel.EMPTY;
    }

    public int getGridX(float x) {
        return (int) ((x - resolutionHelper.getGameAreaPosition().x) / gridWidth);
    }

    public int getGridY(float y) {
        return (int) ((y - resolutionHelper.getGameAreaPosition().y) / gridHeight);
    }

    public void moveGhost(int ghostId, int gridX, int gridY, GameScreen.Direction direction, int distance) {
        GhostModel model = ghostModels.get(ghostId);
        if (model == null) {
            Log.e("Cannot find Ghost Model : " + ghostId);
            return;
        }

        switch (direction) {
            case UP:
                model.setState(GhostModel.State.WALKING_UP);
                model.setTargetGridX(gridX);
                model.setTargetGridY(gridY + distance);
                break;
            case DOWN:
                model.setState(GhostModel.State.WALKING_DOWN);
                model.setTargetGridX(gridX);
                model.setTargetGridY(gridY - distance);
                break;
            case RIGHT:
                model.setState(GhostModel.State.WALKING_RIGHT);
                model.setTargetGridX(gridX + distance);
                model.setTargetGridY(gridY);
                break;
            case LEFT:
                model.setState(GhostModel.State.WALKING_LEFT);
                model.setTargetGridX(gridX - distance);
                model.setTargetGridY(gridY);
                break;
        }
    }

    public void stopGhost(int ghostId) {
        GhostModel ghost = ghostModels.get(ghostId);
        ghost.setGridX(getGridX(ghost.getOriginX()));
        ghost.setGridY(getGridY(ghost.getOriginY()));
        ghost.setTargetGridX(-1);
        ghost.setTargetGridY(-1);
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
        if (UserSession.getInstance().isServer()) {
            if (ghostModel.getListener() != null) {
                ghostModel.getListener().onStop();
            }
        }
    }

    public void movePlayer(String playerName, GameScreen.Direction direction) {
        PlayerModel player = playerModels.get(playerName);
        if (isMoving(player)) {
            return;
        }
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

    private boolean isMoving(PlayerModel playerModel) {
        PlayerModel.State state = playerModel.getState();
        return ! (state == PlayerModel.State.STANDING_DOWN ||
                state == PlayerModel.State.STANDING_LEFT ||
                state == PlayerModel.State.STANDING_RIGHT ||
                state == PlayerModel.State.STANDING_UP);
    }

    public void stopPlayer(String playerName) {
        PlayerModel player = playerModels.get(playerName);
        switch (player.getState()) {
            case WALKING_UP:
                player.setState(PlayerModel.State.STOPPING_UP);
                break;
            case WALKING_DOWN:
                player.setState(PlayerModel.State.STOPPING_DOWN);
                break;
            case WALKING_RIGHT:
                player.setState(PlayerModel.State.STOPPING_RIGHT);
                break;
            case WALKING_LEFT:
                player.setState(PlayerModel.State.STOPPING_LEFT);
                break;
        }
    }

    public void addGhostModels(List<GhostModel> ghostModels) {
        for (GhostModel ghostModel : ghostModels) {
            this.ghostModels.put(ghostModel.getId(), ghostModel);
        }
    }

    public void putGhostEmptyPlace(GhostModel ghostModel) {
        Random rand = new Random();
        int randX, randY;
        do {
            randX = rand.nextInt(LabyrinthModel.NUM_COLS - 4) + 2;
            randY = rand.nextInt(LabyrinthModel.NUM_ROWS - 4) + 2;
        } while (labyrinthModel.getGrid()[randX][randY] != LabyrinthModel.EMPTY);

        ghostModel.setX(getX(randX));
        ghostModel.setY(getY(randY));

        ghostModel.setGridX(randX);
        ghostModel.setGridY(randY);
    }

    public float getX(int gridX) {
        float unitWidth = resolutionHelper.getGameAreaBounds().x / (float) LabyrinthModel.NUM_COLS;
        return unitWidth * gridX + resolutionHelper.getGameAreaPosition().x;
    }

    public float getY(int gridY) {
        float unitHeight = resolutionHelper.getGameAreaBounds().y / (float) LabyrinthModel.NUM_ROWS;
        return unitHeight * gridY + resolutionHelper.getGameAreaPosition().y;
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

    public LabyrinthModel getLabyrinthModel() {
        return labyrinthModel;
    }

    public BombModel dropBomb(int id, int gridX, int gridY, String owner) {
        BombModel bombModel = new BombModel(id);
        bombModel.setX(getX(gridX));
        bombModel.setY(getY(gridY));
        bombModel.setGridX(gridX);
        bombModel.setGridY(gridY);
        bombModel.setOwner(owner);

        bombModel.addBombListener(this);
        bombList.add(bombModel);

        return bombModel;
    }

    public BombModel  playerDroppedBomb(String username) {
        PlayerModel playerModel = playerModels.get(username);
        BombModel bombModel = new BombModel(rand.nextInt());
        bombModel.setWidth(this.gridWidth);
        bombModel.setHeight(this.gridHeight);
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
        bombModel.setGridX(getGridX(bombModel.getOriginX()));
        bombModel.setGridY(getGridY(bombModel.getOriginY()));

        bombModel.addBombListener(this);
        bombList.add(bombModel);
        return bombModel;
    }

    @Override
    public void onBombExploded(BombModel bombModel) {
        this.bombList.remove(bombModel);
        System.out.println("There are " + bombList.size() + " bombs on screen.");
    }

    public List<BombModel> getBombList() {
        return bombList;
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

    public float getGridHeight() {
        return gridHeight;
    }

    public float getGridWidth() {
        return gridWidth;
    }

    private int clamp(int min, int value, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public void removePlayer(String playerName) {
        this.playerModels.remove(playerName);
    }

    public int getNextGameIndex() {
        int max = 0;
        for (PlayerModel player : playerModels.values()) {
            if (player.getGameIndex() > max) {
                max = player.getGameIndex();
            }
        }
        return max + 1;
    }
}
