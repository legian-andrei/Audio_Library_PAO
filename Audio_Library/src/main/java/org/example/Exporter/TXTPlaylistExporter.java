package org.example.Exporter;

import org.example.SongsData.Playlist;
import org.example.SongsData.Song;
import org.example.SongsData.SongService;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TXTPlaylistExporter implements PlaylistExporter {
    private final SongService songService;

    public TXTPlaylistExporter(SongService songService) {
        this.songService = songService;
    }

    @Override
    public boolean exportPlaylist(Playlist playlist, String username) {
        List<Song> songs = songService.getSongsInPlaylist(playlist.getId());
        if (songs.isEmpty()){
            return false;
        }
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "export_" + username + "_" + playlist.getName() + "_" + dateStr + ".txt";

        try(FileWriter writer = new FileWriter(fileName)){
            writer.append("playlistName -> " + playlist.getName() + "\n")
                    .append("userID -> " + playlist.getUserId() + "\n")
                    .append("songNo -> " + songs.size() + "\n");
            for (Song song : songs) {
                writer.append("songID -> " + song.getId() + "\n")
                        .append("songTitle -> " + song.getTitle() + "\n")
                        .append("songArtist -> " + song.getSinger() + "\n")
                        .append("songReleaseDate -> " + song.getRelease_year() + "\n");
            }
            System.out.println("Playlist exported to " + fileName);
            return true;
        } catch(IOException e){
            System.out.println("I/O error");
        } catch (Exception e) {
            System.out.println("Error exporting playlist to TXT");
        }
        return false;
    }
}
