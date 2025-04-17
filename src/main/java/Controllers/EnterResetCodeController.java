package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import Main.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDateTime;

public class EnterResetCodeController {
    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleResetPassword() {
        errorLabel.setText("");
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ups = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getCnx();
            
            // First query to find the user with the reset token
            String query = "SELECT id, reset_token_expiry FROM user WHERE reset_token = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, code);
            rs = ps.executeQuery();
            
            if (!rs.next()) {
                errorLabel.setText("Invalid or expired code.");
                return;
            }
            
            LocalDateTime expiry = rs.getTimestamp("reset_token_expiry").toLocalDateTime();
            if (expiry.isBefore(LocalDateTime.now())) {
                errorLabel.setText("Reset code has expired.");
                return;
            }
            
            int userId = rs.getInt("id");
            
            // Update password and clear token
            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            String update = "UPDATE user SET password = ?, reset_token = NULL, reset_token_expiry = NULL WHERE id = ?";
            ups = conn.prepareStatement(update);
            ups.setString(1, hashed);
            ups.setInt(2, userId);
            ups.executeUpdate();
            
            errorLabel.setStyle("-fx-text-fill: green");
            errorLabel.setText("Password reset successfully! Redirecting to login page...");
            
            // Redirect to login page after a 3-second delay
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
            pause.setOnFinished(e -> loadLoginScene());
            pause.play();
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
        } finally {
            // Close resources in reverse order of creation
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (ups != null) ups.close();
                // Note: We don't close the connection here as it's managed by the DatabaseConnection singleton
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Handle the back to login button click
     */
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        loadLoginScene();
    }
    
    /**
     * Loads the login screen
     */
    private void loadLoginScene() {
        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Apply CSS if available
            if (getClass().getResource("/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            }
            
            // Get the current stage and set the new scene
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error loading login scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
