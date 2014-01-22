package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExplodeBombCommand extends Command {
    private int gridX;
    private int gridY;
    private int id;
    private List<String> explodedPlayers = new ArrayList<String>();

    public static Command build(JSONObject json) {
        ExplodeBombCommand command = new ExplodeBombCommand();
        try {
            command.parseCommonFields(json);
            command.gridX = json.getInt("gridX");
            command.gridY = json.getInt("gridY");
            command.id = json.getInt("id");
            JSONArray jsonArray = json.getJSONArray("explodedPlayers");
            if (jsonArray != null) {
                int length = jsonArray.length();
                for (int i=0; i<length; i++) {
                    command.explodedPlayers.add(jsonArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());

        }
        return command;
    }

    public int getGridX() {
        return gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        json.put("gridX", gridX);
        json.put("gridY", gridY);
        json.put("id", id);
        json.put("explodedPlayers", this.explodedPlayers);
    }

    @Override
    public int getCommand() {
        return EXPLODE_BOMB;
    }


    public List<String> getExplodedPlayers() {
        return explodedPlayers;
    }

    public void setExplodedPlayers(List<String> explodedPlayers) {
        this.explodedPlayers = explodedPlayers;
    }
}
