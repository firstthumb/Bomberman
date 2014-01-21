package net.javaci.mobile.bomberman.core.net.appwarp;


import net.javaci.mobile.bomberman.core.Synchronizer;
import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;
import net.javaci.mobile.bomberman.core.net.protocol.CommandFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class TestClient {
    private String username;
    private AppWarpClient warpClient;
    private Synchronizer synchronizer;

    public static void main(String[] args) throws Exception {
        TestClient client = new TestClient();
        client.start();
    }

    private void start() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String command;
        while ((command = in.readLine()) != null && command.length() != 0) {
            handleCommand(command.toLowerCase(Locale.US));
        }
    }

    private void handleCommand(String command) throws Exception {
        if (command.startsWith("connect")) {
            handleConnectCommand(command);
        } else if (command.startsWith("disconnect")) {
            handleDisconnectCommand();
        } else if (command.startsWith("create_room")) {
            handleCreateRoomCommand(command);
        } else if (command.startsWith("list_rooms")) {
            handleListRoomCommand();
        } else if (command.startsWith("join_room")) {
            handleJoinRoomCommand(command);
        } else if (command.startsWith("send_message_to")) {
            handleSendMessageToCommand(command);
        } else if (command.startsWith("send_message")) {
            handleSendMessageCommand(command);
        } else if (command.startsWith("clear_rooms")) {
            handleClearRooms();
        } else {
            log("Undefined command '" + command + "'");
        }
    }

    private void handleClearRooms() {
        warpClient.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onRoomListReceived(List<RoomModel> rooms) {
                log("Room list : ");
                for (RoomModel room : rooms) {
                    warpClient.deleteRoom(room.getId());
                }
            }

            @Override
            public void onRoomDeleted(String roomId) {
                log("Room deleted : " + roomId);
            }

            @Override
            public void onDisconnected() {
                warpClient.removeNetworkListener(this);
            }
        });
        warpClient.listRooms();
    }

    private void handleSendMessageCommand(String command) {
        String[] parts = command.split(" ");
        String message = parts[1];
        warpClient.sendMessage(message);
    }

    private void handleSendMessageToCommand(String command) {
        String[] parts = command.split(" ");
        String destination = parts[1];
        String message = parts[2];
        warpClient.sendMessageTo(destination, message);
    }

    private void handleJoinRoomCommand(String command) {
        String[] parts = command.split(" ");
        String roomId = parts[1];
        warpClient.joinRoom(roomId);
    }

    private void handleListRoomCommand() {
        warpClient.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onRoomListReceived(List<RoomModel> rooms) {
                log("Room list : ");
                for (RoomModel room : rooms) {
                    log(room.toString());
                }
                warpClient.removeNetworkListener(this);
            }
        });
        warpClient.listRooms();
    }

    private void handleCreateRoomCommand(String command) {
        String[] parts = command.split(" ");
        String roomName = parts[1];
        this.warpClient.createRoom(roomName);
    }

    private void handleDisconnectCommand() {
        warpClient.disconnect();
    }

    private void handleConnectCommand(String command) {
        String[] parts = command.split(" ");
        this.username = parts[1];
        log("Connecting with username : " + username);
        warpClient = new AppWarpClient(username);
        synchronizer = new Synchronizer(warpClient, new CommandFactory());
        warpClient.connect();
        warpClient.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onMessageReceived(String from, String message) {
                System.out.println("Command received : " + message + " from " + from);
            }

            @Override
            public void onPlayerJoinedRoom(RoomModel room, String playerName) {
                System.out.println(playerName + " joined " + room);
            }

            @Override
            public void onPlayerLeftRoom(RoomModel room, String playerName) {
                System.out.println(playerName + " left " + room);
            }
        });
    }

    public void log(String s) {
        System.out.println(Thread.currentThread().getName() + " : " + s);
    }

}
