package org.example.Users;

import org.example.Exceptions.AlreadyAdminException;
import org.example.Exceptions.DuplicateUsernameException;
import org.example.Exceptions.UsernameNotFoundException;

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
    public final boolean register(String username, String password) throws DuplicateUsernameException {
        if (!checkUsername(username)) {
            throw new DuplicateUsernameException("Username " + username + " already exists.");
        }

        String query = "INSERT INTO Users (username, password, user_rights) VALUES (?,?,?);";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            UserRole userRole = isFirstUser() ? UserRole.ADMIN : UserRole.AUTHENTICATED;
            preparedStatement.setString(3, userRole.name());

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
                UserRole userRole = UserRole.valueOf(resultSet.getString("user_rights"));

                if (dbPassword.equals(password)) {
                    System.out.println("Welcome back, " + username + "!");
                    switch (userRole) {
                        case AUTHENTICATED:
                            return AuthenticatedUser.builder()
                                    .id(userId)
                                    .username(username)
                                    .userType(userRole)
                                    .build();
                        case ADMIN:
                            return AdminUser.builder()
                                    .id(userId)
                                    .username(username)
                                    .userType(userRole)
                                    .build();
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
    public final boolean promoteToAdmin(String username) throws AlreadyAdminException, UsernameNotFoundException {
        String query = "UPDATE Users SET user_rights = 'ADMIN' WHERE username = ? AND user_rights = 'AUTHENTICATED'";

        if (!checkUsername(username)){
            if (isAdmin(username)){
                throw new AlreadyAdminException("User " + username + " is already an admin.");
            }
        } else {
            throw new UsernameNotFoundException("User " + username + " not found.");
        }
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            int result = preparedStatement.executeUpdate();
            if (result > 0){
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("SQL exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if the user is an admin
     * @param username The username to be checked
     * @return Whether the user is an admin or not
     */
    private boolean isAdmin(String username){
        String query = "SELECT user_rights FROM Users WHERE username = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("user_rights").equals("ADMIN");
            }
        } catch (SQLException e) {
            System.out.println("SQL exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verify if the username exists in the database
     * @param username The username to be verified.
     * @return Whether the username exists or not.
     */
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
        } catch (SQLException e) {
            System.out.println("SQL exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get the user id from a User instance
     * @param user The user instance
     * @return The user id
     */
    public final int getUserId(User user){
        return user.getId();
    }

    /**
     * Get the user id from a username
     * @param username The username
     * @return The user id
     */
    public final int getUserId(String username) throws UsernameNotFoundException {
        if (checkUsername(username)){
            throw new UsernameNotFoundException("User " + username + " not found.");
        } else {
            String query = "SELECT id FROM Users WHERE username = ?;";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return resultSet.getInt("id");
                }
            } catch (SQLException e) {
                System.out.println("SQL exception: " + e.getMessage());
            }
        }
        return -1;
    }

    /**
     * Get the number of users in the database;
     * Used to see if a user is the first one registered
     * @return The number of users in the database
     */
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

    /**
     * Get users from the database
     * @param page: the page number to be retrieved
     * @param pageSize: the number of users to be retrieved
     * @return A list of users
     */
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
                UserRole userRole = UserRole.valueOf(resultSet.getString("user_rights"));
                User user;
                if (userRole == UserRole.ADMIN){
                    user = AdminUser.builder()
                            .id(resultSet.getInt("id"))
                            .username(resultSet.getString("username"))
                            .userType(userRole)
                            .build();
                } else {
                    user = AuthenticatedUser.builder()
                            .id(resultSet.getInt("id"))
                            .username(resultSet.getString("username"))
                            .userType(userRole)
                            .build();
                }
                users.add(user);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return users;
    }
}
