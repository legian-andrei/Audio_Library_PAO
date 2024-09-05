package org.example;

import org.example.Users.*;

import java.util.Scanner;

public final class Application {
    private static Database database = new Database();
    Scanner scanner = new Scanner(System.in);
    private final UserManager userManager;
    public Application() {
        System.out.println("Starting...");
        database.connect();
        userManager = new UserManager(scanner, database);
    }

    /**
     * Running the application.
     */
    public void run(){

        while(true){
            userManager.currentUser.showMenu();

            try{
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    exit();
                }
                if (userManager.isGuestUser()) {
                    userManager.handleGuestChoice(input);
                }
                else {
                    userManager.userHandler.handleChoice(input);
                }
            } catch (Exception e) { // if an unexpected error occurs
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Closing the application.
     */
    public static void exit(){
        System.out.println("Exiting...");
        database.disconnect();
        System.exit(0);
    }
}
