package Controllers;

import Models.UserTableData;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Utils.AnimationUtils;
import javafx.scene.layout.GridPane;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class UpdateUserDialogController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;

    @FXML
    private TextField numberField;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    private UserTableData user;
    private boolean saveClicked = false;
    
    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll(
            "ROLE_INVESTOR",
            "ROLE_PROJECTLEADER",
            "ROLE_EMPLOYEE"
        );

        // Animate form fields
        AnimationUtils.fadeInUp(usernameField);
        PauseTransition pause1 = new PauseTransition(Duration.seconds(0.1));
        pause1.setOnFinished(e -> AnimationUtils.fadeInUp(emailField));
        pause1.play();

        PauseTransition pause2 = new PauseTransition(Duration.seconds(0.2));
        pause2.setOnFinished(e -> AnimationUtils.fadeInUp(numberField));
        pause2.play();

        PauseTransition pause3 = new PauseTransition(Duration.seconds(0.3));
        pause3.setOnFinished(e -> AnimationUtils.fadeInUp(roleComboBox));
        pause3.play();
    }
    
    public void setUser(UserTableData user) {
        this.user = user;
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        numberField.setText(user.getNumber() != null ? user.getNumber().toString() : "");
        roleComboBox.setValue(user.getRole());
    }
    
    @FXML
    private void handleSave() {
        if (isInputValid()) {
            user.setUsername(usernameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            try {
                user.setNumber(numberField.getText().trim().isEmpty() ? null : Integer.parseInt(numberField.getText().trim()));
            } catch (NumberFormatException e) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Invalid Phone Number");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid phone number!");
                alert.showAndWait();
                return;
            }
            user.setRole(roleComboBox.getValue());
            
            saveClicked = true;
            AnimationUtils.pulseAnimation(usernameField.getScene().getRoot());
            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(e -> closeDialog());
            pause.play();
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        
        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            errorMessage += "Username is required!\n";
        }
        
        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            errorMessage += "Email is required!\n";
        } else if (!emailField.getText().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            errorMessage += "Invalid email format!\n";
        }
        
        String phoneNumber = numberField.getText().trim();
        if (!phoneNumber.isEmpty()) {
            if (!phoneNumber.matches("\\d{8}")) {
                errorMessage += "Phone number must be exactly 8 digits!\n";
            }
        }

        if (roleComboBox.getValue() == null) {
            errorMessage += "Role is required!\n";
        }
        
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Show error alert
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
    
    public boolean isSaveClicked() {
        return saveClicked;
    }
    
    public UserTableData getUser() {
        return user;
    }
}
