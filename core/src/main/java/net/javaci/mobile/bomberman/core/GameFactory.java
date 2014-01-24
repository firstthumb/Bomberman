package net.javaci.mobile.bomberman.core;

import java.util.HashMap;
import java.util.Map;

public class GameFactory {
    public static class GameModel {
        public int numGhosts;
        public int numBricks;
        public int numBomb;
    }

    private static Map<Integer, GameModel> gameModels = new HashMap<Integer, GameModel>();

    static {
        GameModel level1 = new GameModel();
        level1.numGhosts = 10;
        level1.numBricks = 10;
        level1.numBomb = 2;
        gameModels.put(1, level1);
    }

    public static GameModel getGameModel(int level) {
        return gameModels.get(level);
    }
}
