package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class UndefinedCommand extends Command {
    private String message;

    public UndefinedCommand(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {

    }

    @Override
    public int getCommand() {
        return 0;
    }
}
