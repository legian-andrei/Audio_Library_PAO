package org.example.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuditService {
    private final Connection conn;

    public AuditService(Connection connection) {conn = connection;}

    /**
     * Log command into database
     * @param userId the user id that executed the command
     * @param command the command executed
     * @return true if the command was logged successfully, false otherwise
     */
    public final boolean logCommand(int userId, String command){
        String query = "INSERT INTO UserAuditor (userId, command) VALUES (?,?);";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, command);
            int result = preparedStatement.executeUpdate();
            if (result > 0){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the audit logs for a specified user.
     * @param userId the user id to get the logs for
     * @param page the page number
     * @param pageSize the number of logs per page
     * @return a list of audit logs
     */
    public final List<AuditLog> getAuditLogs(int userId, int page, int pageSize){
        String query = """
                SELECT id, userId, command, timestamps 
                FROM UserAuditor 
                WHERE userId = ? 
                LIMIT ? OFFSET ?;""";
        List<AuditLog> logs = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, (page - 1) * pageSize);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                AuditLog currentLog = AuditLog.builder()
                        .id(resultSet.getInt("id"))
                        .userId(resultSet.getInt("userId"))
                        .command(resultSet.getString("command"))
                        .timestamp(resultSet.getTimestamp("timestamps"))
                        .build();
                logs.add(currentLog);
            }
            return logs;
        } catch (Exception e) {
            e.printStackTrace();
            return logs;
        }
    }

    /**
     * Get the number of audit logs for a specified user.
     * @param userId the user id to get the number of logs for
     * @return the number of audit logs
     */
    public final int getAuditSize(int userId){
        String query = """
                SELECT COUNT(*) AS countAudit
                FROM UserAuditor 
                WHERE userId = ? ;""";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("countAudit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
