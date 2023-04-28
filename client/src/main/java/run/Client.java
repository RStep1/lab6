package run;

import org.apache.commons.lang3.SerializationUtils;
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

    public Client(String host, int port) throws IOException {

        client = SocketChannel.open(new InetSocketAddress(host, port));
        System.out.println("Local port is: "+client.getLocalAddress());
        buffer = ByteBuffer.allocate(256);
//        System.out.println("client =" + client);
    }

    public static void stop() {
        try {
            //client.close();
            client.finishConnect();
        } catch (IOException e) {
            System.out.println("Client is open: "+client.isConnected());
            e.printStackTrace();
        }
        System.out.println("Client is open: "+client.isConnected());
        buffer = null;
    }

    public SocketChannel getSocketChannel() {
        return client;
    }

    public ServerAnswer sendRequest(CommandArguments request) {
        ServerAnswer serverAnswer;
        try {
            byte[] objectBytes = SerializationUtils.serialize(request);
            buffer = ByteBuffer.wrap(objectBytes);
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            serverAnswer = SerializationUtils.deserialize(buffer.array());
            buffer.clear();
        } catch (ClassCastException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return serverAnswer;
    }
}
