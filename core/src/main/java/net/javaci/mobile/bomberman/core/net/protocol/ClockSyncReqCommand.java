package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class ClockSyncReqCommand extends Command {

    public static Command build(JSONObject json) {
        ClockSyncReqCommand command = new ClockSyncReqCommand();
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
        return CLOCK_SYNC_REQ;
    }


}
