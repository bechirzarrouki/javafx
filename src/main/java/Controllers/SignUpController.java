package Controllers;

import Main.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;
import Utils.AnimationUtils;
import javafx.scene.layout.VBox;
import javafx.animation.PauseTransition;

import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField numberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox employeeCheckBox;

    @FXML
    private CheckBox investorCheckBox;

    @FXML
    private CheckBox projectLeaderCheckBox;

    @FXML
    public void initialize() {
        // Animate form elements sequentially
        VBox form = (VBox) usernameField.getParent();
        double delay = 0;
        for (javafx.scene.Node node : form.getChildren()) {
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(delay));
            pause.setOnFinished(e -> AnimationUtils.fadeInUp(node));
            pause.play();
            delay += 0.1; // Add 100ms delay between each element
        }
    }

    @FXML
    private void handleSignUp() {
        // Reset error styles
        resetErrorStyles();
        
        // Get trimmed values
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String number = numberField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validate all fields are filled
        boolean hasError = false;
        
        if (username.isEmpty()) {
            setErrorStyle(usernameField);
            hasError = true;
        }
        
        if (email.isEmpty()) {
            setErrorStyle(emailField);
            hasError = true;
        } else if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            setErrorStyle(emailField);
            showError("Invalid email format!");
            return;
        }
        
        if (number.isEmpty()) {
            setErrorStyle(numberField);
            hasError = true;
        } else if (!number.matches("\\d{8}")) {
            setErrorStyle(numberField);
            showError("Phone number must be exactly 8 digits!");
            return;
        }
        
        if (password.isEmpty()) {
            setErrorStyle(passwordField);
            hasError = true;
        } else if (password.length() < 6) {
            setErrorStyle(passwordField);
            showError("Password must be at least 6 characters long!");
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            setErrorStyle(confirmPasswordField);
            hasError = true;
        }
        
        if (hasError) {
            showError("All fields are required!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            setErrorStyle(passwordField);
            setErrorStyle(confirmPasswordField);
            showError("Passwords do not match!");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getCnx();
            
            // Check if username already exists
            PreparedStatement checkUsername = conn.prepareStatement("SELECT COUNT(*) FROM user WHERE username = ?");
            checkUsername.setString(1, username);
            ResultSet rsUsername = checkUsername.executeQuery();
            rsUsername.next();
            if (rsUsername.getInt(1) > 0) {
                setErrorStyle(usernameField);
                showError("Username already exists!");
                return;
            }
            
            // Check if email already exists
            PreparedStatement checkEmail = conn.prepareStatement("SELECT COUNT(*) FROM user WHERE email = ?");
            checkEmail.setString(1, email);
            ResultSet rsEmail = checkEmail.executeQuery();
            rsEmail.next();
            if (rsEmail.getInt(1) > 0) {
                setErrorStyle(emailField);
                showError("Email already exists!");
                return;
            }
            
            // If all validations pass, insert new user
            String query = "INSERT INTO user (username, email, number, password, roles) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setInt(3, Integer.parseInt(number));
            ps.setString(4, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setString(5, "ROLE_USER");

            int result = ps.executeUpdate();
            if (result > 0) {
                showSuccess("Account created successfully! You can customize your profile picture in your profile settings.");
                
                // Refresh admin dashboard if it exists
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                for (Window window : Stage.getWindows()) {
                    if (window instanceof Stage && window != currentStage) {
                        Scene scene = ((Stage) window).getScene();
                        if (scene != null && scene.getRoot().getId() != null && scene.getRoot().getId().equals("adminDashboard")) {
                            AdminDashboardController controller = (AdminDashboardController) scene.getUserData();
                            if (controller != null) {
                                controller.refreshTable();
                            }
                        }
                    }
                }

                // Clear the form
                clearForm();
                // Add a small delay before navigating to login
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> navigateToLogin());
                pause.play();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error registering user: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        loadScene("/login.fxml");
    }

    private void setErrorStyle(TextField field) {
        field.getStyleClass().add("error");
    }

    private void resetErrorStyles() {
        usernameField.getStyleClass().remove("error");
        emailField.getStyleClass().remove("error");
        numberField.getStyleClass().remove("error");
        passwordField.getStyleClass().remove("error");
        confirmPasswordField.getStyleClass().remove("error");
    }

    private void loadScene(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading " + path + ": " + e.getMessage());
            errorLabel.setText("Error loading page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        AnimationUtils.shakeAnimation(errorLabel);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        AnimationUtils.pulseAnimation(errorLabel);
    }

    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        numberField.clear();
    }

    private void navigateToLogin() {
        loadScene("/login.fxml");
    }
}
