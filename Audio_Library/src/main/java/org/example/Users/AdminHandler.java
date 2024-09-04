package org.example.Users;

import org.example.Database;
import org.example.SongsData.SongService;

import java.util.Scanner;

public class AdminHandler extends AuthenticatedHandler {
    public AdminHandler(User user, Scanner sc, Database db) {
        super(user, sc, db);
    }

    @Override
    public void handleChoice(String choice) {
        switch (choice){
            case "1": // List songs
                listSongs();
                break;
            case "2": // Create song
                addSong();
                break;
            case "3": // List playlists
                listPlaylists();
                break;
            case "4": // List songs from playlist
                listSongsFromPlaylist();
                break;
            case "5": // Create playlist
                createPlaylist();
                break;
            case "6": // Add song to playlist
                addSongToPlaylist();
                break;
            case "7": // Export to CSV
                exportToCSV();
                break;
            case "8": // Export to JSON
                exportToJSON();
                break;
            case "9": // List users
                listUsers();
                break;
            case "10": // Promote user to admin
                promoteUser();
                break;
            case "11": // View audit
                listAudit();
                break;
            default:
                System.out.println("""
                        =======================================================
                        Invalid choice! Please select one of the options above.""");
                break;
        }
    }

    /**
     * Add a song to the database.
     */
    private void addSong(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());

        System.out.println("Enter song title:");
        String title = scanner.nextLine();

        System.out.println("Enter song artist:");
        String artist = scanner.nextLine();

        System.out.println("Enter release year of the song:");
        int releaseYear = scanner.nextInt();

        if (title.length() < 3 || artist.length() < 3 || releaseYear < 1000 || releaseYear > 2025) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        if (songService.addSong(title, artist, releaseYear)) {
            System.out.println("Song added successfully.");
            auditService.logCommand(userService.getUserId(currentUser), "ADD_SONG_" + title + "_BY_" + artist);
        } else {
            System.out.println("Failed to add song.");
        }
    }

    /**
     * List all users in the database.
     */
    private void listUsers(){
        UserService userService = new UserService(database.getConn());

        int pageSize = 5;
        int currentPage = 1;
        int pageNum = userService.countUsers() / pageSize + 1;
        boolean back = false;
        do{
            for (User user : userService.getUsers(currentPage, pageSize)){
                System.out.println(user);
            }
            System.out.println("Page " + currentPage + "/" + pageNum);
            System.out.println("""
                    1. Go to previours page
                    2. Go to next page
                    3. Go back""");
            int ch = scanner.nextInt();
            scanner.nextLine();
            switch (ch){
                case 1: //last page
                    if (currentPage == 1){
                        System.out.println("You have reached the first page.");
                    } else {
                        currentPage--;
                    }
                    break;
                case 2: // next page
                    if (currentPage == pageNum){
                        System.out.println("You have reached the last page.");
                    } else {
                        currentPage++;
                    }
                    break;
                case 3: //go back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    /**
     * Promote a user to admin.
     */
    private void promoteUser(){
        UserService userService = new UserService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());

        System.out.println("Enter username to promote:");
        String username = scanner.nextLine();

        if (userService.promoteToAdmin(username)){
            System.out.println("User " + username + " promoted successfully!\n");
            auditService.logCommand(userService.getUserId(currentUser), "PROMOTE_" + username);
        } else {
            System.out.println("Failed to promote the username.");
        }
    }

    /**
     * List the audit logs for a specified user.
     */
    private void listAudit() {
        UserService userService = new UserService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());

        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        int userId = userService.getUserId(username);
        if (userId == -1) {
            System.out.println("Failed to find the user.");
            return;
        }

        auditService.logCommand(userService.getUserId(currentUser), "AUDIT_" + userId);
        int pageSize = 5;
        int currentPage = 1;
        int pageNum = auditService.getAuditSize(userId) / pageSize + 1;
        boolean back = false;
        do{
            for (AuditLog log : auditService.getAuditLogs(userId, currentPage, pageSize)){
                System.out.println(log);
            }
            System.out.println("Page " + currentPage + "/" + pageNum);
            System.out.println("""
                    1. Go to previours page
                    2. Go to next page
                    3. Go back""");
            int ch = scanner.nextInt();
            scanner.nextLine();
            switch (ch){
                case 1: //last page
                    if (currentPage == 1){
                        System.out.println("You have reached the first page.");
                    } else {
                        currentPage--;
                    }
                    break;
                case 2: // next page
                    if (currentPage == pageNum){
                        System.out.println("You have reached the last page.");
                    } else {
                        currentPage++;
                    }
                    break;
                case 3: // go back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }
}
