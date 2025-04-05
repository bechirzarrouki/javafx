package Services;

import Models.Chat;
import Main.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private final Connection cnx;

    public ChatService() {
        this.cnx = DatabaseConnection.instance.getCnx();
    }

    // Ajouter un nouveau chat
    public void add(Chat chat) {
        String query = "INSERT INTO chat (user1_id, user2_id) VALUES (?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, chat.getUser1Id());
            stm.setInt(2, chat.getUser2Id());

            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                chat.setId(rs.getInt(1)); // Récupérer l'ID généré
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du chat: " + e.getMessage(), e);
        }
    }

    // Mettre à jour un chat
    public void update(Chat chat) {
        String query = "UPDATE chat SET user1_id = ?, user2_id = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setInt(1, chat.getUser1Id());
            stm.setInt(2, chat.getUser2Id());
            stm.setInt(3, chat.getId());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du chat: " + e.getMessage(), e);
        }
    }

    // Supprimer un chat par ID
    public void delete(int chatId) {
        String query = "DELETE FROM chat WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setInt(1, chatId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du chat: " + e.getMessage(), e);
        }
    }

    // Récupérer un chat par ID
    public Chat getById(int chatId) {
        String query = "SELECT * FROM chat WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setInt(1, chatId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new Chat(
                        rs.getInt("id"),
                        rs.getInt("user1_id"),
                        rs.getInt("user2_id")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du chat: " + e.getMessage(), e);
        }
        return null;
    }

    // Lister tous les chats
    public List<Chat> getAll() {
        List<Chat> chatList = new ArrayList<>();
        String query = "SELECT * FROM chat";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                Chat chat = new Chat(
                        rs.getInt("id"),
                        rs.getInt("user1_id"),
                        rs.getInt("user2_id")
                );
                chatList.add(chat);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des chats: " + e.getMessage(), e);
        }
        return chatList;
    }

    // Vérifier si un chat existe entre deux utilisateurs
    public Chat getChatByUsers(int user1Id, int user2Id) {
        String query = "SELECT * FROM chat WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setInt(1, user1Id);
            stm.setInt(2, user2Id);
            stm.setInt(3, user2Id);
            stm.setInt(4, user1Id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new Chat(
                        rs.getInt("id"),
                        rs.getInt("user1_id"),
                        rs.getInt("user2_id")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du chat entre les utilisateurs: " + e.getMessage(), e);
        }
        return null;
    }
}
