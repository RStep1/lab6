package host;

import commands.SaveCommand;
import processing.BufferedDataBase;
import processing.NBChannelController;
import processing.RequestHandler;
import utility.CommandArguments;
import utility.ServerAnswer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Server {
    private static final String HOST = "localhost";
    private static final int PORT = 18022;
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private final RequestHandler requestHandler;
    private static final CommandArguments SAVE_COMMAND = 
                new CommandArguments(SaveCommand.getName(), null, null,
                        null, null);
    private CommandArguments commandArguments;

    public Server(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }
    
    private void setup() {
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocket.bind(new InetSocketAddress(HOST, PORT));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
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
                        // SocketChannel client = (SocketChannel) key.channel();
                        // System.out.println("client = " + client);
                        // System.out.println("Client is connected by: "+client.isConnected());
                        answer(selector, key);
                    }
                    if (key.isWritable()) {
                        write(key);
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

    private void answer(Selector selector, SelectionKey key) {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            // CommandArguments commandArguments;
            try {
                commandArguments = (CommandArguments) NBChannelController.read(client);
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_WRITE);
            } catch (SocketException e) {
                e.printStackTrace();
                System.out.println("Socket exception");
                // e.printStackTrace();
                requestHandler.processRequest(SAVE_COMMAND);
                System.out.println("close1");
                client.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(String.format(
                        "Not accepting client %s messages anymore", client.getRemoteAddress()));
                requestHandler.processRequest(SAVE_COMMAND); // save collection after client exits
                // System.out.println("close2");
                client.close();
                return;
            }


            // ServerAnswer serverAnswer = requestHandler.processRequest(commandArguments);
            // NBChannelController.write(client, serverAnswer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(SelectionKey key) {
        ServerAnswer serverAnswer = requestHandler.processRequest(commandArguments);
        SocketChannel client = (SocketChannel) key.channel();
        try {
            NBChannelController.write(client, serverAnswer);
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CommandArguments getSaveCommand() {
        return SAVE_COMMAND;
    }
}