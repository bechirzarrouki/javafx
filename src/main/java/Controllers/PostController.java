package Controllers;

import Models.Post;
import Models.User;
import Services.PostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PostController {
    private File selectedFile;


    @FXML
    private TextField postContentField;
    @FXML
    private ImageView postImageView;



    @FXML
    private void handleCreatePost(){
        if (selectedFile == null) {
            System.out.println("No image selected.");
            return;
        }
        //task
        //change the user when the session is added
        User newUser = new User(28,"john_doe", "john.doe@example.com", "securepassword",123456789,"profile.jpg",false);
        try {
            Path destinationFolder = Paths.get("src/main/resources/images");
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path destinationPath = destinationFolder.resolve(fileName);
            Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Post newPost = new Post(postContentField.getText(),newUser,fileName);
            PostServices postService = new PostServices();
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
