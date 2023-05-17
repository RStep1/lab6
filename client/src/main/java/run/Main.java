package run;

import java.util.Scanner;

import user.ClientManager;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientManager clientManager = new ClientManager(scanner);
        String[] hostAndPort = args[0].split("\\s+");
        String host = hostAndPort[0];
        int port = Integer.parseInt(args[1]);
        boolean processingStatus = false;
        boolean isTryReconnecting = false;
        while (!processingStatus) { // processingStatus = true, if client input 'exit', else connection failed
            if (!clientManager.setConnection(host, port)) {
                if (!isTryReconnecting)
                    System.out.println("reconnection...");
                isTryReconnecting = true;
                continue;
            }
            isTryReconnecting = false;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processingStatus = clientManager.processRequestToServer();
        }
        scanner.close();
    }
}