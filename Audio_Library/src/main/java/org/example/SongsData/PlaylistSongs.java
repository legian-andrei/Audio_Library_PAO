package org.example.SongsData;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class PlaylistSongs {
    private final int idPlaylist;
    private final int idSong;
}
