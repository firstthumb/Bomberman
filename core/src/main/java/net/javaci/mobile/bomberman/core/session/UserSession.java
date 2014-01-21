package net.javaci.mobile.bomberman.core.session;

import net.javaci.mobile.bomberman.core.net.models.RoomModel;

public class UserSession {
    private long userId;
    private String username;
    private RoomModel room;

    public static final UserSession INSTANCE = new UserSession();

    public static UserSession getInstance() {
        return INSTANCE;
    }

    private UserSession() {
        this.userId = generateUserId();
        this.username = generateUsername();
    }

    private String generateUsername() {
        return "Player_" + generateUserId();
    }

    private long generateUserId() {
        return (long)(Math.random() * 1000000);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public boolean isOwnerRoom() {
        if (room != null) {
            return username.equals(room.getOwner());
        }

        return false;
    }
}
