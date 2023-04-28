package run;

import com.google.gson.stream.JsonToken;
import commands.ExitCommand;
import mods.AnswerType;
import mods.MessageType;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import processing.Serializator;
import utility.CommandArguments;
import utility.MessageHolder;
import utility.ServerAnswer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static final String host = "localhost";
    private static final int port = 15454;
    private final ArrayList<SocketChannel> socketChannels = new ArrayList<>();
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ByteBuffer buffer;
    private RequestHandler requestHandler;

    public Server(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    private void setup() {
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocket.bind(new InetSocketAddress(host, port));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            buffer = ByteBuffer.allocate(2048);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        setup();
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                        register(selector, serverSocket);
                    }
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        System.out.println("client = " + client);
                        System.out.println("Client is connected by: "+client.isConnected());
                        answer(buffer, key);
                    }
                    iter.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocket) {
        try {
            for (SocketChannel socketChannel : socketChannels) {
                if (!socketChannel.isConnected())
                    socketChannel.finishConnect();
            }
            SocketChannel client = serverSocket.accept();
            socketChannels.add(client);
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answer(ByteBuffer buffer, SelectionKey key) {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            System.out.println("client = " + client);
            int r = -1;
            try {
                r = client.read(buffer);
            } catch (SocketException e) {
                System.out.println("socket exception");
                client.close();
                return;
            }
            System.out.println(r);
            if (r == -1) {
                System.out.println(String.format(
                        "Not accepting client %s messages anymore", client.getRemoteAddress()));
                client.close();
                return;
            }
            CommandArguments commandArguments = SerializationUtils.deserialize(buffer.array());
            buffer.clear();
            //processing
            MessageHolder.putMessage(commandArguments.commandName(), MessageType.OUTPUT_INFO);//
            MessageHolder.putMessage(commandArguments.commandName(), MessageType.USER_ERROR);//
            ServerAnswer serverAnswer = new ServerAnswer(null,null, true, AnswerType.EXECUTION_RESPONSE);//example
            MessageHolder.clearMessages(MessageType.OUTPUT_INFO);
            MessageHolder.clearMessages(MessageType.USER_ERROR);
            buffer = Serializator.serialize(serverAnswer);
            client.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
