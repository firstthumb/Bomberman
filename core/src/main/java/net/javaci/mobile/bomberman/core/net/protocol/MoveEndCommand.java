package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class MoveEndCommand extends Command {
    private String direction;
    private int x;
    private int y;

    public static Command build(JSONObject json) {
        MoveEndCommand command = new MoveEndCommand();
        try {
            command.parseCommonFields(json);
            command.direction = json.getString("direction");
            command.x = json.getInt("x");
            command.y = json.getInt("y");
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());

        }
        return command;
    }

    public String getDirection() {
        return direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        json.put("x", x);
        json.put("y", y);
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
