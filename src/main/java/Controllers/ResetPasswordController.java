package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import Utils.EmailUtil;

public class ResetPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();

        // Validation
        if (email.isEmpty()) {
            errorLabel.setText("Email field cannot be empty.");
            return;
        }
        if (!isValidEmail(email)) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        // Check if email exists
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ups = null;
        ResultSet rs = null;
        
        try {
            conn = Main.DatabaseConnection.getInstance().getCnx();
            
            // Check if email exists in database
            String query = "SELECT id FROM user WHERE email = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            
            if (!rs.next()) {
                errorLabel.setText("No account found for this email.");
                return;
            }
            
            int userId = rs.getInt("id");
            
            // Generate secure code
            String code = generateCode(6);
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
            
            // Store code and expiry in DB
            String update = "UPDATE user SET reset_token = ?, reset_token_expiry = ? WHERE id = ?";
            ups = conn.prepareStatement(update);
            ups.setString(1, code);
            ups.setTimestamp(2, Timestamp.valueOf(expiry));
            ups.setInt(3, userId);
            ups.executeUpdate();
            
            // Send email with professional HTML formatting
            try {
                // Use the new HTML email method for a more professional look
                EmailUtil.sendPasswordResetEmail(email, code);
                errorLabel.setStyle("-fx-text-fill: green");
                errorLabel.setText("Reset code sent! Check your email.");
                
                // Navigate to code entry page after a short delay
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> loadScene("/enter_reset_code.fxml"));
                pause.play();
            } catch (Exception ex) {
                errorLabel.setText("Failed to send email: " + ex.getMessage());
                ex.printStackTrace();
            }
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

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        loadScene("/login.fxml");
    }

    private void loadScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(regex, email);
    }

    private String generateCode(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
