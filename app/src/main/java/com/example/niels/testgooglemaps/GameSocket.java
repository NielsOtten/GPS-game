package com.example.niels.testgooglemaps;

import android.os.Vibrator;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class GameSocket {
    private static Socket socket = null;

    static Socket getInstance() {

        if(socket == null) {
            try {
                socket = IO.socket("http://cd41be73.ngrok.io");
                socket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return socket;
    }
}
