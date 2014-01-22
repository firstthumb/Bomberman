package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class DropBombCommand extends Command {
    private int gridX;
    private int gridY;
    private int id;

    public static Command build(JSONObject json) {
        DropBombCommand command = new DropBombCommand();
        try {
            command.parseCommonFields(json);
            command.gridX = json.getInt("gridX");
            command.gridY = json.getInt("gridY");
            command.id = json.getInt("id");
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
    }

    @Override
    public int getCommand() {
        return DROP_BOMB;
    }
}
