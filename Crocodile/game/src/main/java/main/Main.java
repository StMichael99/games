package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        GameServer server = new GameServer(8189);
        server.start();

        BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
        while ( true ) {
            String in = sysin.readLine();

            if ( in.equals("all") ) {
                server.showAllUsers();
            }

            if( in.equals( "exit" ) ) {
                server.stop(1000);
                break;
            }

            server.broadcast( in );
        }
    }
}
