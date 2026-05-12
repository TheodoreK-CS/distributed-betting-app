package com.example.tzogos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import MessageEssentials.Message;

public class NetworkClient {
    // Android emulator -> host machine. Change to your computer LAN IP for a real phone.
    public static String SERVER_IP = "10.0.2.2";
    public static int SERVER_PORT = 8000;

    public static Message send(Message request) throws Exception {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        socket.setSoTimeout(10000);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();

        in.close();
        out.close();
        socket.close();

        return response;
    }
}
