package run;

import processing.Serializator;
import utility.CommandArguments;
import utility.ServerAnswer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static Client instance;

    private Client(String host, int port) {
        try {
            client = SocketChannel.open(new InetSocketAddress(host, port));
            buffer = ByteBuffer.allocate(256);
        } catch (ConnectException e) {
            System.out.println("SERVER NOT ANSWER");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Client start(String host, int port) {
        if (instance == null) {
            instance = new Client(host, port);
        }
        if (client == null) {
            return null;
        }
        return instance;
    }

    public static void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = null;
    }

    public ServerAnswer sendRequest(CommandArguments request) {
        ServerAnswer serverAnswer = null;
        try {
            buffer = Serializator.serialize(request);
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            serverAnswer = Serializator.deserialize(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverAnswer;
    }
}
