package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class StartGameCommand extends Command {

    public static Command build(JSONObject json) {
        StartGameCommand command = new StartGameCommand();
        try {
            command.parseCommonFields(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());
        }
        return command;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
    }

    @Override
    public int getCommand() {
        return START_GAME;
    }
}
