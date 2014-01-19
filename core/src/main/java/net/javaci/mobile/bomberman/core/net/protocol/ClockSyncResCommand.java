package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class ClockSyncResCommand extends Command {
    private long initialTimestamp;

    public static Command build(JSONObject json) {
        ClockSyncResCommand command = new ClockSyncResCommand();
        try {
            command.parseCommonFields(json);
            command.initialTimestamp = json.getLong("initialTimestamp");
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());
        }
        return command;
    }

    public long getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(long initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        json.put("initialTimestamp", initialTimestamp);
    }

    @Override
    public int getCommand() {
        return CLOCK_SYNC_RES;
    }
}
