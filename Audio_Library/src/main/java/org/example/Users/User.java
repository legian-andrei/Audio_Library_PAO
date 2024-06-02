package org.example.Users;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class User {
    @Getter
    private int id;
    @Getter
    private String username;
    private String password;
    @Getter
    private String userType;

    @Override
    public String toString() {
        return "[ID: " + getId() + "] " + getUsername() + " (" + getUserType().toUpperCase() + ")";
    }
}
