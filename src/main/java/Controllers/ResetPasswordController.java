package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

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

        // TODO: Implement password reset logic here
        // For now, just show a success message
        errorLabel.setText("Password reset instructions have been sent to your email.");
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
}
