package net.javaci.mobile.bomberman.core.net.protocol;

import net.javaci.mobile.bomberman.core.models.GhostModel;
import net.javaci.mobile.bomberman.core.models.LabyrinthModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateGameCommand extends Command {

    private List<GhostModel> ghostModels = new ArrayList<GhostModel>();
    private byte [][] grid;

    public static Command build(JSONObject json) {
        CreateGameCommand command = new CreateGameCommand();
        try {
            command.parseCommonFields(json);
            JSONArray jsonArray = json.getJSONArray("ghosts");
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                GhostModel ghostModel = new GhostModel(jsonObject.getInt("id"));
                ghostModel.setGridX(jsonObject.getInt("gridX"));
                ghostModel.setGridY(jsonObject.getInt("gridY"));
                ghostModel.setType(GhostModel.Type.valueOf(jsonObject.getString("type")));
                command.ghostModels.add(ghostModel);
            }

            byte[][] grid = new byte[21][13];
            JSONArray gridArray = json.getJSONArray("grid");
            for (int i=0; i<gridArray.length(); i++) {
                JSONArray gridRowArray = gridArray.getJSONArray(i);
                for (int j=0; j<gridRowArray.length(); j++) {
                    grid[i][j] = (byte)gridRowArray.getInt(j);
                }
            }
            command.grid = grid;
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());
        }
        return command;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (GhostModel ghostModel : ghostModels) {
            JSONObject object = new JSONObject();
            object.put("id", ghostModel.getId());
            object.put("gridX", ghostModel.getGridX());
            object.put("gridY", ghostModel.getGridY());
            object.put("type", ghostModel.getType());
            jsonArray.put(object);
        }
        json.put("ghosts", jsonArray);

        JSONArray gridArray = new JSONArray();
        if (grid != null) {
            for (int i=0; i<grid.length; i++) {
                JSONArray gridRowArray = new JSONArray();
                for (int j=0; j<grid[i].length; j++) {
                    gridRowArray.put(grid[i][j]);
                }
                gridArray.put(gridRowArray);
            }
        }
        json.put("grid", gridArray);
    }

    public void setGhostModels(List<GhostModel> ghostModels) {
        this.ghostModels = ghostModels;
    }

    public List<GhostModel> getGhostModels() {
        return ghostModels;
    }

    public byte[][] getGrid() {
        return grid;
    }

    @Override
    public int getCommand() {
        return CREATE_GAME;
    }

    public void setGrid(byte[][] grid) {
        this.grid = grid;
    }
}
