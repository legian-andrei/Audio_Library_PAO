package org.example.Users;

import java.sql.*;
public class UserService {
    private Connection conn;

    public UserService(Connection connection) {
        conn = connection;
    }

    /**
     * Check if the user being registered is the first user in DB
     * @return whether the user is the first one registered or not
     */
    public boolean isFirstUser() {
        String query = "SELECT COUNT(*) AS userCount FROM Users;";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return resultSet.getInt("userCount") == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Register a new user
     * @param username The username for the user to be created
     * @param password The password for the user to be created
     * @return Whether the user was created or not
     */
    public boolean register(String username, String password){
        String query;
        if (isFirstUser()){
            query = "INSERT INTO Users (username, password, user_rights) VALUES (?,?,admin);";
        } else {
            query = "INSERT INTO Users (username, password, user_rights) VALUES (?,?,authenticated);";
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Login into account
     * @param username The username used for login
     * @param password The password used for login
     * @return A user instance or null if the login failed
     */
    public User login(String username, String password) {
        String query = "SELECT id, username, password, user_rights FROM Users WHERE username = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Integer userId = resultSet.getInt("id");
                String dbPassword = resultSet.getString("password");
                String userRights = resultSet.getString("user_rights");

                if (dbPassword.equals(password)) {
                    switch (userRights.toLowerCase()) {
                        case "authenticated":
                            AuthenticatedUser authUser = AuthenticatedUser.builder()
                                    .id(userId)
                                    .username(username)
                                    .password(password)
                                    .userType("authenticated")
                                    .build();
                            return authUser;
                        case "admin":
                            AdminUser admin = AdminUser.builder()
                                    .id(userId)
                                    .username(username)
                                    .password(password)
                                    .userType("authenticated")
                                    .build();
                            return admin;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Authentication failed
    }

    public boolean promoteToAdmin(String username) {
        String query = "UPDATE Users SET user_rights = 'admin' WHERE username = ? AND user_rights = 'authenticated'";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
