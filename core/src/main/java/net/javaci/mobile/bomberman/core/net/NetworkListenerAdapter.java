package net.javaci.mobile.bomberman.core.net;

import net.javaci.mobile.bomberman.core.net.NetworkInterface;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;

import java.util.List;

public class NetworkListenerAdapter implements NetworkInterface.NetworkListener {
    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailure(Exception e) {

    }

    @Override
    public void onRoomListReceived(List<RoomModel> rooms) {

    }

    @Override
    public void onRoomListRequestFailed() {

    }

    @Override
    public void onRoomCreated(RoomModel room) {

    }

    @Override
    public void onCreateRoomFailed() {

    }

    @Override
    public void onRoomDeleted(String roomId) {

    }

    @Override
    public void onDeleteRoomFailed() {

    }

    @Override
    public void onJoinRoomSuccess(String roomId) {

    }

    @Override
    public void onJoinRoomFailed() {

    }

    @Override
    public void onMessageReceived(String from, String message) {

    }

    @Override
    public void onPlayerJoinedRoom(RoomModel room, String playerName) {

    }

    @Override
    public void onPlayerLeftRoom(RoomModel room, String playerName) {

    }
}
