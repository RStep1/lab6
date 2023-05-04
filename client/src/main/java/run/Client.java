package run;

import mods.ClientRequestType;
import mods.ExecuteMode;
import org.apache.commons.lang3.SerializationUtils;
import utility.CommandArguments;
import utility.ServerAnswer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static SocketChannel client;
    private static ByteBuffer buffer;

    public Client(String host, int port) throws IOException {

        client = SocketChannel.open(new InetSocketAddress(host, port));
        // System.out.println("Local port is: "+client.getLocalAddress());
        buffer = ByteBuffer.allocate(100000);
//        System.out.println("client =" + client);
    }

    public static void stop() {
        try {
            client.close();
//            client.finishConnect();
        } catch (IOException e) {
            // System.out.println("Client is open: "+client.isConnected());
            e.printStackTrace();
        }
        // System.out.println("Client is open: "+client.isConnected());
        buffer = null;
    }

    public SocketChannel getSocketChannel() {
        return client;
    }

    public ServerAnswer dataExchange(CommandArguments request) {
        ServerAnswer serverAnswer;
        try {
            byte[] objectBytes = SerializationUtils.serialize(request);
            buffer = ByteBuffer.wrap(objectBytes);
            client.write(buffer);
            buffer.clear();
            buffer = ByteBuffer.allocate(100000);
            client.read(buffer);
            // System.out.println("buffer: " + buffer.toString());
            serverAnswer = SerializationUtils.deserialize(buffer.array());
            buffer.clear();
        } catch (ClassCastException e) {
            // System.out.println("1 " + e);
            return null;
        } catch (IOException e) {
            // System.out.println("2 " + e);
            return null;
        }
        return serverAnswer;
    }
}
