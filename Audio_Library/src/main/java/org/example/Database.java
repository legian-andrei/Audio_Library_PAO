package org.example;

import lombok.Getter;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public class Database {
    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/AudioLibrary?createDatabaseIfNotExist=true";
    static final String USER = "root";
    static final String PASSWORD = "rootpa55";

    @Getter
    private Connection conn;

    /**
     * Establish the connection with the database
     */
    public final void connect() {
        System.out.println("=======================================================\n");
        System.out.println("Establishing connection with the database. Please wait...");
        try {
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            Statement statement = conn.createStatement();

            String createTableUsers =
                    """
                    CREATE TABLE IF NOT EXISTS Users (
                                                id int auto_increment PRIMARY KEY,
                                                username varchar(255) NOT NULL UNIQUE,
                                                password varchar(255) NOT NULL,
                                                user_rights varchar(255) NOT NULL
                                            );
                    """;

            String createTableSongs =
                    """
                    CREATE TABLE IF NOT EXISTS Songs (
                                                id int auto_increment PRIMARY KEY,
                                                title varchar(255) NOT NULL,
                                                singer varchar(255) NOT NULL,
                                                release_year int NOT NULL,
                                                CONSTRAINT UniqueTitleSinger UNIQUE KEY (title, singer)
                                            );
                    """;

            String createTablePlaylists =
                    """
                    CREATE TABLE IF NOT EXISTS Playlists (
                                                id int auto_increment PRIMARY KEY,
                                                name varchar(255) NOT NULL,
                                                userId int NOT NULL,
                                                CONSTRAINT ForeignUserPlaylist FOREIGN KEY (userId) REFERENCES Users(id)
                                                    ON DELETE CASCADE,
                                                CONSTRAINT UniqueUserPlaylist UNIQUE KEY (userId, name)
                                            );
                    """;

            String createTablePlaylistSongs =
                    """
                    CREATE TABLE IF NOT EXISTS PlaylistsSongs (
                                                playlistId int,
                                                songId int,
                                                CONSTRAINT PrimaryPlaylistsSongs PRIMARY KEY (playlistId, songId),
                                                CONSTRAINT ForeignPlaylistsSongs_Songs FOREIGN KEY (playlistId) REFERENCES Playlists(id)
                                                    ON DELETE CASCADE,
                                                CONSTRAINT ForeignPlaylistsSongs_Playlists FOREIGN KEY (songId) REFERENCES Songs(id)
                                                    ON DELETE CASCADE
                                            );
                    """;

            String createTableUserAuditor =
                    """
                    CREATE TABLE IF NOT EXISTS UserAuditor (
                                                id int auto_increment PRIMARY KEY,
                                                command varchar(255) NOT NULL,
                                                userId int NOT NULL,
                                                timestamps TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                CONSTRAINT ForeignUserAudit FOREIGN KEY (userId) REFERENCES Users(id)
                                                    ON DELETE CASCADE
                                            );
                    """;

            statement.execute(createTableUsers);
            statement.execute(createTableSongs);
            statement.execute(createTablePlaylists);
            statement.execute(createTablePlaylistSongs);
            statement.execute(createTableUserAuditor);

            TimeUnit.MILLISECONDS.sleep(100);

            System.out.println("Connected successfully\n");
            System.out.println("=======================================================\n\n");
        } catch (Exception e) {
            System.out.println("Failed to connect with the database.\n" + e);
        }
    }

    /**
     * Close the connection with the database
     */
    public final void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                System.out.println("=======================================================\n");
                System.out.println("Disconnecting...");
                conn.close();
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println("Goodbye!");
            }
        } catch (Exception e) {
            System.out.println("Failed to connect with the database.\n" + e);
        }
    }
}
