package Services;

import Models.Messages;
import Main.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessagesServices {
    private Connection cnx;

    public MessagesServices() {
        cnx = DatabaseConnection.instance.getCnx();
    }

    // Add a new message
    public void add(Messages message) {
        String query = "INSERT INTO messages (chat_id, sender_id, content, sent_at) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stm.setInt(1, message.getChatId());
            stm.setInt(2, message.getSenderId());
            stm.setString(3, message.getContent());
            stm.setTimestamp(4, Timestamp.valueOf(message.getSentAt())); // Convert LocalDateTime to Timestamp

            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                message.setId(rs.getInt(1)); // Set the generated ID
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding message: " + e.getMessage(), e);
        }
    }

    // Update an existing message
    public void update(Messages message) {
        String query = "UPDATE messages SET chat_id = ?, sender_id = ?, content = ?, sent_at = ? WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, message.getChatId());
            stm.setInt(2, message.getSenderId());
            stm.setString(3, message.getContent());
            stm.setTimestamp(4, Timestamp.valueOf(message.getSentAt())); // Convert LocalDateTime to Timestamp
            stm.setInt(5, message.getId());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating message: " + e.getMessage(), e);
        }
    }

    // Delete a message by its ID
    public void delete(int messageId) {
        String query = "DELETE FROM messages WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, messageId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting message: " + e.getMessage(), e);
        }
    }

    // Get all messages
    public List<Messages> getAll() {
        List<Messages> messagesList = new ArrayList<>();
        String query = "SELECT * FROM messages";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                Messages message = new Messages();
                message.setId(rs.getInt("id"));
                message.setChatId(rs.getInt("chat_id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setContent(rs.getString("content"));
                message.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime()); // Convert Timestamp to LocalDateTime

                messagesList.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all messages: " + e.getMessage(), e);
        }
        return messagesList;
    }

    // Get a message by its ID
    public Messages getById(int messageId) {
        String query = "SELECT * FROM messages WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, messageId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Messages message = new Messages();
                message.setId(rs.getInt("id"));
                message.setChatId(rs.getInt("chat_id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setContent(rs.getString("content"));
                message.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime()); // Convert Timestamp to LocalDateTime
                return message;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching message by ID: " + e.getMessage(), e);
        }
        return null;
    }

    // Get all messages by chatId
    public List<Messages> getByChatId(int chatId) {
        List<Messages> messagesList = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE chat_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, chatId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Messages message = new Messages();
                message.setId(rs.getInt("id"));
                message.setChatId(rs.getInt("chat_id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setContent(rs.getString("content"));
                message.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime()); // Convert Timestamp to LocalDateTime

                messagesList.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching messages by chatId: " + e.getMessage(), e);
        }
        return messagesList;
    }
}
