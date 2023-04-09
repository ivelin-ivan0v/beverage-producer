package org.beverageproducer;

import org.beverageproducer.exceptions.*;
import org.beverageproducer.service.Controller;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        Scanner scanner = new Scanner(System.in);
        int clientID;
        do {
            System.out.println("Please enter clientID (1-5): ");
            clientID = scanner.nextInt();
        } while (clientID < 1 || clientID > 5);
        System.out.println("Please enter quantity of product A that is ordered: ");
        int quantityA = scanner.nextInt();
        System.out.println("Please enter quantity of product B that is ordered: ");
        int quantityB = scanner.nextInt();
        System.out.println("Please enter quantity of product C that is ordered: ");
        int quantityC = scanner.nextInt();
        System.out.println("Please enter quantity of product D that is ordered: ");
        int quantityD = scanner.nextInt();

        try {
            controller.startApplication(clientID, quantityA, quantityB, quantityC, quantityD);
        } catch (InvalidMarkupException | InvalidPercentException | InvalidUnitCostException |
                 InvalidCountOfItemsException | NegativeCountOfItemsException e) {
            throw new RuntimeException(e);
        }
    }
}