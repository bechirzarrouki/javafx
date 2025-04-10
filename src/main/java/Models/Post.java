package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private Integer id;
    private String content;
    private User author;
    private List<Comment> comments;
    private String image;
    private LocalDateTime createdAt;
    private List<User> likes;

    public Post() {
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.createdAt = LocalDateTime.now(); // Automatically set creation date
    }

    public Post(String content, User author,String image) {
        this.content = content;
        this.author = author;
        this.image = image;
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.createdAt = LocalDateTime.now(); // Automatically set creation date
    }


    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        if (!comments.contains(comment)) {
            comments.add(comment);
            comment.setPost(this);
        }
    }

    public void removeComment(Comment comment) {
        if (comments.remove(comment)) {
            comment.setPost(null);
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public int getLikes() {
        return likes.size();
    }
    public void setLikes(){

    }
    public void addLike(User user) {
        if (!likes.contains(user)) {
            likes.add(user);
        }
    }

    public void removeLike(User user) {
        likes.remove(user);
    }

    public boolean isLikedByUser(User user) {
        return user != null && likes.contains(user);
    }
}
