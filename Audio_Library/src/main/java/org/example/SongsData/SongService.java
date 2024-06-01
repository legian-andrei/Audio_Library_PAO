package org.example.SongsData;

import java.sql.*;

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
    public boolean addSong(String title, String singer, int release_year){
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


    public boolean createPlaylist(int userId, String name){
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
                System.out.println("Playlist " + name + " created successfully!\n");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSongToPlaylist(int playlistId, int songId){
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
                System.out.println("Song added successfully into the playlist.");
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
    private int getSongId(String title, String singer) {
        if (!checkSong(title, singer)) {
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

    /**
     * Get playlist ID from a certain user having a certain name
     * @param userId
     * @param playlistName
     * @return Playlist ID or -1 if the playlist does not exist for the specified user.
     */
    private int getPlaylistId (int userId, String playlistName) {
        if (!checkPlaylist(userId, playlistName)) {
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
}
