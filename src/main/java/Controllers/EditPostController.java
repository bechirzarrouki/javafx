package Controllers;

import Models.Post;
import Services.PostServices;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class EditPostController {
    @FXML private TextField contentField;
    @FXML private ImageView imageView;

    private File selectedFile;
    private Post postToEdit;
    private final PostServices postService = new PostServices();

    private PostController postController; // reference to main controller

    public void setPost(Post post) {
        this.postToEdit = post;
        contentField.setText(post.getContent());

        if (post.getImage() != null) {
            File imageFile = new File("src/main/resources/images/" + post.getImage());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
        }
    }

    // This method allows the main post list controller to be injected
    public void setPostController(PostController controller) {
        this.postController = controller;
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a New Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedFile != null) {
            imageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleSave() {
        try {
            postToEdit.setContent(contentField.getText());

            if (selectedFile != null) {
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destination = Paths.get("src/main/resources/images/" + fileName);
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                postToEdit.setImage(fileName);
            }

            postService.update(postToEdit);

            if (postController != null) {
                postController.refreshPosts(); // trigger post list refresh
            }

            closeWindow();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error updating post image.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) contentField.getScene().getWindow();
        stage.close();
    }
}
