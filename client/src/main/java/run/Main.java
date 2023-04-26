package run;

import mods.MessageType;
import utility.FileHandler;
import utility.MessageHolder;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientManager clientManager = new ClientManager(scanner);
        //args = (host, port) - get and check
        boolean processingStatus = false;
        while (!processingStatus) { // processingStatus = true, if client input 'exit', else connection failed
            clientManager.setConnection("localhost", 5621);
            processingStatus = clientManager.interactiveMode();
        }
    }

}