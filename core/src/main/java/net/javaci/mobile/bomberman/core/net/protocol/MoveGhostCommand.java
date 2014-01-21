package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class MoveGhostCommand extends Command {
    private String direction;
    private int id;
    private int gridX;
    private int gridY;
    private int distance;

    public static Command build(JSONObject json) {
        MoveGhostCommand command = new MoveGhostCommand();
        try {
            command.parseCommonFields(json);
            command.direction = json.getString("direction");
            command.gridX = json.getInt("gridX");
            command.gridY = json.getInt("gridY");
            command.distance = json.getInt("distance");
            command.id = json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());

        }
        return command;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        json.put("id", id);
        json.put("gridX", gridX);
        json.put("gridY", gridY);
        json.put("direction", direction);
        json.put("distance", distance);
    }

    @Override
    public int getCommand() {
        return MOVE_GHOST;
    }
}
