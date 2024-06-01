package org.example.Users;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class User {
    private int id;
    @Getter
    private String username;
    private String password;
    @Getter
    private String userType;
}
