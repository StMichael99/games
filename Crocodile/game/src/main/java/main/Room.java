package main;

import java.util.HashMap;

public class Room {
    private final String id;
    private HashMap<String, User> connectedUsers = new HashMap<>();
    private String canvasData = "";
    private boolean GameStatus = false;
    private String word = "Motzart";
    private User user;


    public void connectUser(User user) {
        connectedUsers.put(user.getConnectionName(), user);
    }

    public void removeUser(User user) {
        connectedUsers.remove(user.getConnectionName());
    }

    public void updateCanvas(String canvasData) {
        this.canvasData = canvasData;
    }
    // вставить
    public void setGameStatus(boolean parametr){ this.GameStatus = parametr; }

    public void setWord( String word ){ this.word = word;}

    public void setUser( User user){ this.user = user;}

    public void setCanvasData(String canvasData) {
        this.canvasData = canvasData;
    }
    //
    public Room(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, User> getConnectedUsers() {
        return connectedUsers;
    }

    public String getCanvasData() {
        return canvasData;
    }
    // вставить
    public boolean getGameStatus(){ return GameStatus; }
    public String getWord(){ return word; }

    public User getUser() {
        return user;
    }
}