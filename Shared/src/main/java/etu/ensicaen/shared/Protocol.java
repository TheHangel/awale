package etu.ensicaen.shared;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Protocol {
    public static void writeString(Socket socket, String s) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        out.writeInt(data.length);
        out.write(data);
    }

    public static String readString(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        int len = in.readInt();
        byte[] data = new byte[len];
        in.readFully(data);
        return new String(data, StandardCharsets.UTF_8);
    }
}