package org.example.SongsData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Playlist {
    private int id;
    private int userId;
    private String name;

    @Override
    public String toString() {
        return "[ID: " + getId() + "] " + getName();
    }
}
