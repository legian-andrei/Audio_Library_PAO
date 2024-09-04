package org.example.Users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class User {
    protected int id;
    protected String username;
    protected String userType;
    public abstract void showMenu();
}
