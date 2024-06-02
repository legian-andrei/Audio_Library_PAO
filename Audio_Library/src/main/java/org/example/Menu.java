package org.example;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.SongsData.Playlist;
import org.example.SongsData.Song;
import org.example.SongsData.SongService;
import org.example.Users.*;

import java.sql.Connection;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

@Setter
@NoArgsConstructor
@SuperBuilder
public class Menu {
//    private Connection connection;
    private UserService userService;
    private SongService songService;
    private AuditService auditService;

    public final void showAnonymousMenu(Scanner scanner){
        System.out.println("""
                =======================================================
                You are currently logged in as Anonymous User.
                Please choose an action:
                1. Register
                2. Login
                3. Exit
                """);
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch(choice){
            case 1:
                register(scanner);
                break;
            case 2:
                login(scanner);
                break;
            case 3:
                scanner.close();
                Main.exit();
                break;
        }
    }

    public final void showAuthenticatedMenu(AuthenticatedUser user, Scanner scanner){
        System.out.println("\n=======================================================");
        System.out.println("Logged in as " + user.getUsername() + ".");
        System.out.println("""
                To make everything easier for you, choose one option from 
                the following menu:
                1. View all songs
                2. View all playlists
                3. View songs in playlist
                4. Create a new playlist
                5. Add song to playlist
                6. Export playlist to CSV
                7. Export playlist to JSON
                8. Log out""");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice){
            case 1: // List songs
                listSongs(scanner);
                break;
            case 2: // List playlists
                listPlaylists(user, scanner);
                break;
            case 3: // List songs from playlist
                listSongsFromPlaylist(user, scanner);
                break;
            case 4: // Create playlist
                createPlaylist(user, scanner);
                break;
            case 5: // Add song to playlist
                addSongToPlaylist(user, scanner);
                break;
            case 6: // Export to CSV
                exportToCSV(user, scanner);
                break;
            case 7: // Export to JSON
                exportToJSON(user, scanner);
                break;
            case 8: // Log out
                logout();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public final void showAdminMenu(AdminUser user, Scanner scanner){
        System.out.println("\n=======================================================");
        System.out.println("Logged in as " + user.getUsername() + "(ADMIN).");
        System.out.println("""
                To make everything easier for you, choose one option from 
                the following menu:
                1. View all songs
                2. Create a new song
                3. View all playlists
                4. View songs in playlist
                5. Create a new playlist
                6. Add song to playlist
                7. Export playlist to CSV
                8. Export playlist to JSON
                9. View all users
                10. Promote user to admin
                11. View audit of a user
                12. Log out""");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice){
            case 1: // List songs
                listSongs(scanner);
                break;
            case 2: // Create song
                addSong(user, scanner);
                break;
            case 3: // List playlists
                listPlaylists(user, scanner);
                break;
            case 4: // List songs from playlist
                listSongsFromPlaylist(user, scanner);
                break;
            case 5: // Create playlist
                createPlaylist(user, scanner);
                break;
            case 6: // Add song to playlist
                addSongToPlaylist(user, scanner);
                break;
            case 7: // Export to CSV
                exportToCSV(user, scanner);
                break;
            case 8: // Export to JSON
                exportToJSON(user, scanner);
                break;
            case 9: // List users
                listUsers(scanner);
                break;
            case 10: // Promote user to admin
                promoteUser(user, scanner);
                break;
            case 11: // View audit
                listAudit(user, scanner);
                break;
            case 12: // Log out
                logout();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void register(Scanner scanner){
        System.out.println("Register a new user:");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (userService.register(username, password)){
            System.out.println("User " + username + " registered successfully.\n");
        } else {
            System.out.println("Registration failed. Please try again!");
        }
    }

    private void login(Scanner scanner){
        System.out.println("Login:");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userService.login(username, password);
        if (user == null){
            System.out.println("Login failed. Incorrect username or password.");
        } else {
            Main.currentUser = user;
        }
    }

    private void logout(){
        Main.currentUser = new AnonymousUser();
    }

    private void listSongs(Scanner scanner){
        int pageSize = 5;
        int currentPage = 1;
        int pageNum = (songService.countSongs()) / pageSize + 1;
        boolean back = false;
        do{
            for (Song song : songService.getSongs(currentPage, pageSize)){
                System.out.println(song);
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
                case 3: //back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    private void listPlaylists(User user, Scanner scanner){
        int userId = userService.getUserId(user);
        List<Playlist> playlists = songService.getPlaylistsByUser(userId);

        int pageSize = 5;
        int currentPage = 1;
        int pageNum = playlists.size() / pageSize + 1;
        boolean back = false;
        do{
            for (Playlist playlist : songService.getPlaylistsByUser(userId, currentPage, pageSize)){
                System.out.println(playlist);
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
                case 3: //back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    private void createPlaylist(User user, Scanner scanner){
        System.out.println("Enter playlist name:");
        String playlistName = scanner.nextLine();
        if (songService.createPlaylist(userService.getUserId(user), playlistName)) {
            System.out.println("Playlist " + playlistName + " created successfully.");
            auditService.logCommand(userService.getUserId(user), "CREATE_PLAYLIST_" + playlistName);
        } else {
            System.out.println("Failed to create playlist.");
        }
    }

    private void addSongToPlaylist(User user, Scanner scanner){
        System.out.println("Enter playlist name:");
        String playlistNameForSong = scanner.nextLine();
        System.out.println("Enter song title:");
        String songTitle = scanner.nextLine();
        System.out.println("Enter song artist:");
        String songArtist = scanner.nextLine();

        int playlistId = songService.getPlaylistId(userService.getUserId(user), playlistNameForSong);
        int songId = songService.getSongId(songTitle, songArtist);
        if (playlistId != -1 && songId != -1) {
            if (songService.addSongToPlaylist(playlistId, songId)) {
                System.out.println("Song added to playlist successfully.");
                auditService.logCommand(userService.getUserId(user),
                        "ADD_SONG_" + songId + "_PLAYLIST" + playlistId);
            } else {
                System.out.println("Failed to add song to playlist.");
            }
        } else {
            System.out.println("Invalid playlist name or song title.");
        }
    }

    private void listSongsFromPlaylist(User user, Scanner scanner){
        System.out.println("Enter playlist name: ");
        String playlistName = scanner.nextLine();
        int playlistId = songService.getPlaylistId(userService.getUserId(user), playlistName);

        int pageSize = 5;
        int currentPage = 1;
        int pageNum = songService.countSongsInPlaylist(playlistId) / pageSize + 1;
        boolean back = false;
        do{
            for (Song song : songService.getSongsInPlaylist(playlistId, currentPage, pageSize)){
                System.out.println(song);
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
                case 3: //back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    private void exportToCSV(User user, Scanner scanner){
        System.out.println("Enter playlist name to export:");
        String playlistNameForCSV = scanner.nextLine();
        int playlistIdForCSV = songService.getPlaylistId(userService.getUserId(user), playlistNameForCSV);

        if (playlistIdForCSV != -1) {
            if (songService.exportPlaylistToCSV(playlistIdForCSV, playlistNameForCSV, user)) {
                System.out.println("Playlist exported successfully.");
                auditService.logCommand(userService.getUserId(user), "EXPORT_CSV_" + playlistNameForCSV);
            } else {
                System.out.println("Failed to export playlist.");
            }
        }
    }

    private void exportToJSON(User user, Scanner scanner){
        System.out.println("Enter playlist name to export:");
        String playlistNameForJSON = scanner.nextLine();
        int playlistIdForJSON = songService.getPlaylistId(userService.getUserId(user), playlistNameForJSON);

        if (playlistIdForJSON != -1) {
            if (songService.exportPlaylistToJSON(playlistIdForJSON, playlistNameForJSON, user)) {
                System.out.println("Playlist exported successfully.");
                auditService.logCommand(userService.getUserId(user), "EXPORT_JSON_" + playlistNameForJSON);
            } else {
                System.out.println("Failed to export playlist.");
            }
        }
    }

    private void listUsers(Scanner scanner){
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
                case 3: //back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    private void addSong(User user, Scanner scanner){
        System.out.println("Enter song title:");
        String title = scanner.nextLine();
        System.out.println("Enter song artist:");
        String artist = scanner.nextLine();
        System.out.println("Enter release year of the song:");
        int releaseYear = scanner.nextInt();

        if (songService.addSong(title, artist, releaseYear)) {
            System.out.println("Song added successfully.");
            auditService.logCommand(userService.getUserId(user), "ADD_SONG_" + title + "_BY_" + artist);
        } else {
            System.out.println("Failed to add song.");
        }
    }

    private void promoteUser(User user, Scanner scanner){
        System.out.println("Enter username to promote:");
        String username = scanner.nextLine();

        if (userService.promoteToAdmin(username)){
            System.out.println("User " + username + " promoted successfully!\n");
            auditService.logCommand(userService.getUserId(user), "PROMOTE_" + username);
        } else {
            System.out.println("Failed to promote the username.");
        }
    }

    private void listAudit(User user, Scanner scanner) {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        int userId = userService.getUserId(username);
        if (userId == -1) {
            System.out.println("Failed to find the user.");
            return;
        }

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
                case 3: //back
                    auditService.logCommand(userService.getUserId(user), "AUDIT_" + userId);
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }
}
