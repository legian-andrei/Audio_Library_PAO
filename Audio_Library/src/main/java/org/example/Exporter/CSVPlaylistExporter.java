package org.example.Exporter;

import org.example.SongsData.Playlist;
import org.example.SongsData.Song;
import org.example.SongsData.SongService;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CSVPlaylistExporter implements PlaylistExporter {
    private final SongService songService;

    public CSVPlaylistExporter(SongService songService) {
        this.songService = songService;
    }
    @Override
    public boolean exportPlaylist(Playlist playlist, String username) {
        List<Song> songs = songService.getSongsInPlaylist(playlist.getId());
        if (songs.isEmpty()){
            return false;
        }
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "export_" + username + "_" + playlist.getName() + "_" + dateStr + ".csv";

        try(FileWriter writer = new FileWriter(fileName)){
            writer.append("Playlist Name," + playlist.getName() + ",UserID,"+ playlist.getUserId() +"\n");
            writer.append("Sond ID,Song Name,Artist,Release Date\n");
            for (Song song : songs) {
                writer.append(Integer.toString(song.getId())).append(',')
                        .append(song.getTitle()).append(',')
                        .append(song.getSinger()).append(',')
                        .append(Integer.toString(song.getRelease_year())).append('\n');
            }
            System.out.println("Playlist exported to " + fileName);
            return true;
        } catch(IOException e){
            System.out.println("I/O error");
        } catch (Exception e) {
            System.out.println("Error exporting playlist to CSV");
        }
        return false;
    }
}
