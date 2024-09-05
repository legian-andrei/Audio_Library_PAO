package org.example.SongsData;

import org.example.Users.User;

import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SongService {
    private final Connection conn;

    public SongService(Connection connection){
        conn = connection;
    }

    /**
     * Create a new song
     * @param title Song title
     * @param singer Song singer
     * @param release_year Song release year
     * @return Wheather the song was created or not
     */
    public final boolean addSong(String title, String singer, int release_year){
        if (!checkSong(title, singer)){
            System.out.println("Song already exists!");
            return false;
        }
        String query = "INSERT INTO Songs (title, singer, release_year) VALUES (?,?,?);";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, singer);
            preparedStatement.setString(3, Integer.toString(release_year));

            int result = preparedStatement.executeUpdate();
            if (result > 0){
                System.out.println("Song created successfully!\n");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a new playlist
     * @param userId User ID of the user creating the playlist
     * @param name Playlist name
     * @return Whether the playlist was created or not
     */
    public final boolean createPlaylist(int userId, String name){
        if (!checkPlaylist(userId, name)) {
            System.out.println("Playlist " + name + " already exists!");
            return false;
        }
        String query = "INSERT INTO Playlists (name, userId) VALUES (?,?);";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, Integer.toString(userId));

            int result = preparedStatement.executeUpdate();
            if (result > 0){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add a song to the playlist
     * @param playlistId Playlist ID to add the song to
     * @param songId Song ID to add to the playlist
     * @return Whether the song was added to the playlist or not
     */
    public final boolean addSongToPlaylist(int playlistId, int songId){
        if (!checkSongIntoPlaylist(playlistId, songId)){
            System.out.println("Song already exists in playlist.");
            return false;
        }

        String query = "INSERT INTO PlaylistsSongs (playlistId, songId) VALUES (?,?);";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, Integer.toString(playlistId));
            preparedStatement.setString(2, Integer.toString(songId));
            int result = preparedStatement.executeUpdate();
            if (result > 0){
                return true;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a song already exists
     * @param title Song title to check
     * @param singer Song singer to check
     * @return Whether the song exists or not
     */
    private boolean checkSong(String title, String singer){
        String query = "SELECT COUNT(*) AS songCount FROM Songs WHERE title = ? AND singer = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, singer);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int result = resultSet.getInt("songCount");
                if (result == 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get song ID from title and singer.
     * @param title Song title
     * @param singer Song singer
     * @return Song ID or -1 if the song does not exist.
     */
    public final int getSongId(String title, String singer) {
        if (checkSong(title, singer)) {
            System.out.println("Song " + title + " does not exists.");
            return -1;
        }
        String query = "SELECT id FROM Songs WHERE title = ? AND singer = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, singer);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt("id");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Get a paginated view of songs
     * @param page Page number
     * @param pageSize Number of songs per page
     * @return List of songs
     */
    public final List<Song> getSongs(int page, int pageSize){
        String query = "SELECT id, title, singer, release_year FROM Songs LIMIT ? OFFSET ?;";
        List<Song> songs = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, (page - 1) * pageSize);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Song current_song = Song.builder()
                        .id(resultSet.getInt("id"))
                        .title(resultSet.getString("title"))
                        .singer(resultSet.getString("singer"))
                        .release_year(resultSet.getInt("release_year"))
                        .build();
                songs.add(current_song);
            }
            return songs;
        } catch (Exception e){
            e.printStackTrace();
            return songs;
        }
    }

    /**
     * Get a paginated view of songs from a certain playlist
     * @param playlistId Playlist ID to get songs from
     * @param page Page number
     * @param pageSize Number of songs per page
     * @return List of songs
     */
    public final List<Song> getSongsInPlaylist(int playlistId, int page, int pageSize){
        String query = """
                        SELECT id, title, singer, release_year
                        FROM Songs, PlaylistsSongs
                        WHERE Songs.id = PlaylistsSongs.songId
                            AND ? = PlaylistsSongs.playlistId
                        LIMIT ? OFFSET ?;""";

        List<Song> songs = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, (page - 1) * pageSize);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Song current_song = Song.builder()
                        .id(resultSet.getInt("id"))
                        .title(resultSet.getString("title"))
                        .singer(resultSet.getString("singer"))
                        .release_year(resultSet.getInt("release_year"))
                        .build();
                songs.add(current_song);
            }
            return songs;
        } catch (Exception e){
            e.printStackTrace();
            return songs;
        }
    }

    /**
     * Get songs from a certain playlist
     * @param playlistId Playlist ID to get songs from
     * @return List of songs
     */
    public final List<Song> getSongsInPlaylist(int playlistId){
        String query = """
                        SELECT id, title, singer, release_year
                        FROM Songs, PlaylistsSongs
                        WHERE Songs.id = PlaylistsSongs.songId
                            AND ? = PlaylistsSongs.playlistId;""";

        List<Song> songs = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, playlistId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Song current_song = Song.builder()
                        .id(resultSet.getInt("id"))
                        .title(resultSet.getString("title"))
                        .singer(resultSet.getString("singer"))
                        .release_year(resultSet.getInt("release_year"))
                        .build();
                songs.add(current_song);
            }
            return songs;
        } catch (Exception e){
            e.printStackTrace();
            return songs;
        }
    }

    /**
     * Get the number of songs in a playlist; used for pagination
     * @param playlistId Playlist ID to get the number of songs from
     * @return Number of songs in the playlist
     */
    public final int countSongsInPlaylist(int playlistId){
        String query = "SELECT COUNT(*) AS songCount FROM PlaylistsSongs WHERE playlistId = ?;";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, Integer.toString(playlistId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int result = resultSet.getInt("songCount");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the number of songs in the database; used for pagination
     * @return Number of songs in the database
     */
    public final int countSongs(){
        String query = "SELECT COUNT(*) AS songCount FROM Songs;";

        try (Statement statement = conn.createStatement()){
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()){
                int result = resultSet.getInt("songCount");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Check if a playlist already exists for a specified user
     * @param userId User ID to check the playlist for
     * @param name Playlist name to check
     * @return Whether the playlist exists or not
     */
    private boolean checkPlaylist(int userId, String name){
        String query = "SELECT COUNT(*) AS playlistCount FROM Playlists WHERE userId = ? AND name = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, Integer.toString(userId));
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int result = resultSet.getInt("playlistCount");
                if (result == 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the playlists for a specified user;
     * @param userId User ID to get the playlists for
     * @param page Page number
     * @param pageSize Number of playlists per page
     * @return List of playlists
     */
    public final List<Playlist> getPlaylistsByUser(int userId, int page, int pageSize){
        String query = """
                SELECT Playlists.id AS playlistId, Playlists.name AS playlistName
                FROM Playlists
                WHERE Playlists.userId = ?
                LIMIT ? OFFSET ?;""";
        List<Playlist> playlists = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, (page - 1) * pageSize);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Playlist playlist = Playlist.builder()
                        .userId(userId)
                        .name(resultSet.getString("playlistName"))
                        .id(resultSet.getInt("playlistId"))
                        .build();
                playlists.add(playlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlists;
    }

    /**
     * Get the playlists for a specified user;
     * @param userId User ID to get the playlists for
     * @return List of playlists
     */
    public final List<Playlist> getPlaylistsByUser(int userId){
        String query = """
                SELECT Playlists.id AS playlistId, Playlists.name AS playlistName
                FROM Playlists
                WHERE Playlists.userId = ?;""";
        List<Playlist> playlists = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Playlist playlist = Playlist.builder()
                        .userId(userId)
                        .name(resultSet.getString("playlistName"))
                        .id(resultSet.getInt("playlistId"))
                        .build();
                playlists.add(playlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlists;
    }


    /**
     * Get playlist ID from a certain user having a certain name
     * @param userId User ID to get the playlist ID for
     * @param playlistName Playlist name to get the ID for
     * @return Playlist ID or -1 if the playlist does not exist for the specified user.
     */
    public final int getPlaylistId (int userId, String playlistName) {
        if (checkPlaylist(userId, playlistName)) {
            System.out.println("Playlist " + playlistName + " does not exists.");
            return -1;
        }
        String query = "SELECT id FROM Playlists WHERE userId = ? AND name = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, Integer.toString(userId));
            preparedStatement.setString(2, playlistName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt("id");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Check if a song already exists within a playlist.
     * @param playlistId Playlist ID to check the song for
     * @param songId Song ID to check
     * @return Whether the song exists in the playlist or not
     */
    private boolean checkSongIntoPlaylist(int playlistId, int songId) {
        String query = "SELECT COUNT(*) AS songIntoPlaylistCount FROM PlaylistsSongs WHERE playlistId = ? AND songId = ?;";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, Integer.toString(playlistId));
            preparedStatement.setString(2, Integer.toString(songId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int result = resultSet.getInt("songIntoPlaylistCount");
                if (result == 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
