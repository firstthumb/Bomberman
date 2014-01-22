package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class MoveEndCommand extends Command {
    private String direction;
    private int gridX;
    private int gridY;

    public static Command build(JSONObject json) {
        MoveEndCommand command = new MoveEndCommand();
        try {
            command.parseCommonFields(json);
            command.direction = json.getString("direction");
            command.gridX = json.getInt("gridX");
            command.gridY = json.getInt("gridY");
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());

        }
        return command;
    }

    public String getDirection() {
        return direction;
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

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        json.put("gridX", gridX);
        json.put("gridY", gridY);
        json.put("direction", direction);
    }

    @Override
    public int getCommand() {
        return MOVE_END;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
