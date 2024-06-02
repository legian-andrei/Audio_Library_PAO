package org.example.SongsData;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Song {
    private int id;
    private String title;
    private String singer;
    private int release_year;

    @Override
    public String toString() {
        return "[ID: " + getId() + "] " + getSinger() + " - " + getTitle() + "(" + getRelease_year() + ")";
    }
}
