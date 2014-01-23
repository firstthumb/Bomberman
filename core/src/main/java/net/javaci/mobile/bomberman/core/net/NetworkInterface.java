package net.javaci.mobile.bomberman.core.net;

import net.javaci.mobile.bomberman.core.net.models.RoomModel;

import java.util.List;

public interface NetworkInterface {

    public static final int MAX_USERS = 4;

    public static interface NetworkListener {
        public void onConnected();

        public void onDisconnected();

        public void onConnectionFailure(Exception e);

        public void onRoomListReceived(List<RoomModel> rooms);

        public void onRoomListRequestFailed();

        public void onRoomCreated(RoomModel room);

        public void onCreateRoomFailed();

        public void onRoomDeleted(String roomId);

        public void onDeleteRoomFailed();

        public void onJoinRoomSuccess(String roomId);

        public void onJoinRoomFailed();

        public void onMessageReceived(String from, String message);

        public void onPlayerJoinedRoom(RoomModel room, String playerName);

        public void onPlayerLeftRoom(RoomModel room, String playerName);

        public void onRoomInfoReceived(String [] players, String data);
    }

    public void addNetworkListener(NetworkListener listener);

    public void removeNetworkListener(NetworkListener listener);

    public void clearNetworkListeners();

    public void connect();

    public void disconnect();

    public void listRooms();

    public void createRoom(String roomName);

    public void deleteRoom(String roomId);

    public void joinRoom(String roomId);

    public void leaveRoom(String roomId);

    public void sendMessage(String message);

    public void sendMessageTo(String destination, String message);

    public void startGame(String roomId);

    public void getRoomInfo(String roomId);

    public boolean isConnected();

}
