package main;

public class User {
    private final String name;
    private final String connectionName;
    private String roomId;

    public String getName() {
        return name;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public User(String name, String connectionName) {
        this.name = name;
        this.connectionName = connectionName;
        this.roomId = "lobby";
    }
}
