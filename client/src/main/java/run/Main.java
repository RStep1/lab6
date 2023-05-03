package run;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientManager clientManager = new ClientManager(scanner);
        //args = (host, port) - get and check
        boolean processingStatus = false;
        while (!processingStatus) { // processingStatus = true, if client input 'exit', else connection failed
            if (!clientManager.setConnection("localhost", 15454))
                continue;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processingStatus = clientManager.processRequestToServer();
        }
    }
}