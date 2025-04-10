//task
//change the user when the session is added
//add reload to all the functions needed (CRUD/APIS)
//change the css so it looks better
package Controllers;
import Models.Post;
import Models.User;
import Services.PostServices;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
public class PostController {
    private File selectedFile;
    private final PostServices postService = new PostServices();

    @FXML
    private TextField postContentField;
    @FXML
    private ImageView postImageView;
    @FXML
    private VBox postsContainer;

    @FXML
    public void initialize() {
        loadPosts(); // Load posts when UI initializes
    }
    private void loadPosts() {
        postsContainer.getChildren().clear(); // Clear old posts
        List<Post> posts = postService.getAll(); // Fetch posts from DB

        for (Post post : posts) {
            VBox postBox = createPostBox(post);
            postsContainer.getChildren().add(postBox);
        }
    }
    private VBox createPostBox(Post post) {
        VBox postBox = new VBox(10);
        postBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-padding: 15;");

        // Post Header
        HBox postHeader = new HBox(10);
        postHeader.setSpacing(10);
        postHeader.setStyle("-fx-alignment: CENTER_LEFT;");

        ImageView avatarImage = new ImageView(new Image("file:/images/avatar.jpg")); // Default avatar
        avatarImage.setFitWidth(50);
        avatarImage.setFitHeight(30);
        avatarImage.setPreserveRatio(true);

        VBox userInfo = new VBox(2);
        Label usernameLabel = new Label(post.getAuthor().getUsername());
        usernameLabel.setStyle("-fx-font-weight: bold;");
        Label dateLabel = new Label(post.getCreatedAt().toString());
        dateLabel.setStyle("-fx-font-size: 10;");
        userInfo.getChildren().addAll(usernameLabel, dateLabel);

        Button favoriteButton = new Button("Favorite");

        // Spacer to push buttons to the right
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // New Buttons (Edit & Delete)
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> handleEditPost(post));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDeletePost(post));

        HBox buttonContainer = new HBox(5, editButton, deleteButton);

        // Add elements to postHeader
        postHeader.getChildren().addAll(avatarImage, userInfo, favoriteButton, spacer, buttonContainer);

        // Post Content
        Label postContent = new Label(post.getContent());
        postContent.setWrapText(true);

        // Post Image
        ImageView postImageView = new ImageView();
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            postImageView.setImage(new Image(getClass().getResource("/images/" + post.getImage()).toExternalForm()));
            postImageView.setFitWidth(300);
            postImageView.setFitHeight(300);
            postImageView.setPreserveRatio(true);
            postImageView.setVisible(true);
        } else {
            postImageView.setVisible(false);
        }

        // Like & Comments
        HBox postStats = new HBox(10);
        Button likeButton = new Button("Like");
        Label likeCountLabel = new Label("Likes: " + post.getLikes());
        postStats.getChildren().addAll(likeButton, likeCountLabel);

        // Comments Section
        VBox commentsSection = new VBox(5);
        Label commentsLabel = new Label("Comments (" + post.getComments().size() + ")");
        commentsLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        commentsSection.getChildren().add(commentsLabel);

        // Comment Box
        HBox commentInputBox = new HBox(10);
        TextField commentField = new TextField();
        commentField.setPromptText("Post a comment...");
        Button postCommentButton = new Button("Post");
        commentInputBox.getChildren().addAll(commentField, postCommentButton);

        // Add all elements to post box
        postBox.getChildren().addAll(postHeader, postContent, postImageView, postStats, commentsSection, commentInputBox);

        return postBox;
    }

    // Methods to handle Edit & Delete actions
    private void handleEditPost(Post post) {
        System.out.println("Editing post: " + post.getId());
        // Implement edit logic here
    }

    private void handleDeletePost(Post post) {
        System.out.println("Deleting post: " + post.getId());
        postService.delete(post.getId());
    }

    @FXML
    private void handleCreatePost(){
        if (selectedFile == null) {
            System.out.println("No image selected.");
            return;
        }

        User newUser = new User(28,"john_doe", "john.doe@example.com", "securepassword",123456789,"profile.jpg",false);
        try {
            Path destinationFolder = Paths.get("src/main/resources/images");
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path destinationPath = destinationFolder.resolve(fileName);
            Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Post newPost = new Post(postContentField.getText(),newUser,fileName);

            postService.add(newPost);
            System.out.println("Create Post " + postContentField.getText());
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving image.");
        }
    }
    @FXML
    private void handleAddPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
                Image image = new Image(selectedFile.toURI().toString());
                System.out.println("Add image " + selectedFile.getName());
                postImageView.setImage(image);
        }
    }

}
