package net.javaci.mobile.bomberman.core.session;

public class UserSession {
    private long userId;
    private String username;

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
}
