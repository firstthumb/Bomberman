package net.javaci.mobile.bomberman.core.net.protocol;


import org.json.JSONException;
import org.json.JSONObject;

public abstract class Command {
    public static final int CLOCK_SYNC_REQ = 1;
    public static final int CLOCK_SYNC_RES = 2;
    public static final int MOVE_START = 3;
    public static final int MOVE_END = 4;
    public static final int START_GAME = 5;
    protected long timestamp;
    protected String fromUser;
    protected int command;

    protected void parseCommonFields(JSONObject json) throws JSONException {
        this.timestamp = json.getLong("t");
        this.command = json.getInt("command");
        this.fromUser = json.getString("fromUser");
    }

    public String serialize() {
        JSONObject json = new JSONObject();
        try {
            this.timestamp = System.currentTimeMillis();
            json.put("t", this.timestamp);
            json.put("command", this.getCommand());
            json.put("fromUser", this.fromUser);
            serializeCustomFields(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    protected abstract void serializeCustomFields(JSONObject json) throws JSONException;

    public abstract int getCommand();

    public long getTimestamp() {
        return timestamp;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    @Override
    public String toString() {
        return "Command{" +
                "timestamp=" + timestamp +
                ", fromUser='" + fromUser + '\'' +
                ", command=" + command +
                '}';
    }
}
