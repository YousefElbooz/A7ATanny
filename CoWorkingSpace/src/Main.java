
import java.io.IOException;
import java.util.Scanner;

import services.*;
public class Main{

    public static void main(String[] args) throws Exception {
        // launch(args);
            Scanner scanner = new Scanner(System.in);
            SystemManager systemManager = new SystemManager();
        
            while (true) {
                    System.out.println("=== Welcome to the Coworking Space System ===");
                    System.out.println("1. Visitor Menu");
                    System.out.println("2. Admin Menu");
                    System.out.println("3. Exit");
                    System.out.print("Choose an option: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                            case 1 -> systemManager.visitorMenu(scanner);
                            case 2 -> systemManager.adminMenu(scanner);
                            case 3 -> {
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}