//task
//change the user when the session is added
//add reload to all the functions needed (CRUD/APIS)
//change the css so it looks better
package Controllers;
import Models.Post;
import Models.User;
import Models.Comment;
import Services.PostServices;
import Services.CommentServices;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    private final CommentServices commentService = new CommentServices();
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

        Image avatarImg = new Image(getClass().getResource("/images/avatar.jpg").toExternalForm());
        ImageView avatarImage = new ImageView(avatarImg);
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

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Edit & Delete Buttons
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> handleEditPost(post));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDeletePost(post));

        HBox buttonContainer = new HBox(5, editButton, deleteButton);

        postHeader.getChildren().addAll(avatarImage, userInfo, favoriteButton, spacer, buttonContainer);

        // Post Content
        Label postContent = new Label(post.getContent());
        postContent.setWrapText(true);

        // Post Image
        ImageView postImageView = new ImageView();
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            try {
                postImageView.setImage(new Image(getClass().getResource("/images/" + post.getImage()).toExternalForm()));
                postImageView.setFitWidth(300);
                postImageView.setFitHeight(300);
                postImageView.setPreserveRatio(true);
                postImageView.setVisible(true);
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
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
        loadComments(post, commentsSection); // Load existing comments

        // Comment Box
        HBox commentInputBox = new HBox(10);
        TextField commentField = new TextField();
        commentField.setPromptText("Post a comment...");
        Button postCommentButton = new Button("Post");
        User newUser = new User(28, "john_doe", "john.doe@example.com", "securepassword", 123456789, "profile.jpg", false);
        postCommentButton.setOnAction(e -> {
            String commentText = commentField.getText().trim();
            if (!commentText.isEmpty()) {
                Comment newComment = new Comment();
                newComment.setContent(commentText);
                newComment.setPost(post);
                newComment.setAuthor(newUser);
                commentService.add(newComment);

                // Optionally save with commentService.add(newComment);
                commentField.clear();
                loadComments(post, commentsSection); // Refresh just this post’s comments
            }
        });

        commentInputBox.getChildren().addAll(commentField, postCommentButton);

        postBox.getChildren().addAll(postHeader, postContent, postImageView, postStats, commentsSection, commentInputBox);

        return postBox;
    }

    // Methods to handle Edit & Delete actions
    private void handleEditPost(Post post) {
        System.out.println("Editing post: " + post.getId());
        // Implement edit logic here
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_post.fxml"));
            Parent root = loader.load();

            EditPostController controller = loader.getController();
            controller.setPost(post);
            controller.setPostController(this);
            Stage stage = new Stage();
            stage.setTitle("Edit Post");
            stage.setScene(new Scene(root));
            stage.show();
            loadPosts();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void refreshPosts() {
        loadPosts(); // or however you're reloading posts from the database
    }

    private void handleDeletePost(Post post) {
        System.out.println("Deleting post: " + post.getId());
        postService.delete(post.getId());
        loadPosts();
    }

    @FXML
    private void handleCreatePost() {
        if (selectedFile == null) {
            System.out.println("No image selected.");
            return;
        }

        // Hardcoded user for now
        User newUser = new User(28, "john_doe", "john.doe@example.com", "securepassword", 123456789, "profile.jpg", false);

        try {
            Path destinationFolder = Paths.get("src/main/resources/images");
            if (!Files.exists(destinationFolder)) {
                Files.createDirectories(destinationFolder);
            }

            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path destinationPath = destinationFolder.resolve(fileName);
            Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Make sure the file was actually saved
            if (!Files.exists(destinationPath)) {
                System.out.println("Image was not saved properly.");
                return;
            }

            Post newPost = new Post(postContentField.getText(), newUser, fileName);
            postService.add(newPost);

            // Clear UI
            postContentField.clear();
            postImageView.setImage(null);
            selectedFile = null;

            // Reload posts
            reloadPage();

        } catch (IOException e) {
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
    private void reloadPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Feed.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.scene.Scene scene = postContentField.getScene();
            javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to reload the page.");
        }
    }
    private void loadComments(Post post, VBox commentsSection) {
        commentsSection.getChildren().clear();

        Label commentsLabel = new Label("Comments (" + post.getComments().size() + ")");
        commentsLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        commentsSection.getChildren().add(commentsLabel);

        for (Comment comment : commentService.getAllByPostId(post.getId())) {
            HBox commentBox = new HBox(10);
            commentBox.setAlignment(Pos.CENTER_LEFT);

            Label commentLabel = new Label(comment.getContent());
            commentLabel.setStyle("-fx-padding: 5 10 5 10; -fx-background-color: #f0f0f0; -fx-background-radius: 5;");
            HBox.setHgrow(commentLabel, Priority.ALWAYS);

            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");

            // Handle Edit
            editBtn.setOnAction(e -> {
                TextField editField = new TextField(comment.getContent());
                Button saveBtn = new Button("Save");

                commentBox.getChildren().setAll(editField, saveBtn); // Replace comment with editor

                saveBtn.setOnAction(ev -> {
                    String newText = editField.getText().trim();
                    if (!newText.isEmpty()) {
                        comment.setContent(newText);
                        commentService.modify(comment);
                        // Optionally update in DB via commentService.update(comment);
                        loadComments(post, commentsSection);
                    }
                });
            });

            // Handle Delete
            deleteBtn.setOnAction(e -> {

                commentService.delete(comment.getId());
                loadComments(post, commentsSection);
            });

            commentBox.getChildren().addAll(commentLabel, editBtn, deleteBtn);
            commentsSection.getChildren().add(commentBox);
        }
    }


}
