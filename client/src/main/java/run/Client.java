package run;

import processing.NBChannelController;

import utility.CommandArguments;
import utility.ServerAnswer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private static SocketChannel client;

    public Client(String host, int port) throws IOException {
        client = SocketChannel.open(new InetSocketAddress(host, port));
    }

    public static void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketChannel getSocketChannel() {
        return client;
    }

    public ServerAnswer dataExchange(CommandArguments request) {
        ServerAnswer serverAnswer;
        try {
            NBChannelController.write(client, request);
            serverAnswer = (ServerAnswer) NBChannelController.read(client);
        } catch (ClassCastException | IOException e) {
            return null;
        }
        return serverAnswer;
    }
}
