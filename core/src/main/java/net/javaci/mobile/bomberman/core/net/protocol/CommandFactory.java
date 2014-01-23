package net.javaci.mobile.bomberman.core.net.protocol;

import net.javaci.mobile.bomberman.core.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private Map<Integer, Map<Integer, String>> messsages = new HashMap<Integer, Map<Integer, String>>();

    public Command createCommand(String jsonString) {
        Log.d("Received : " + jsonString);
        if (jsonString.startsWith("split")) {
            String arr[] = jsonString.split("#");
            int messageId = Integer.parseInt(arr[1]);
            int messageLength = Integer.parseInt(arr[2]);
            int messageIndex = Integer.parseInt(arr[3]);
            String message = arr[4];

            Map<Integer, String> map = messsages.get(messageId);
            if (map == null) {
                map = new HashMap<Integer, String>();
                messsages.put(messageId, map);
            }

            map.put(messageIndex, message);

            if (map.size() != messageLength) {
                return null;
            }

            jsonString = "";
            for (int i=0; i<=messageLength; i++) {
                jsonString += map.get(i);
            }

            messsages.remove(messageId);
        }

        try {
            JSONObject json = new JSONObject(jsonString);
            int command = json.getInt("command");
            switch (command) {
                case Command.CLOCK_SYNC_REQ:
                    return ClockSyncReqCommand.build(json);
                case Command.CLOCK_SYNC_RES:
                    return ClockSyncResCommand.build(json);
                case Command.MOVE_START:
                    return MoveCommand.build(json);
                case Command.MOVE_END:
                    return MoveEndCommand.build(json);
                case Command.CREATE_GAME:
                    return CreateGameCommand.build(json);
                case Command.MOVE_GHOST:
                    return MoveGhostCommand.build(json);
                case Command.GAME_END:
                    return GameEndCommand.build(json);
                case Command.DROP_BOMB:
                    return DropBombCommand.build(json);
                case Command.EXPLODE_BOMB:
                    return ExplodeBombCommand.build(json);
                case Command.CAUGHT_GHOST:
                    return GhostCaughtCommand.build(json);
                case Command.START_GAME:
                    return StartGameCommand.build(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new UndefinedCommand(jsonString);
    }
}
