package org.example.Users;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class AuthenticatedUser extends User{
        @Override
    public void showMenu() {
        System.out.println("\n=======================================================");
        System.out.println("Logged in as " + this.getUsername() + ".");
        System.out.println("""
                To make everything easier for you, choose one option from 
                the following menu:
                1. View all songs
                2. View all playlists
                3. View songs in playlist
                4. Create a new playlist
                5. Add song to playlist
                6. Export playlist to CSV
                7. Export playlist to JSON
                Press ENTER to exit.""");    }
    @Override
    public String toString() {
        return "[ID: " + getId() + "] " + getUsername() + " (" + getUserType().toUpperCase() + ")";
    }
}
