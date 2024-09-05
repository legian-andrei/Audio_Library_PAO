package org.example.Users;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public final class AdminUser extends AuthenticatedUser{
    @Override
    public void showMenu() {
        System.out.println("\n=======================================================");
        System.out.println("Logged in as " + this.getUsername() + "(ADMIN).");
        System.out.println("""
                To make everything easier for you, choose one option from
                the following menu:
                1. View all songs
                2. Create a new song
                3. View all playlists
                4. View songs in playlist
                5. Create a new playlist
                6. Add song to playlist
                7. Export playlist to CSV
                8. Export playlist to JSON
                9. View all users
                10. Promote user to admin
                11. View audit of a user
                Press ENTER to exit.""");
    }
    @Override
    public String toString() {
        return "[ID: " + getId() + "] " + getUsername() + " (" + getUserType().name() + ")";
    }
}
