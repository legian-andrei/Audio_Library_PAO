package org.example.Users;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class AuditLog {
    private int id;
    private int userId;
    private String command;
    private Timestamp timestamp;

    @Override
    public String toString() {
        return "[ID: " + getId() + "] User " + getUserId() + ": " + getCommand() + "@" + getTimestamp();
    }
}
