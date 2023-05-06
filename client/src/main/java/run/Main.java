package run;

import java.util.Scanner;

import user.ClientManager;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientManager clientManager = new ClientManager(scanner);
        //args = (host, port) - get and check
        boolean processingStatus = false;
        boolean isTryReconnecting = false;
        while (!processingStatus) { // processingStatus = true, if client input 'exit', else connection failed
            if (!clientManager.setConnection("localhost", 15454)) {
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