import socket
from _thread import *
from player import Player
from bullet import Bullet
import pickle

server = "192.168.0.122"
port = 5555

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
    s.bind((server, port))
except socket.error as e:
    str(e)

s.listen(4)
print("Waiting for a connection, Server Started")


players = [ Player(0, 0, 255, 0, 0, 5), Player(0, 450, 0, 255, 0, 5), Player(450, 0, 0, 0, 255, 5),  Player( 450,450, 0, 200, 200, 5) ]
bullets = [None, None, None, None]


def threaded_client(conn, player):
    global players_data
    conn.send( pickle.dumps([player, players[player]]))
    reply = " "
    while True:
        try:
            data = pickle.loads(conn.recv(2048))
            players[data[0]] = data[1]
            bullets[data[0]] = data[2]
            
            if( not data ):
                print("Disconnected")
                break
            else:
                if player == 0:
                    #players[1] = data[0]
                    #players[2] = data[1]
                    #players[3] = data[2]
                    reply = [ [1,players[1],bullets[1]], [2,players[2], bullets[2]],  [3, players[3], bullets[3]] ]
                if player == 1:
                    #players[0] = data[0]
                    #players[2] = data[1]
                    #players[3] = data[2]
                    reply = [ [0,players[0],bullets[0]],   [2,players[2], bullets[2]],  [3, players[3], bullets[3]] ]
                if player == 2:
                    #players[0] = data[0]
                    #players[1] = data[1]
                    #players[3] = data[2]
                    reply = [  [0,players[0],bullets[0]],  [1,players[1], bullets[1]],   [3, players[3], bullets[3]]  ]
                if player == 3:
                    #players[0] = data[0]
                    #players[1] = data[1]
                    #players[2] = data[2]
                    reply = [ [0,players[0],bullets[0]], [1,players[1], bullets[1]],  [2, players[2], bullets[2]] ]

                #print("Received: ", data)
                #print("Sending : ", reply)

            conn.sendall(pickle.dumps(reply))
        except:
            break

    print("Lost connection")
    conn.close()
current_player = 0
while True:
    conn, addr = s.accept()
    print("Connected to:", addr)

    start_new_thread(threaded_client, (conn, current_player) )
    current_player +=1