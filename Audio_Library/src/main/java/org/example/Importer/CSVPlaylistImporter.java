package org.example.Importer;

import org.example.SongsData.SongService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVPlaylistImporter implements PlaylistImporter {
    private final SongService songService;
    public CSVPlaylistImporter(SongService songService) {
        this.songService = songService;
    }

    @Override
    public boolean importPlaylist(String filePath) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] data;
            String playlistName = "";
            int userId=-1;
            if ((line = br.readLine()) != null) {
                data = line.split(",");
                if (data.length != 4 && !data[0].equals("Playlist Name") && !data[2].equals("UserID") && data[1].length() >= 3) {
                    System.out.println("Invalid data format");
                    return false;
                } else {
                    playlistName = data[1];
                    userId = Integer.parseInt(data[3]);
                    if (songService.checkPlaylist(userId, playlistName)){
                        if (playlistName.length() > 3) {
                            songService.createPlaylist(userId, playlistName);
                        } else {
                            System.out.println("Invalid playlist name");
                            return false;
                        }
                    }
                }
            }
            br.readLine();
            while ((line = br.readLine()) != null) {
                data = line.split(",");
                if (data.length != 4) {
                    System.out.println("Invalid data format");
                    return false;
                }
                int songId;
                String songName = data[1];
                String artist = data[2];
                int releaseYear = Integer.parseInt(data[3]);

                if (songService.checkSong(songName, artist)) {
                    songService.addSong(songName, artist, releaseYear);
                }
                songId = songService.getSongId(songName, artist);
                songService.addSongToPlaylist(songService.getPlaylistId(userId, playlistName), songId);
            }
            System.out.println("Playlist imported successfully");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (NumberFormatException e) {
            System.out.println("Invalid data format");
        } catch (IOException e) {
            System.out.println("I/O error");
        } catch (Exception e) {
            System.out.println("Error importing playlist from CSV");
        }
        return false;
    }
}
//import_User01_playlist01user01_20240905.csv