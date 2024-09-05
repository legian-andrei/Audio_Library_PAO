package org.example.Exporter;

import org.example.SongsData.Playlist;

public interface PlaylistExporter {
    /**
     * Export the playlist to a file
     * @param playlist the playlist to export
     * @param username the username of the user who owns the playlist
     * @return true if the playlist was exported successfully, false otherwise
     */
    boolean exportPlaylist(Playlist playlist, String username);
}
