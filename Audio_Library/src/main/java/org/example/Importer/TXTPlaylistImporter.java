package org.example.Importer;

import org.example.SongsData.SongService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TXTPlaylistImporter implements PlaylistImporter {
    private final SongService songService;

    public TXTPlaylistImporter(SongService songService) {
        this.songService = songService;
    }

    @Override
    public boolean importPlaylist(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String playlistName = "";
            int userId = -1;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("playlistName -> ")) {
                    playlistName = line.substring(16);
                } else if (line.startsWith("userID -> ")) {
                    userId = Integer.parseInt(line.substring(10));
                    if (songService.checkPlaylist(userId, playlistName)){
                        if (playlistName.length() > 3) {
                            songService.createPlaylist(userId, playlistName);
                        } else {
                            System.out.println("Invalid playlist name");
                            return false;
                        }
                    }
                } else if (line.startsWith("songID -> ")) {
                    int songId = -1;
                    String songTitle = br.readLine().substring(13);
                    String songArtist = br.readLine().substring(14);
                    int releaseYear = Integer.parseInt(br.readLine().substring(19));

                    if (songService.checkSong(songTitle, songArtist)) {
                        songService.addSong(songTitle, songArtist, releaseYear);
                        songId = songService.getSongId(songTitle, songArtist);
                    }
                    if (songId != -1) {
                        songService.addSongToPlaylist(songService.getPlaylistId(userId, playlistName), songId);
                    }
                }
            }
            System.out.println("Playlist imported successfully");
            return true;
        } catch (IOException e) {
            System.out.println("I/O error");
        } catch (Exception e) {
            System.out.println("Error importing playlist from TXT");
        }
        return false;
    }
}
//import_User01_playlist01user01_20240905.txt