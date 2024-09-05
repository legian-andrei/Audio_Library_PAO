package org.example.Users;

import org.example.Database;
import org.example.Exporter.*;
import org.example.Importer.*;
import org.example.SongsData.*;

import java.util.List;
import java.util.Scanner;


public class AuthenticatedHandler implements UserHandler {
    protected static Scanner scanner;
    protected static User currentUser;
    protected static Database database;

    /**
     * Constructor for the AuthenticatedHandler class.
     * @param user: the current user
     * @param sc: the scanner object
     * @param db: the database object
     */
    public AuthenticatedHandler(User user, Scanner sc, Database db){
        currentUser = user;
        scanner = sc;
        database = db;
    }

    @Override
    public void handleChoice(String choice) {
        switch (choice){
            case "1": // List songs
                listSongs();
                break;
            case "2": // List playlists
                listPlaylists();
                break;
            case "3": // List songs from playlist
                listSongsFromPlaylist();
                break;
            case "4": // Create playlist
                createPlaylist();
                break;
            case "5": // Add song to playlist
                addSongToPlaylist();
                break;
            case "6": // Export to CSV
                exportToCSV();
                break;
            case "7": // Export to JSON
                exportToJSON();
                break;
            case "8": // Export to TXT
                exportToTXT();
                break;
            case "9": // Import from CSV
                importFromCSV();
                break;
            case "10": // Import from JSON
                importFromJSON();
                break;
            case "11": // Import from TXT
                importFromTXT();
                break;
            default:
                System.out.println("""
                        =======================================================
                        Invalid choice! Please select one of the options above.""");
                break;
        }
    }

    /**
     * Imports a playlist from a TXT file.
     */
    protected void importFromTXT() {
        System.out.println("Enter file path: ");
        String filePath = scanner.nextLine();

        PlaylistImporter playlistImporter = new TXTPlaylistImporter(new SongService(database.getConn()));
        if (playlistImporter.importPlaylist(filePath)) {
            System.out.println("Playlist imported successfully.");
        } else {
            System.out.println("Failed to import playlist.");
        }
    }

    /**
     * Imports a playlist from a JSON file.
     */
    protected void importFromJSON() {
        System.out.println("Enter file path: ");
        String filePath = scanner.nextLine();

        PlaylistImporter playlistImporter = new JSONPlaylistImporter(new SongService(database.getConn()));
        if (playlistImporter.importPlaylist(filePath)) {
            System.out.println("Playlist imported successfully.");
        } else {
            System.out.println("Failed to import playlist.");
        }
    }

    /**
     * Imports a playlist from a CSV file.
     */
    protected void importFromCSV() {
        System.out.println("Enter file path: ");
        String filePath = scanner.nextLine();

        PlaylistImporter playlistImporter = new CSVPlaylistImporter(new SongService(database.getConn()));
        if (playlistImporter.importPlaylist(filePath)) {
            System.out.println("Playlist imported successfully.");
        } else {
            System.out.println("Failed to import playlist.");
        }
    }

