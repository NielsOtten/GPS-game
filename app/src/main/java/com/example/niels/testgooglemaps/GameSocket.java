package com.example.niels.testgooglemaps;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

class GameSocket {
    private static Socket socket = null;

    static Socket getInstance() {

        if(socket == null) {
            try {
                socket = IO.socket("https://yyvszwsrug.localtunnel.me");
                socket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return socket;
    }
}
