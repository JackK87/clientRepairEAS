package ru.jackK.repairEAS.client.io;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

public class Client {

    private Socket socket;
    private String uri;
    private String username;

    public Client(String uri, String username, String password) {
        try {
            this.uri = uri;
            this.username = username;
            this.socket = IO.socket(uri);
            this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JSONObject obj = new JSONObject();

                    HashMap<String, String> msg = new HashMap<>();

                    msg.put("to", "XFXwMofo7DshMIzgAAAB");
                    msg.put("text", "Привет");

                    HashMap<String, String> usr = new HashMap<>();

                    usr.put("name", username);
                    usr.put("pwd", password);

                    try {
                        obj.put("message", msg);
                        obj.put("user", usr);
                    } catch (JSONException jsonEx) {
                        System.out.println(jsonEx);
                    }

                    socket.emit("on_chat_message", obj);
                }

            }).on("connect_users", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    System.out.println(objects);
                }
            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    System.out.println(objects);
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            });

        } catch (URISyntaxException uriEx) {
            System.out.println(uriEx);
        }
    }

    public void Connect() {

        if (socket != null)
            socket.connect();
    }

    public void  Disconnect() {
        if (socket != null)
            socket.close();
    }

    @Override
    public String toString() {
        return "пользователь: " + this.username + " строке подключения: " + this.uri;
    }
}
