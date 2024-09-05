package org.example.Users;

import org.example.Database;
import org.example.Exceptions.DuplicateUsernameException;
import org.example.Main;

import java.util.Scanner;

public class UserManager {
    public static User currentUser = new AnonymousUser();
    public static UserHandler userHandler;
    public static Scanner scanner;
    public static UserService userService;
    private static Database database;

    /**
     * Constructor for the UserManager class.
     * @param sc: the scanner object
     */
    public UserManager(Scanner sc, UserService us, Database db){
        scanner = sc;
        userService = us;
        database = db;
        userService = new UserService(database.getConn());
    }

    /**
     * Guest user choice handler.
     * Use a local handler to be able to register and login easier.
     * @param input: the user's input
     */
    public void handleGuestChoice(String input) {
        switch(input){
            case "1":
                register();
                break;
            case "2":
                login();
                break;
            default:
                System.out.println("""
                        =======================================================
                        Invalid choice! Please select one of the options above.""");
                break;
        }
    }

    /**
     * Verifies if the current user is a guest user.
     * @return true if the current user is a guest user, false otherwise
     */
    public boolean isGuestUser(){
        return currentUser instanceof AnonymousUser;
    }

    /**
     * Register a new user.
     */
    private void register(){
        System.out.println("Register a new user:");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (username.length() < 3){
            System.out.println("Username must be at least 3 characters long.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (password.length() < 3){
            System.out.println("Password must be at least 3 characters long.");
            return;
        }

        try {
            if (userService.register(username, password)) {
                System.out.println("User " + username + " registered successfully.\n");
            } else {
                System.out.println("Registration failed. Please try again!");
            }
        } catch (DuplicateUsernameException e){
            System.out.println(e.getMessage() + " Please try again.");
        }
    }

    /**
     * Login into an existing account.
     * This method modify the currentUser and userHandler fields.
     */
    private void login(){
        System.out.println("Login:");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userService.login(username, password);
        if (user == null){
            System.out.println("Login failed. Incorrect username or password.");
        } else {
            currentUser = user;
            if (user instanceof AdminUser){
                userHandler = new AdminHandler(currentUser, scanner, database);
            } else {
                userHandler = new AuthenticatedHandler(currentUser, scanner, database);
            }
        }
    }
}
