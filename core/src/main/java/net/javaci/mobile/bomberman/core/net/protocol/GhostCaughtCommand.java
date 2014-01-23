package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GhostCaughtCommand extends Command {
    private int id;
    private List<String> caughtPlayers = new ArrayList<String>();

    public static Command build(JSONObject json) {
        GhostCaughtCommand command = new GhostCaughtCommand();
        try {
            command.parseCommonFields(json);
            command.id = json.getInt("id");
            {
                JSONArray jsonArray = json.getJSONArray("caughtPlayers");
                if (jsonArray != null) {
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        command.caughtPlayers.add(jsonArray.getString(i));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new UndefinedCommand(json.toString());

        }
        return command;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getCaughtPlayers() {
        return caughtPlayers;
    }

    public void setCaughtPlayers(List<String> caughtPlayers) {
        this.caughtPlayers = caughtPlayers;
    }

    @Override
    protected void serializeCustomFields(JSONObject json) throws JSONException {
        json.put("id", id);
        JSONArray jsonArray = new JSONArray();
        for (String playerName : caughtPlayers) {
            jsonArray.put(playerName);
        }
        json.put("caughtPlayers", jsonArray);

    }

    @Override
    public int getCommand() {
        return CAUGHT_GHOST;
    }
}
