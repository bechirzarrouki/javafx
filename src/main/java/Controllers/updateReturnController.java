package Controllers;

import Services.TReturnService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import Models.TReturn;
import javafx.stage.Stage;

public class updateReturnController {
    private TReturn selectedReturn;
    private TReturnService service = new TReturnService();
    private Runnable refreshCallback;

    @FXML private TextField descriptionField;
    @FXML private TextField typeRetourField;
    @FXML private TextField tauxRendementField;
    @FXML private DatePicker dateDeadlineField;
    @FXML private TextField statusField;

    public void setReturn(TReturn tReturn) {
        this.selectedReturn = tReturn;
        populateFields();
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    private void populateFields() {
        if (selectedReturn != null) {
            descriptionField.setText(selectedReturn.getDescription());
            typeRetourField.setText(selectedReturn.getReturnType());
            tauxRendementField.setText(String.valueOf(selectedReturn.getRendement()));
            dateDeadlineField.setValue(selectedReturn.getDeadline());
            statusField.setText(selectedReturn.getStatus());
        }
    }

    @FXML
    private void updateReturn(ActionEvent event) {
        if (selectedReturn == null) {
            showAlert("Error", "No return selected for update");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try {
            updateSelectedReturn();
            service.update(selectedReturn);
            showAlert("Success", "Return updated successfully!");
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid number format for rate of return");
        } catch (Exception e) {
            showAlert("Error", "Failed to update return: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (descriptionField.getText().isEmpty() ||
                typeRetourField.getText().isEmpty() ||
                tauxRendementField.getText().isEmpty() ||
                dateDeadlineField.getValue() == null ||
                statusField.getText().isEmpty()) {

            showAlert("Warning", "Please fill in all fields");
            return false;
        }
        return true;
    }

    private void updateSelectedReturn() {
        selectedReturn.setDescription(descriptionField.getText());
        selectedReturn.setReturnType(typeRetourField.getText());
        selectedReturn.setRendement(Double.parseDouble(tauxRendementField.getText()));
        selectedReturn.setDeadline(dateDeadlineField.getValue());
        selectedReturn.setStatus(statusField.getText());
    }

    @FXML
    private void cancelUpdate(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}