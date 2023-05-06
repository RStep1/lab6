package run;

import processing.NBChannelController;

import utility.CommandArguments;
import utility.ServerAnswer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private static SocketChannel clientSocketChannel;

    public Client(String host, int port) throws IOException {
        clientSocketChannel = SocketChannel.open(new InetSocketAddress(host, port));
    }

    public static void stop() {
        try {
            clientSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketChannel getSocketChannel() {
        return clientSocketChannel;
    }

    public ServerAnswer dataExchange(CommandArguments request) {
        ServerAnswer serverAnswer;
        try {
            NBChannelController.write(clientSocketChannel, request);
            serverAnswer = (ServerAnswer) NBChannelController.read(clientSocketChannel);
        } catch (ClassCastException | IOException e) {
            return null;
        }
        return serverAnswer;
    }
}
