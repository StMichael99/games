import pygame
from network import Network
from player import Player
from bullet import Bullet
import sys
from _thread import *
import time
bullet = None
Number = None
q = True
player = None
metka = True
#x = int(sys.argv[1])
#y = int(sys.argv[2]) 
#Col = ( int(sys.argv[3]), int(sys.argv[4]), int(sys.argv[5]) )


WHITE = (255, 255, 255)
width = 500
height = 500
win = pygame.display.set_mode((width, height))
pygame.display.set_caption("Client")


def enemy_target(player, bullets):
    for bullet in bullets:
        if( bullet != None):
            if( ( bullet.x < player.x+50 ) and (bullet.x > player.x) and (bullet.y < player.y+50 ) and(bullet.y > player.y) ):
                player.health -=1
                print('your health', player.health)

def my_target(players, bullet):
    for player in players:
        if( ( bullet.x < player.x+50 ) and (bullet.x > player.x) and (bullet.y < player.y+50 ) and(bullet.y > player.y) ):
            print('nice shot')
            print(len(players))
            return True
    return False


def move_player():
    global player
    global width
    global height
    player.move(width-50,height-50)
    
def check_bullet_or_player(bullet):
    if( ( bullet.x > width ) or ( bullet.x < 0)):
        return True
    if( (bullet.y> height ) or ( bullet.y < 0) ):
        return True
    return False


def redrawWindow_player( players, bullets ):
    global player
    global bullet
    players.append(player)
    bullets.append(bullet)
    win.fill(WHITE)
    for player in players: 
        player.draw(win)
    for bullet in bullets:
        if( bullet != None):
            bullet.draw(win)
    pygame.display.update()

    players.pop(len(players)-1)
    bullets.pop(len(bullets)-1)



def redrawWindow_bullet(players, network):
    global q
    global bullet
    global Number
    global player
    global metka
    clock = pygame.time.Clock()
    while True:
        #win.fill(WHITE)
        #redrawWindow_player(players, [bullet])
        #network.send([Number, players[0], bullet])
        #bullet.draw(win)
        if( bullet != None ):
            bullet.move()
            if( check_bullet_or_player(bullet) or my_target(players, bullet) ):
                network.send([Number, player, bullet])
                q = True
                metka = True
                bullet = None
                return True
            clock.tick(400)
        else:
            break
 
    
def main():
    run = True
    network = Network()
    p = network.get_player()
    global player
    player = p[1]
    clock = pygame.time.Clock()
    global Number
    Number = p[0]
    global q
    global bullet
    global metka
    while run:
        clock.tick(60)
        players = []

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False
                pygame.quit()


        if( player.health < 1):
            player.color = (0,0,0)
            redrawWindow_player(players, bullets)
            network.send([Number, player, bullet])
            pygame.quit()
            print( "You Lose")
            break




        keys = pygame.key.get_pressed()
        
        if( q ):
            if( keys[pygame.K_a]):
                bullet = Bullet(player.x+int(player.width/2), player.y+int(player.height/2), 1 ,player.color)
                #n.send_bullet(bullet, 1)
                q = False
            if( keys[pygame.K_w]):
                bullet = Bullet(player.x+int(player.width/2), player.y+int(player.height/2), 4 ,player.color)
                #n.send_bullet(bullet, 2)
                q = False
            if( keys[pygame.K_d]):
                q = False
                bullet = Bullet(player.x+int(player.width/2), player.y+int(player.height/2), 3 ,player.color)
                #n.send_bullet(bullet, 3)
                print('d')
            if( keys[pygame.K_s]):
                q = False
                bullet = Bullet(player.x+int(player.width/2), player.y+int(player.height/2), 2 ,player.color)
                #n.send_bullet(bullet, 4)

        #if( bullet == None ):
        network.send([Number, player, bullet])
        


        data = network.receive()
        players = [ data[0][1], data[1][1], data[2][1]]
        bullets = [ data[0][2], data[1][2], data[2][2]]

        enemy_target(player, bullets)

        #print( 'here')
        if(( bullet != None) and (metka == True)):
            start_new_thread( redrawWindow_bullet, (players, network ) )
            metka = False
        



        move_player()
        

        redrawWindow_player(players, bullets)
        
            

        

main()