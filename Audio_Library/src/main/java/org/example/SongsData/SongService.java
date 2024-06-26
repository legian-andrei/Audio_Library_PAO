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
     * @param title
     * @param singer
     * @param release_year
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
     * @param title
     * @param singer
     * @return
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
     * @param page
     * @param pageSize
     * @return
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
            return null;
        }
    }

    /**
     * Get a paginated view of songs from a certain playlist
     * @param playlistId
     * @param page
     * @param pageSize
     * @return
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
            return null;
        }
    }

    /**
     * Get songs from a certain playlist
     * @param playlistId
     * @return
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
            return null;
        }
    }

    /**
     * Get the number of songs in a playlist; used for pagination
     * @param playlistId
     * @return
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
     * @param userId
     * @param name
     * @return
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
     * @param userId
     * @param playlistName
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
     * @param playlistId
     * @param songId
     * @return
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

    /**
     * Export playlist to CSV file
     * @param playlistId
     * @param playlistName
     * @param user
     * @return
     */
    public final boolean exportPlaylistToCSV(int playlistId, String playlistName, User user){
        List<Song> songs = getSongsInPlaylist(playlistId);
        if (songs.isEmpty()){
            return false;
        }
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "export_" + user.getUsername() + "_" + playlistName + "_" + dateStr + ".csv";

        try(FileWriter writer = new FileWriter(fileName)){
            writer.append("Playlist Name," + playlistName + "\n");
            writer.append("Sond ID,Song Name,Artist,Release Date\n");
            for (Song song : songs) {
                writer.append(Integer.toString(song.getId())).append(',')
                        .append(song.getTitle()).append(',')
                        .append(song.getSinger()).append(',')
                        .append(Integer.toString(song.getRelease_year())).append('\n');
            }
            System.out.println("Playlist exported to " + fileName);
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Export playlist to JSON file
     * @param playlistId
     * @param playlistName
     * @param user
     * @return
     */
    public final boolean exportPlaylistToJSON(int playlistId, String playlistName, User user){
        List<Song> songs = getSongsInPlaylist(playlistId);
        if (songs.isEmpty()){
            return false;
        }
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "export_" + user.getUsername() + "_" + playlistName + "_" + dateStr + ".json";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("{\n");
            writer.append("\"playlistName\": \"" + playlistName + "\",\n");
            writer.append("\"songs\": [\n");
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                writer.append("  {\n");
                writer.append("    \"id\": \"" + Integer.toString(song.getId()) + "\",\n");
                writer.append("    \"title\": \"" + song.getTitle() + "\",\n");
                writer.append("    \"artist\": \"" + song.getSinger() + "\",\n");
                writer.append("    \"releaseDate\": \"" + Integer.toString(song.getRelease_year()) + "\"\n");
                writer.append("  }");
                if (i < songs.size() - 1) {
                    writer.append(",");
                }
                writer.append("\n");
            }
            writer.append("]\n");
            writer.append("}\n");
            System.out.println("Playlist exported to " + fileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
