package run;

import mods.AnswerType;
import mods.MessageType;
import org.apache.commons.lang3.SerializationUtils;
import processing.CommandInvoker;
import processing.Serializator;
import utility.CommandArguments;
import utility.MessageHolder;
import utility.ServerAnswer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static final String HOST = "localhost";
    private static final int PORT = 15454;
    private final ArrayList<SocketChannel> socketChannels = new ArrayList<>();
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ByteBuffer buffer;
    private final RequestHandler requestHandler;

    public Server(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    private void setup() {
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
//            serverSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocket.bind(new InetSocketAddress(HOST, PORT));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            buffer = ByteBuffer.allocate(4048);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        setup();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                System.out.println("Shutting down ...");
                try {
                    selector.close();
                } catch (IOException e) {
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
//                e.printStackTrace();
            }
        }));
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
            SocketChannel client = serverSocket.accept();
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
            System.out.println("buffer: " + buffer);
            CommandArguments commandArguments = SerializationUtils.deserialize(buffer.array());
            buffer.clear();
            ServerAnswer serverAnswer = requestHandler.processRequest(commandArguments);
            buffer = Serializator.serialize(serverAnswer);
            System.out.println("SENDING BUFFER: " + buffer);
            client.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
