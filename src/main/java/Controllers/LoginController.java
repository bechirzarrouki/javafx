package Controllers;

import Main.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotLink;

    @FXML
    private Hyperlink signupLink;

    @FXML
    private Label errorLabel;

    @FXML
    private StackPane leftPanel;

    private MediaPlayer mediaPlayer;

    @FXML
    private ImageView gifImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                // Create MediaView programmatically
                MediaView mediaView = new MediaView();

                // Load the video file
                String videoPath = getClass().getResource("/media/gif.gif").toExternalForm();
                Media media = new Media(videoPath);
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                
                // Configure video playback
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.setMute(true);
                
                // Make video responsive
                mediaView.fitWidthProperty().bind(leftPanel.widthProperty());
                mediaView.fitHeightProperty().bind(leftPanel.heightProperty());
                mediaView.setPreserveRatio(true);
                
                // Add MediaView as the first child of StackPane (background)
                leftPanel.getChildren().add(0, mediaView);
                
            } catch (Exception e) {
                System.err.println("Error loading video: " + e.getMessage());
                e.printStackTrace();
            }
        });
        // You can add any initialization code here if needed
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Reset error styles
        resetErrorStyles();
        
        // Get trimmed values
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validate all fields are filled
        boolean hasError = false;
        
        if (username.isEmpty()) {
            setErrorStyle(usernameField);
            hasError = true;
        }
        
        if (password.isEmpty()) {
            setErrorStyle(passwordField);
            hasError = true;
        }
        
        if (hasError) {
            showError("Username and password are required!");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getCnx();
            String query = "SELECT * FROM user WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                setErrorStyle(usernameField);
                showError("Invalid username or password!");
                return;
            }

            String hashedPassword = rs.getString("password");
            if (!BCrypt.checkpw(password, hashedPassword)) {
                setErrorStyle(passwordField);
                showError("Invalid username or password!");
                return;
            }

            // Check if user is banned
            if (rs.getBoolean("banned")) {
                showError("Your account has been banned. Please contact support.");
                return;
            }

            // Store user information
            int userId = rs.getInt("id");
            String role = rs.getString("roles");
            String email = rs.getString("email");

            // Navigate based on role
            if ("ROLE_ADMIN".equals(role)) {
                loadAdminDashboard();
            } else {
                loadUserProfile(userId, username, email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Login error: " + e.getMessage());
        }
    }

    private void setErrorStyle(TextField field) {
        field.getStyleClass().add("error");
    }

    private void resetErrorStyles() {
        usernameField.getStyleClass().remove("error");
        passwordField.getStyleClass().remove("error");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().add("error");
        //AnimationUtils.shakeAnimation(errorLabel);
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        loadScene("/resetpassword.fxml");
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        loadScene("/signup.fxml");
    }

    private void loadScene(String path) {
        try {
            System.out.println("Attempting to load scene: " + path);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
            System.out.println("Scene loaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading scene: " + e.getMessage());
            errorLabel.setText("Error loading page: " + e.getMessage());
        }
    }

    private void loadAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin_dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUserProfile(int userId, String username, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_profile.fxml"));
            Parent root = loader.load();
            
            UserProfileController controller = loader.getController();
            controller.setUserId(userId);
            controller.setUsername(username);
            controller.setEmail(email);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
