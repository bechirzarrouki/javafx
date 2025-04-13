package Controllers;

import Main.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProfileController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;

    @FXML
    private ImageView profileImageView;
    
    @FXML
    private Button changeImageButton;
    
    @FXML
    private TextField phoneNumberField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label errorLabel;
    
    private int userId;
    private File selectedImageFile;

    @FXML
    public void initialize() {
        // Add click handler for image change
        changeImageButton.setOnAction(e -> handleChangeImage());
    }

    private void handleChangeImage() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        selectedImageFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedImageFile != null) {
            try {
                // Save the new image
                String fileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
                File targetDir = new File("target/classes/uploads/profiles");
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }

                File destFile = new File(targetDir, fileName);
                Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Update image in UI
                Image image = new Image(destFile.toURI().toString());
                profileImageView.setImage(image);
                
                // Update database
                String imagePath = "uploads/profiles/" + fileName;
                Connection conn = DatabaseConnection.getInstance().getCnx();
                String updateQuery = "UPDATE user SET profile_image = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(updateQuery);
                ps.setString(1, imagePath);
                ps.setInt(2, userId);
                ps.executeUpdate();
                
                System.out.println("Profile image updated successfully: " + imagePath);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to update profile image");
                alert.setContentText("Please try again.");
                alert.showAndWait();
            }
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadUserData();
    }

    public void setUsername(String username) {
        if (usernameField != null) {
            usernameField.setText(username);
        }
    }

    public void setEmail(String email) {
        if (emailField != null) {
            emailField.setText(email);
        }
    }
    
    private void loadUserData() {
        try {
            Connection conn = DatabaseConnection.getInstance().getCnx();
            String query = "SELECT username, email, number, profile_image FROM user WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));
                Integer phoneNum = rs.getObject("number") != null ? rs.getInt("number") : null;
                phoneNumberField.setText(phoneNum != null ? phoneNum.toString() : "");

                // Load profile image
                String imagePath = rs.getString("profile_image");
                System.out.println("Loading profile image from DB path: " + imagePath);
                
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        File targetDir = new File("target/classes");
                        File imageFile = new File(targetDir, imagePath);
                        
                        System.out.println("Full image path: " + imageFile.getAbsolutePath());
                        System.out.println("File exists: " + imageFile.exists());
                        
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString());
                            profileImageView.setImage(image);
                            System.out.println("Successfully loaded profile image");
                        } else {
                            // Try loading from resources as fallback
                            String resourcePath = "/" + imagePath.replace("\\", "/");
                            InputStream imageStream = getClass().getResourceAsStream(resourcePath);
                            if (imageStream != null) {
                                Image image = new Image(imageStream);
                                profileImageView.setImage(image);
                                System.out.println("Successfully loaded profile image from resources");
                            } else {
                                throw new Exception("Image file not found in any location");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error loading profile image: " + e.getMessage());
                        e.printStackTrace();
                        // Load default avatar on error
                        profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default-avatar.png")));
                    }
                } else {
                    System.out.println("No profile image path in DB, using default avatar");
                    profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default-avatar.png")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String password = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validation
        if (username.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            showErrorMessage("Username, email, and phone number are required!");
            return;
        }
        
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            showErrorMessage("Invalid email format!");
            return;
        }
        
        if (!phoneNumber.matches("\\d{8}")) {
            showErrorMessage("Invalid phone number format! (8 digits)");
            return;
        }
        
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match!");
            return;
        }
        
        try {
            Connection conn = DatabaseConnection.getInstance().getCnx();
            String query;
            PreparedStatement ps;
            
            if (password.isEmpty()) {
                // Update without password
                query = "UPDATE user SET username = ?, email = ?, number = ? WHERE id = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, phoneNumber);
                ps.setInt(4, userId);
            } else {
                // Update with password
                query = "UPDATE user SET username = ?, email = ?, number = ?, password = ? WHERE id = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, phoneNumber);
                ps.setString(4, BCrypt.hashpw(password, BCrypt.gensalt()));
                ps.setInt(5, userId);
            }
            
            int result = ps.executeUpdate();
            if (result > 0) {
                showSuccess("Profile updated successfully!");
                // Clear password fields
                newPasswordField.clear();
                confirmPasswordField.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating profile: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error loading login page: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().remove("error");
    }

    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().add("error");
    }
}
