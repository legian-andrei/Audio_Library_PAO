package org.example.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Connection conn;

    public UserService(Connection connection) {
        conn = connection;
    }

    /**
     * Check if the user being registered is the first user in DB
     * @return whether the user is the first one registered or not
     */
    private boolean isFirstUser() {
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
    public final boolean register(String username, String password){
        if (!checkUsername(username)) {
            System.out.println("User " + username + "already exists!");
            return false;
        }

        String query;
        if (isFirstUser()){
            query = "INSERT INTO Users (username, password, user_rights) VALUES (?,?,'admin');";
        } else {
            query = "INSERT INTO Users (username, password, user_rights) VALUES (?,?,'authenticated');";
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Login into account
     * @param username The username used for login
     * @param password The password used for login
     * @return A user instance or null if the login failed
     */
    public final User login(String username, String password) {
        String query = "SELECT id, username, password, user_rights FROM Users WHERE username = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Integer userId = resultSet.getInt("id");
                String dbPassword = resultSet.getString("password");
                String userRights = resultSet.getString("user_rights");

                if (dbPassword.equals(password)) {
                    System.out.println("Welcome back, " + username + "!");
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

    /**
     * Promote an user to admin rights.
     * @param username The username to be promoted.
     * @return Whether the promotion was successful or not.
     */
    public final boolean promoteToAdmin(String username) {
        String query = "UPDATE Users SET user_rights = 'admin' WHERE username = ? AND user_rights = 'authenticated'";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            int result = preparedStatement.executeUpdate();
            if (result > 0){
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkUsername(String username){
        String query = "SELECT COUNT(*) AS userCount FROM Users WHERE username = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int result = resultSet.getInt("userCount");
                if (result > 0){
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public final int getUserId(User user){
        return user.getId();
    }

    public final int getUserId(String username){
        if (checkUsername(username)){
            return -1;
        } else {
            String query = "SELECT id FROM Users WHERE username = ?;";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return resultSet.getInt("id");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return -1;
    }

    public final int countUsers(){
        String query = "SELECT COUNT(*) AS countUsers FROM Users;";
        int res = -1;
        try (Statement statement = conn.createStatement()){
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()){
                res = resultSet.getInt("countUsers");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public final List<User> getUsers(int page, int pageSize){
        String query = """
                SELECT id, username, user_rights
                FROM Users
                LIMIT ? OFFSET ?;""";
        List<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, (page - 1) * pageSize);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                if (resultSet.getString("user_rights").equals("admin")){
                    AdminUser user = AdminUser.builder()
                            .id(resultSet.getInt("id"))
                            .username(resultSet.getString("username"))
                            .userType("admin")
                            .build();
                    users.add(user);
                } else {
                    AuthenticatedUser user = AuthenticatedUser.builder()
                            .id(resultSet.getInt("id"))
                            .username(resultSet.getString("username"))
                            .userType("authenticated")
                            .build();
                    users.add(user);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return users;
    }
}
