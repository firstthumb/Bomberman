package net.javaci.mobile.bomberman.core.net.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandFactory {

    public Command createCommand(String jsonString) {
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

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new UndefinedCommand(jsonString);
    }
}
