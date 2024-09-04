package org.example.Users;

import lombok.*;
import lombok.experimental.SuperBuilder;


public final class AnonymousUser extends User {
    public AnonymousUser() {
        this.id = 0;
        this.username = null;
        this.userType = UserRole.ANONYMOUS;
    }
    @Override
    public void showMenu() {
        System.out.println("\n=======================================================");
        System.out.println("Welcome to the Music App! You are currently logged in as Anonymous User.\nChoose one option from the following menu:");
        System.out.println("""
                1. Register
                2. Login
                Press ENTER to exit.""");
    }
}