    /**
     * Lists the available songs.
     */
    protected void listSongs(){
        SongService songService = new SongService(database.getConn());

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
            String ch = scanner.nextLine();
            scanner.nextLine();
            switch (ch){
                case "1": //last page
                    if (currentPage == 1){
                        System.out.println("You have reached the first page.");
                    } else {
                        currentPage--;
                    }
                    break;
                case "2": // next page
                    if (currentPage == pageNum){
                        System.out.println("You have reached the last page.");
                    } else {
                        currentPage++;
                    }
                    break;
                case "3": //go back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    /**
     * Lists the playlists created by the current user.
     */
    protected void listPlaylists(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());

        int userId = userService.getUserId(currentUser);
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
            String ch = scanner.nextLine();
            switch (ch){
                case "1": //last page
                    if (currentPage == 1){
                        System.out.println("You have reached the first page.");
                    } else {
                        currentPage--;
                    }
                    break;
                case "2": // next page
                    if (currentPage == pageNum){
                        System.out.println("You have reached the last page.");
                    } else {
                        currentPage++;
                    }
                    break;
                case "3": //go back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    /**
     * Lists songs from a specified playlist.
     */
    protected void listSongsFromPlaylist(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());

        System.out.println("Enter playlist name: ");
        String playlistName = scanner.nextLine();
        int playlistId = songService.getPlaylistId(userService.getUserId(currentUser), playlistName);

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
            String ch = scanner.nextLine();
            scanner.nextLine();
            switch (ch){
                case "1": //last page
                    if (currentPage == 1){
                        System.out.println("You have reached the first page.");
                    } else {
                        currentPage--;
                    }
                    break;
                case "2": // next page
                    if (currentPage == pageNum){
                        System.out.println("You have reached the last page.");
                    } else {
                        currentPage++;
                    }
                    break;
                case "3": //go back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!back);
    }

    /**
     * Creates a new playlist.
     */
    protected void createPlaylist(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());

        System.out.println("Enter playlist name:");
        String playlistName = scanner.nextLine();

        if (playlistName.length() < 3){
            System.out.println("Failed to create playlist.");
            return;
        }

        if (songService.createPlaylist(userService.getUserId(currentUser), playlistName)) {
            System.out.println("Playlist " + playlistName + " created successfully.");
            auditService.logCommand(userService.getUserId(currentUser), "CREATE_PLAYLIST_" + playlistName);
        } else {
            System.out.println("Failed to create playlist.");
        }
    }

    /**
     * Adds a song to a playlist.
     */
    protected void addSongToPlaylist(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());

        System.out.println("Enter playlist name:");
        String playlistNameForSong = scanner.nextLine();
        System.out.println("Enter song title:");
        String songTitle = scanner.nextLine();
        System.out.println("Enter song artist:");
        String songArtist = scanner.nextLine();

        int playlistId = songService.getPlaylistId(userService.getUserId(currentUser), playlistNameForSong);
        int songId = songService.getSongId(songTitle, songArtist);
        if (playlistId != -1 && songId != -1) {
            if (songService.addSongToPlaylist(playlistId, songId)) {
                System.out.println("Song added to playlist successfully.");
                auditService.logCommand(userService.getUserId(currentUser),
                        "ADD_SONG_" + songId + "_PLAYLIST" + playlistId);
            } else {
                System.out.println("Failed to add song to playlist.");
            }
        } else {
            System.out.println("Invalid playlist name or song title.");
        }
    }

    /**
     * Exports the playlist to CSV.
     */
    protected void exportToCSV(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());
        PlaylistExporter playlistExporter = new CSVPlaylistExporter(songService);

        System.out.println("Enter playlist name to export:");
        String playlistName = scanner.nextLine();
        int playlistId = songService.getPlaylistId(userService.getUserId(currentUser), playlistName);

        if (playlistId != -1) {
            if (playlistExporter.exportPlaylist(
                    Playlist.builder()
                            .id(playlistId)
                            .name(playlistName)
                            .userId(currentUser.getId())
                            .build(),
                    currentUser.getUsername())) {
                auditService.logCommand(userService.getUserId(currentUser), "EXPORT_CSV_" + playlistName);
            } else {
                System.out.println("Failed to export playlist.");
            }
        }
    }

    /**
     * Exports the playlist to JSON.
     */
    protected void exportToJSON(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());
        PlaylistExporter playlistExporter = new JSONPlaylistExporter(songService);

        System.out.println("Enter playlist name to export:");
        String playlistName = scanner.nextLine();
        int playlistId = songService.getPlaylistId(userService.getUserId(currentUser), playlistName);

        if (playlistId != -1) {
            if (playlistExporter.exportPlaylist(
                    Playlist.builder()
                            .id(playlistId)
                            .name(playlistName)
                            .userId(currentUser.getId())
                            .build(),
                    currentUser.getUsername())) {
                auditService.logCommand(userService.getUserId(currentUser), "EXPORT_JSON_" + playlistName);
            } else {
                System.out.println("Failed to export playlist.");
            }
        }
    }

    protected void exportToTXT(){
        UserService userService = new UserService(database.getConn());
        SongService songService = new SongService(database.getConn());
        AuditService auditService = new AuditService(database.getConn());
        PlaylistExporter playlistExporter = new TXTPlaylistExporter(songService);

        System.out.println("Enter playlist name to export:");
        String playlistName = scanner.nextLine();
        int playlistId = songService.getPlaylistId(userService.getUserId(currentUser), playlistName);

        if (playlistId != -1) {
            if (playlistExporter.exportPlaylist(
                    Playlist.builder()
                            .id(playlistId)
                            .name(playlistName)
                            .userId(currentUser.getId())
                            .build(),
                    currentUser.getUsername())) {
                auditService.logCommand(userService.getUserId(currentUser), "EXPORT_TXT_" + playlistName);
            } else {
                System.out.println("Failed to export playlist.");
            }
        }
    }
}
