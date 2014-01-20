package net.javaci.mobile.bomberman.core;

import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.appwarp.AppWarpClient;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;
import net.javaci.mobile.bomberman.core.net.protocol.ClockSyncReqCommand;
import net.javaci.mobile.bomberman.core.net.protocol.ClockSyncResCommand;
import net.javaci.mobile.bomberman.core.net.protocol.Command;
import net.javaci.mobile.bomberman.core.net.protocol.CommandFactory;

import java.util.*;

public class Synchronizer {
    public static final int MAX_NUMBER_OF_SYNC_MESSAGES = 21;
    private AppWarpClient appWarpClient;
    private CommandFactory commandFactory;
    private Map<String, Integer> clockDiffs = Collections.synchronizedMap(new HashMap<String, Integer>());
    private Map<String, Integer> syncMessageCountPerUser = Collections.synchronizedMap(new HashMap<String, Integer>());
    private Map<String, List<Integer>> collectedClockDiffsPerUser = Collections.synchronizedMap(new HashMap<String, List<Integer>>());
    private Map<String, ClockSyncReqCommand> waitingSyncRequests = Collections.synchronizedMap(new HashMap<String, ClockSyncReqCommand>());

    public Synchronizer(AppWarpClient appWarpClient, final CommandFactory commandFactory) {
        this.appWarpClient = appWarpClient;
        this.commandFactory = commandFactory;

        this.appWarpClient.addNetworkListener(new NetworkListenerAdapter() {

            @Override
            public void onPlayerJoinedRoom(RoomModel room, String playerName) {
                startSyncClocksWithPlayer(playerName);
            }

            @Override
            public void onMessageReceived(String from, String message) {
                Command command = commandFactory.createCommand(message);
                switch (command.getCommand()) {
                    case Command.CLOCK_SYNC_REQ:
                        handleClockSyncRequest((ClockSyncReqCommand) command);
                        break;
                    case Command.CLOCK_SYNC_RES:
                        handleClockSyncResponse((ClockSyncResCommand) command);
                        break;
                    default:
                        //log

                }
            }
        });
    }

    private void handleClockSyncRequest(ClockSyncReqCommand command) {
        log("Clock sync req. received : " + command);
        ClockSyncResCommand clockSyncResCommand = new ClockSyncResCommand();
        clockSyncResCommand.setFromUser(appWarpClient.getUsername());
        clockSyncResCommand.setInitialTimestamp(command.getTimestamp());
        appWarpClient.sendMessageTo(command.getFromUser(), clockSyncResCommand.serialize());
    }

    private void handleClockSyncResponse(ClockSyncResCommand command) {
        log("Clock sync res. received : " + command);
        String targetUser = command.getFromUser();
        long now = System.currentTimeMillis();
        long roundTripTime = now - command.getInitialTimestamp();
        System.out.println("Round Trip Time : " + roundTripTime);
        long peerClock = command.getTimestamp() + roundTripTime / 2;
        int diff = (int)(now - peerClock);
        List<Integer> list = this.collectedClockDiffsPerUser.get(targetUser);
        if (list == null) {
            list = new ArrayList<Integer>();
            this.collectedClockDiffsPerUser.put(targetUser, list);
        }
        list.add(diff);

        int count = 0;
        if (this.syncMessageCountPerUser.containsKey(targetUser)) {
            count = this.syncMessageCountPerUser.get(targetUser);
        }
        count = count + 1;
        this.syncMessageCountPerUser.put(targetUser, count);
        if (count < MAX_NUMBER_OF_SYNC_MESSAGES) {
            startSyncClocksWithPlayer(targetUser);
        } else {
            calculateAverageClockDiff(targetUser);
        }
    }

    private void calculateAverageClockDiff(String targetUser) {
        //calculate average diff
        List<Integer> list = this.collectedClockDiffsPerUser.get(targetUser);
        Collections.sort(list);
        Integer clockDiff = list.get((list.size() / 2 + 1));
        log("Average clock diff for user " + targetUser + " is " + clockDiff);
        this.clockDiffs.put(targetUser, clockDiff);
    }

    private void startSyncClocksWithPlayer(String playerName) {
        ClockSyncReqCommand clockSyncCommand = new ClockSyncReqCommand();
        clockSyncCommand.setFromUser(this.appWarpClient.getUsername());
        waitingSyncRequests.put(playerName, clockSyncCommand);
        appWarpClient.sendMessageTo(playerName, clockSyncCommand.serialize());
    }

    private void log(String message) {
        System.out.println(System.currentTimeMillis() + " - " + message);
    }

    public long getClockDiff(String username) {
        if (clockDiffs.containsKey(username)) {
            return clockDiffs.get(username);
        }
        return 0;
    }
}
