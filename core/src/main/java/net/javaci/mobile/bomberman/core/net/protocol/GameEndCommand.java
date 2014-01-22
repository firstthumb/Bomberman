package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class GameEndCommand extends Command {
    public static enum GameEndReason {
        GAME_END, OWNER_LEFT
    }

    private GameEndReason reason;

    public static Command build(JSONObject json) {
        GameEndCommand command = new GameEndCommand();
        try {
            command.parseCommonFields(json);
            command.reason = GameEndReason.valueOf(json.getString("reason"));
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());

        }
        return command;
    }

    public GameEndReason getReason() {
        return reason;
    }

    public void setReason(GameEndReason reason) {
        this.reason = reason;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        json.put("reason", reason);
    }

    @Override
    public int getCommand() {
        return GAME_END;
    }
}
