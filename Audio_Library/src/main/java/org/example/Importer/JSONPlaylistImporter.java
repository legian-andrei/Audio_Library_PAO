package org.example.Importer;

import org.example.SongsData.SongService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JSONPlaylistImporter implements PlaylistImporter {
    private final SongService songService;
    public JSONPlaylistImporter(SongService songService) {
        this.songService = songService;
    }

    @Override
    public boolean importPlaylist(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            String playlistName = jsonObject.getString("playlistName");
            int userId = jsonObject.getInt("userID");
            if (songService.checkPlaylist(userId, playlistName)){
                if (playlistName.length() > 3) {
                    songService.createPlaylist(userId, playlistName);
                } else {
                    System.out.println("Invalid playlist name");
                    return false;
                }
            }


            JSONArray songsArray = jsonObject.getJSONArray("songs");
            for (int i = 0; i < songsArray.length(); i++) {
                JSONObject songObject = songsArray.getJSONObject(i);
                String songName = songObject.getString("title");
                String artist = songObject.getString("artist");
                int releaseYear = songObject.getInt("releaseDate");
                int songId;

                if (songService.checkSong(songName, artist)) {
                    songService.addSong(songName, artist, releaseYear);
                }
                songId = songService.getSongId(songName, artist);
                songService.addSongToPlaylist(songService.getPlaylistId(userId, playlistName), songId);

            }
            System.out.println("Playlist imported successfully");
            return true;
        } catch (IOException e) {
            System.out.println("I/O error");
        } catch (Exception e) {
            System.out.println("Error importing playlist from JSON");
        }
        return false;
    }
}
//import_User01_playlist01user01_20240905.json