package main;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class GameServer extends WebSocketServer {
    private HashMap<String, User> allRegisteredUsers = new HashMap<>();
    private HashMap<String, User> usersOnline = new HashMap<>();
    private HashMap<String, Integer> allConnections = new HashMap<>();
    private HashMap<String, ArrayList<WebSocket>> sockets = new HashMap<>();
    private HashMap<String, Room> createdRooms = new HashMap<>();

    public GameServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println(getHostName(conn) + " was connected..");

        String hostName = getHostName(conn);

        if (sockets.get(hostName) != null) {
            sockets.get(hostName).add(conn);
        } else {
            ArrayList<WebSocket> socketsList = new ArrayList<>();
            socketsList.add(conn);
            sockets.put(hostName, socketsList);
        }

        if (!allRegisteredUsers.containsKey(hostName) || allRegisteredUsers.get(hostName).getRoomId().equals("lobby")) {
            sendUsers("lobby");
            sendRooms("lobby");
        }
        if (!allRegisteredUsers.containsKey(hostName)) {
            conn.send("REGISTER");
        } else {
            userConnectedNotify(conn);
        }
    }

    public void showAllUsers() {
        System.out.print("\n===================");
        for (Room createdRoom : createdRooms.values()) {
            System.out.print("\n" + createdRoom.getId() + " room's user list:" );
            createdRoom.getConnectedUsers().values().forEach(element -> System.out.print(element.getName() + " "));
        }
        System.out.print("\n===================\n");
        for (User user : allRegisteredUsers.values()) {
            System.out.print("\n" + user.getName() + " : " + user.getRoomId());
        }
        System.out.print("\n===================\n");
    }

    private void userConnectedNotify(WebSocket conn) {
        String hostName = getHostName(conn); // берем айдишник подключившегося

        usersOnline.put(hostName, allRegisteredUsers.get(hostName)); // добавляем в мапу тех юзеров, которые онлайн

        // Добавляем +1 подключение (вклад очки) к конкретному айпи
        if (allConnections.containsKey(hostName)) { // если чуваку же подключен с другой вкладки, просто добавляем +1 коннекшн с его айпи
            allConnections.put(hostName, allConnections.get(hostName) + 1);
        } else {
            allConnections.put(hostName, 1);
        }

        sendUsers("lobby");
        sendRooms("lobby");
    }

    private void userDisconnectedNotify(WebSocket conn) {
        String hostName = getHostName(conn);

        allConnections.put(hostName, allConnections.get(hostName) - 1);

        if (allConnections.get(hostName) == 0) {
            User leftUser = usersOnline.get(hostName);

            createdRooms.get(leftUser.getRoomId()).removeUser(leftUser);
            broadcast(leftUser.getName() + " вышел из комнаты..", getUsersConnectionsInRoom(leftUser.getRoomId()));

            leftUser.setRoomId("");

            System.out.println("User " + hostName + " was TOTALLY disconnected..");
            usersOnline.remove(hostName);
            sendUsers("lobby");
        }
    }

    private void sendUsers(String roomId) {
        String usersNames = "";
        for(User user : usersOnline.values()) {
            usersNames += user.getName() + ",";
        }

        if (usersNames.length() != 0) {
            usersNames = usersNames.substring(0, usersNames.length() - 1);
        }

        System.out.println("Sending users list...");

        broadcast("users_list:" + usersNames, getUsersConnectionsInRoom(roomId));
    }

    private void sendRooms(String roomId) {
        String roomsIds = "";
        for(Room room : createdRooms.values()) {
            roomsIds += room.getId() + ",";
        }

        if (roomsIds.length() != 0) {
            roomsIds = roomsIds.substring(0, roomsIds.length() - 1);
        }

        System.out.println("Sending rooms list...");

        broadcast("rooms_list:" + roomsIds, getUsersConnectionsInRoom(roomId));
    }

    public ArrayList<WebSocket> getUsersConnectionsInRoom(String roomId) {
        Room room = createdRooms.get(roomId);

        ArrayList<WebSocket> socketsToReturn = new ArrayList<>();
        for (User user : usersOnline.values()) {
            if (user.getRoomId().equals(room.getId())) {
                socketsToReturn.addAll(sockets.get(user.getConnectionName()));
            }
        }
        return socketsToReturn;
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        System.out.println("User " + getHostName(conn) + " closed a tab..");

        userDisconnectedNotify(conn);
    }

    // вставить
