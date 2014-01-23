package net.javaci.mobile.bomberman.core.net.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.ConnectionState;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;

import java.util.*;

public class AppWarpClient implements NetworkInterface {
    public static final String APPWARP_API_KEY = "3e184ce0a4b84ba029594a12818ad86494c6b2ee44d6a6f7b41ba94e72f18111";
    public static final String APPWARP_SECRET_KEY = "a7792638eb60dfb16abfd03473b5e8bd7e643782d08bef9071ece4226c852f5d";
    public static final String GAME_STARTED_KEY = "GAME_STARTED";
    private final String username;
    private Set<NetworkListener> networkListeners = Collections.synchronizedSet(new HashSet<NetworkListener>());
    private WarpClient warpClient;
    private HashMap<String, Object> roomFilterForStartedGames = new HashMap<String, Object>() {{
        put(GAME_STARTED_KEY, true);
    }};
    private HashMap<String, Object> roomFilterForNotStartedGames = new HashMap<String, Object>() {{
        put(GAME_STARTED_KEY, false);
    }};

    public AppWarpClient(String username) {
        this.username = username;
        roomFilterForNotStartedGames.put(GAME_STARTED_KEY, false);
        roomFilterForStartedGames.put(GAME_STARTED_KEY, true);
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public void addNetworkListener(NetworkListener listener) {
        this.networkListeners.add(listener);
    }

    @Override
    public void removeNetworkListener(NetworkListener listener) {
        this.networkListeners.remove(listener);
    }

    @Override
    public void clearNetworkListeners() {
        this.networkListeners.clear();
    }

    @Override
    public void connect() {
        WarpClient.initialize(APPWARP_API_KEY, APPWARP_SECRET_KEY);
        try {
            this.warpClient = WarpClient.getInstance();
        } catch (Exception e) {
            for (NetworkListener listener : this.networkListeners) {
                listener.onConnectionFailure(e);
            }
            return;
        }
        addConnectionRequestListener();

        addZoneRequestListener();

        addRoomRequestListener();

        addNotificationListener();

        warpClient.connectWithUserName(username);
    }

    private void addNotificationListener() {
        warpClient.addNotificationListener(new NotifyListenerAdapter() {
            @Override
            public void onChatReceived(ChatEvent chatEvent) {
                for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                    listener.onMessageReceived(chatEvent.getSender(), chatEvent.getMessage());
                }
            }

            @Override
            public void onUserJoinedRoom(RoomData roomData, String user) {
                for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                    listener.onPlayerJoinedRoom(createRoomModelFromRoomData(roomData), user);
                }
            }

            @Override
            public void onUserLeftRoom(RoomData roomData, String user) {
                for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                    listener.onPlayerLeftRoom(createRoomModelFromRoomData(roomData), user);
                }
            }

            @Override
            public void onPrivateChatReceived(String from, String message) {
                for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                    listener.onMessageReceived(from, message);
                }
            }
        });
    }

    private void addRoomRequestListener() {
        warpClient.addRoomRequestListener(new RoomRequestListenerAdapter() {
            @Override
            public void onJoinRoomDone(RoomEvent roomEvent) {
                if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                    warpClient.subscribeRoom(roomEvent.getData().getId());
                    log("Successfuly joined room " + roomEvent.getData().getId());
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onJoinRoomSuccess(roomEvent.getData().getId());
                    }
                } else {
                    log("Failed to join room.");
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onJoinRoomFailed();
                    }
                }
            }

            @Override
            public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
                if (liveRoomInfoEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onRoomInfoReceived(liveRoomInfoEvent.getJoinedUsers(), liveRoomInfoEvent.getCustomData());
                    }
                } else {
                    log("getLiveRoomInfo failed.");
                }
            }
        });
    }

    private void addZoneRequestListener() {
        this.warpClient.addZoneRequestListener(new ZoneRequestListenerAdapter() {
            @Override
            public void onGetMatchedRoomsDone(MatchedRoomsEvent matchedRoomsEvent) {
                if (matchedRoomsEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                    RoomData[] roomsData = matchedRoomsEvent.getRoomsData();
                    List<RoomModel> rooms = new ArrayList<RoomModel>();
                    if (roomsData != null) {
                        for (RoomData roomData : roomsData) {
                            RoomModel room = createRoomModelFromRoomData(roomData);
                            rooms.add(room);
                        }
                    }
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onRoomListReceived(rooms);
                    }
                } else {
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onRoomListRequestFailed();
                    }
                }
            }

            @Override
            public void onCreateRoomDone(RoomEvent roomEvent) {
                if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                    RoomModel roomModel = createRoomModelFromRoomData(roomEvent.getData());
                    log("Room created : " + roomModel);
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onRoomCreated(roomModel);
                    }
                } else {
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onCreateRoomFailed();
                    }
                }
            }

            @Override
            public void onDeleteRoomDone(RoomEvent roomEvent) {
                if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onRoomDeleted(roomEvent.getData().getId());
                    }
                } else {
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onDeleteRoomFailed();
                    }
                }
            }

        });
    }

    private void addConnectionRequestListener() {
        warpClient.addConnectionRequestListener(new ConnectionRequestListener() {
            @Override
            public void onConnectDone(ConnectEvent connectEvent) {
                if (connectEvent.getResult() == WarpResponseResultCode.SUCCESS ||
                        connectEvent.getResult() == WarpResponseResultCode.BAD_REQUEST ||
                        connectEvent.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED) {
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onConnected();
                    }
                    log("Connection established.");
                }
                else {
                    for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                        listener.onDisconnected();
                    }
                    log("Disconnected REASON : " + connectEvent.getResult());
                }
            }

            @Override
            public void onDisconnectDone(ConnectEvent connectEvent) {
                for (NetworkListener listener : networkListeners.toArray(new NetworkListener[0])) {
                    listener.onDisconnected();
                }
                log("Disconnected.");
            }

            @Override
            public void onInitUDPDone(byte b) {
            }
        });
    }

    @Override
    public void disconnect() {
        log("disconnect");
        warpClient.disconnect();
    }

    @Override
    public void listRooms() {
        log("listRooms");
        warpClient.getRoomWithProperties(roomFilterForNotStartedGames);

    }

    @Override
    public void createRoom(String roomName) {
        warpClient.createRoom(roomName, this.username, MAX_USERS, this.roomFilterForNotStartedGames);
    }

    @Override
    public void deleteRoom(String roomId) {
        warpClient.deleteRoom(roomId);
    }

    @Override
    public void joinRoom(String roomId) {
        warpClient.joinRoom(roomId);
    }

    @Override
    public void leaveRoom(String roomId) {
        warpClient.leaveRoom(roomId);
    }

    @Override
    public void sendMessage(String message) {
        log("Sending message " + message);
        warpClient.sendChat(message);
    }

    @Override
    public void sendMessageTo(String destination, String message) {
        log("Sending message " + message + " to " + destination);
        warpClient.sendPrivateChat(destination, message);
    }

    @Override
    public void startGame(String roomId) {
        warpClient.updateRoomProperties(roomId, roomFilterForStartedGames, new String[0]);
    }

    @Override
    public void getRoomInfo(String roomId) {
        warpClient.getLiveRoomInfo(roomId);
    }

    @Override
    public boolean isConnected() {
        return warpClient.getConnectionState() == ConnectionState.CONNECTED;
    }

    private RoomModel createRoomModelFromRoomData(RoomData roomData) {
        RoomModel room = new RoomModel();
        room.setName(roomData.getName());
        room.setId(roomData.getId());
        room.setOwner(roomData.getRoomOwner());
        return room;
    }

    private void log(String s) {
        System.out.println(s);
    }
}
