package org.example.Exporter;

import org.example.SongsData.Playlist;
import org.example.SongsData.Song;
import org.example.SongsData.SongService;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JSONPlaylistExporter implements PlaylistExporter {
    private final SongService songService;

    public JSONPlaylistExporter(SongService songService) {
        this.songService = songService;
    }
    @Override
    public boolean exportPlaylist(Playlist playlist, String username) {
        List<Song> songs = songService.getSongsInPlaylist(playlist.getId());
        if (songs.isEmpty()){
            return false;
        }
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "export_" + username + "_" + playlist.getName() + "_" + dateStr + ".json";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("{\n");
            writer.append("\"playlistName\": \"" + playlist.getName() + "\",\n");
            writer.append("\"userID\": \"" + playlist.getUserId() + "\",\n");
            writer.append("\"songs\": [\n");
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                writer.append("  {\n");
                writer.append("    \"id\": \"" + song.getId() + "\",\n");
                writer.append("    \"title\": \"" + song.getTitle() + "\",\n");
                writer.append("    \"artist\": \"" + song.getSinger() + "\",\n");
                writer.append("    \"releaseDate\": \"" + song.getRelease_year() + "\"\n");
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
        } catch(IOException e){
            System.out.println("I/O error");
        } catch (Exception e) {
            System.out.println("Error exporting playlist to CSV");
        }
        return false;
    }
}