//
//
//
    public User randomUser(Room room){
        HashMap<String, User> users = room.getConnectedUsers();
        ArrayList<User> us = new ArrayList<>(users.values());
        int random_int = (int)(Math.random() * (us.size()));
        return us.get(random_int);
    }
    public String randomWord(){
        String[] words = {"цветок", "машина", "ключи"};
        int random_int = (int)( Math.random() * (words.length));
        return words[random_int];
    }
/////

    @Override
    public void onMessage( WebSocket conn, String message ) {
        String hostName = getHostName(conn);
        System.out.println("\nMESSAGE FROM CONNECTION: " + message + "\n");
        if (message.contains("user:")) {  // регистрация нового юзера
            message = message.replace("user:", "");

            System.out.println("\nNew user " + message + " was registered..\n");

            User user = new User(message, hostName);
            allRegisteredUsers.put(user.getConnectionName(), user);
            createdRooms.get("lobby").connectUser(user);
            userConnectedNotify(conn); // оповещаем о присоединении нового юзера
        } else if (message.contains("room:")) {
            message = message.replace("room:", "");

            System.out.println("\nNew room " + message + " was created..\n");

            Room newRoom = new Room(message);
            createdRooms.put(message, newRoom);
            sendRooms("lobby");
        } else if (message.contains("user_connect:")) {
            message = message.replace("user_connect:", "");

            System.out.println("\n" + allRegisteredUsers.get(hostName).getName() + " entered to " + message + "\n");

            User redirectedUser = allRegisteredUsers.get(hostName);
            createdRooms.get(message).connectUser(redirectedUser);
        } else if (message.contains("set_room_name:")) {
            message = message.replace("set_room_name:", "");
            User redirectedUser = allRegisteredUsers.get(hostName);

            if (redirectedUser == null) return; // если зайти на дроу без регистрации, то ничего не делаем

            redirectedUser.setRoomId(message);

            createdRooms.get(message).connectUser(redirectedUser);

            broadcast(usersOnline.get(hostName).getName() + " присоединился к комнате..",
                    getUsersConnectionsInRoom(message));

            broadcast(createdRooms.get(message).getCanvasData(),
                    getUsersConnectionsInRoom(message));

            if ((createdRooms.get(message).getConnectedUsers().size() >= 1 ) && (createdRooms.get(message).getGameStatus() == false) && (!message.equals("lobby"))){
                createdRooms.get(message).setGameStatus(true);
                createdRooms.get(message).setWord(randomWord());
                createdRooms.get(message).setUser(allRegisteredUsers.get(hostName));
                broadcast( "secret:"+createdRooms.get(message).getWord() , sockets.get(createdRooms.get(message).getUser().getConnectionName()) );
            } else if (message.equals("lobby")) {
                sendUsers("lobby");
                sendRooms("lobby");
            }
        } else if (message.contains("objects")) {
            User user = usersOnline.get(hostName);
            String roomId = user.getRoomId();
            createdRooms.get(roomId).setCanvasData(message);
            broadcast( message, getUsersConnectionsInRoom(roomId));
        } else {
            User user = usersOnline.get(hostName);
            String roomId = user.getRoomId();
            broadcast(user.getName() + ": " + message, getUsersConnectionsInRoom(roomId));
            if( message.contains(createdRooms.get(roomId).getWord()) ){
                broadcast( user.getName() +" is Winner \n New game", getUsersConnectionsInRoom(roomId));
                createdRooms.get(roomId).setWord(randomWord());
                createdRooms.get(roomId).setUser(randomUser(createdRooms.get(roomId)));
                broadcast( "secret:"+createdRooms.get(roomId).getWord() , sockets.get(createdRooms.get(roomId).getUser().getConnectionName()) );
            }
        }
    }

    private String getHostName(WebSocket connection) {
        return connection.getLocalSocketAddress().getHostString();
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        //broadcast( connectedUsers.get(conn.toString()) + ": " + message.array() );
        //System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        Room lobby = new Room("lobby");
        createdRooms.put("lobby", lobby);

        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}