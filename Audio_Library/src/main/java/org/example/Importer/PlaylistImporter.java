package org.example.Importer;

public interface PlaylistImporter {
    /**
     * Import a playlist from a file
     * @param filename the name of the file to import
     * @return true if the playlist was imported successfully, false otherwise
     */
    boolean importPlaylist(String filename);
}
