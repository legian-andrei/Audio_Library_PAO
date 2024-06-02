package org.example;

import org.example.SongsData.SongService;
import org.example.Users.*;
import org.example.Database;

import java.util.Scanner;

public class Main {
    private static Database database = new Database();
    private static UserService userService;
    private static SongService songService;
    private  static AuditService auditService;
    private static Menu menu;
    public static User currentUser = new AnonymousUser();
    public static void main(String[] args) {
        start();

        Scanner scanner = new Scanner(System.in);
        while(true){
            if (currentUser instanceof AnonymousUser){
                menu.showAnonymousMenu(scanner);
            } else if (currentUser instanceof AuthenticatedUser){
                menu.showAuthenticatedMenu((AuthenticatedUser) currentUser, scanner);
            } else if (currentUser instanceof AdminUser){
                menu.showAdminMenu((AdminUser) currentUser, scanner);
            }
        }
    }

    /**
     * Starting the application; establishing connections.
     */
    private static void start(){
        System.out.println("Starting...");
        database.connect();
        userService = new UserService(database.getConn());
        songService = new SongService(database.getConn());
        auditService = new AuditService(database.getConn());
        menu = Menu.builder()
                .userService(userService)
                .songService(songService)
                .auditService(auditService)
                .build();
    }

    /**
     * Closing the application.
     */
    public static final void exit(){
        System.out.println("Exiting...");
        database.disconnect();
        System.exit(0);
    }
}
