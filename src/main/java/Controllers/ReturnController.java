package Controllers;

import Services.TReturnService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.scene.layout.VBox;
import Models.TReturn;
import Models.Investment;
import java.util.List;

public class ReturnController {
    private Investment investmentId;
    @FXML private TextField descriptionField;
    @FXML private TextField typeRetourField;
    @FXML private TextField tauxRendementField;
    @FXML private DatePicker dateDeadlineField;
    @FXML private TextField statusField;


    public void setInvestmentId(Investment investmentId) {
        this.investmentId = investmentId;
    }
    TReturnService service = new TReturnService();

    @FXML
    private void submitReturn(ActionEvent event) {
        String description = descriptionField.getText();
        String type = typeRetourField.getText();
        String tauxStr = tauxRendementField.getText();
        LocalDate deadline = dateDeadlineField.getValue();
        String status = statusField.getText();

        if (description.isEmpty() || type.isEmpty() || tauxStr.isEmpty() || deadline == null || status.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try {
            double taux = Double.parseDouble(tauxStr);


            TReturn tReturn = new TReturn();
            tReturn.setDescription(description);
            tReturn.setReturnType(type);
            tReturn.setRendement(taux);
            tReturn.setDeadline(deadline);
            tReturn.setStatus(status);
            tReturn.setInvestmentId(this.investmentId);
            service.add(tReturn);
            System.out.println(tReturn.toString());

            clearForm();
            showAlert("Return saved successfully!");

        } catch (NumberFormatException e) {
            showAlert("Rate of return must be a valid number.");
        }
    }

    @FXML
    private void cancelReturn(ActionEvent event) {
        clearForm();
        showAlert("Return cancelled.");
    }

    private void clearForm() {
        descriptionField.clear();
        typeRetourField.clear();
        tauxRendementField.clear();
        dateDeadlineField.setValue(null);
        statusField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
