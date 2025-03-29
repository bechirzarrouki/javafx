package Services;

import Models.Comment;
import Models.Post;
import Models.User;
import Main.DatabaseConnection;
import java.time.LocalDateTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServices {
    private Connection cnx;

    public CommentServices() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    // Add a new comment
    public void add(Comment comment) {
        String req = "INSERT INTO comment (content, date_creation, date_modification, post_id, author_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, comment.getContent());
            stm.setTimestamp(2, Timestamp.valueOf(comment.getDateCreation()));
            stm.setTimestamp(3, Timestamp.valueOf(comment.getDateModification()));
            stm.setInt(4, comment.getPost().getId());
            stm.setInt(5, comment.getAuthor().getId());
            stm.executeUpdate();

            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                comment.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Modify an existing comment
    public void modify(Comment comment) {
        String req = "UPDATE comment SET content = ?, date_modification = ? WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, comment.getContent());
            stm.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stm.setInt(3, comment.getId());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Delete a comment
    public void delete(int id) {
        String req = "DELETE FROM comment WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Retrieve all comments
    public List<Comment> getAll() {
        List<Comment> comments = new ArrayList<>();
        String req = "SELECT * FROM comment";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setContent(rs.getString("content"));
                comment.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                comment.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());
                comments.add(comment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comments;
    }

    // Retrieve a comment by ID
    public Comment getById(int id) {
        String req = "SELECT * FROM comment WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setContent(rs.getString("content"));
                comment.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                comment.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());
                return comment;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}