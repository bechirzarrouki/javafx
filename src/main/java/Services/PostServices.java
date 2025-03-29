package Services;

import Models.Post;
import Models.User;
import Models.Comment;
import Main.DatabaseConnection;
import Services.UserServices;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostServices {
    private Connection cnx;

    public PostServices() {
        cnx = DatabaseConnection.instance.getCnx();
    }

    public void add(Post post) {
        String query = "INSERT INTO post (content, author_id, image, created_at) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, post.getContent());
            stm.setInt(2, post.getAuthor().getId());
            stm.setString(3, post.getImage());
            stm.setTimestamp(4, Timestamp.valueOf(post.getCreatedAt()));

            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Post post) {
        String query = "UPDATE post SET content = ?, image = ? WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, post.getContent());
            stm.setString(2, post.getImage());
            stm.setInt(3, post.getId());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int postId) {
        String query = "DELETE FROM post WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, postId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM post";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setContent(rs.getString("content"));
                post.setImage(rs.getString("image"));

                posts.add(post);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    public Post getById(int postId) {
        String query = "SELECT * FROM post WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, postId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setContent(rs.getString("content"));
                post.setImage(rs.getString("image"));
                return post;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void addLike(int postId, int userId) {
        String query = "INSERT INTO post_likes (post_id, user_id) VALUES (?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, postId);
            stm.setInt(2, userId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLike(int postId, int userId) {
        String query = "DELETE FROM post_likes WHERE post_id = ? AND user_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, postId);
            stm.setInt(2, userId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getLikeCount(int postId) {
        String query = "SELECT COUNT(*) FROM post_likes WHERE post_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, postId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
