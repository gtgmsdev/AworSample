package com.gamatechno.awor.socket;

import android.content.Context;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketSingleton {

    private SocketSingleton instance;
    private Socket mSocket;
    private Context context;

    public SocketSingleton(Context context, String baseUrl, String access_token, String auth_token, String code) {
        this.context = context;
        this.mSocket = getServerSocket(baseUrl, access_token, auth_token, code);
    }

    public SocketSingleton get(Context context, String baseUrl, String access_token, String auth_token, String code) {
        if (instance == null) {
            instance = getSync(context, baseUrl,access_token, auth_token, code);
        }
        instance.context = context;
        return instance;
    }

    private synchronized SocketSingleton getSync(Context context, String baseUrl, String access_token, String auth_token, String code) {
        if (instance == null) {
            instance = new SocketSingleton(context, baseUrl, access_token, auth_token, code);
        }
        return instance;
    }

    public Socket getSocket() {
        return this.mSocket;
    }

    public Socket getServerSocket(String baseUrl, String access_token, String auth_token, String code) {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.path = "/ws/";
            opts.query = "access_token=" + access_token + "&auth_token=" + auth_token + "&code=" + code;
            mSocket = IO.socket(baseUrl + "meetroom", opts);
            Log.d("socketSingleton", "getServerSocket: " + baseUrl + "/meetroom" + "/ws/" + "access_token=" + access_token + "&auth_token=" + auth_token + "&code=" + code);
            return mSocket;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
