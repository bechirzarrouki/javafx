package Controllers;

import Models.Investment;
import Services.InvestmentServices;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Collections;

public class UpdateInvestmentController {

    @FXML private TextField updateContentField;
    @FXML private Label selectedTypeLabel;
    @FXML private VBox typeModal;
    @FXML private ListView<String> typeList;

    private Investment investment;
    private String selectedType;

    private final InvestmentServices service = new InvestmentServices();

    // Setter called by parent controller
    public void setInvestment(Investment investment) {
        this.investment = investment;

        if (investment != null) {
            updateContentField.setText(investment.getContent());
            if (investment.getInvestmentTypes() != null && !investment.getInvestmentTypes().isEmpty()) {
                selectedType = investment.getInvestmentTypes().get(0);
                selectedTypeLabel.setText(selectedType);
            } else {
                selectedType = null;
                selectedTypeLabel.setText("No Type Selected");
            }
        } else {
            showAlert("Erreur", "Aucun investissement sélectionné.");
        }
    }

    @FXML
    public void initialize() {
        // Charger les types d'investissement dans le ListView
        typeList.setItems(FXCollections.observableArrayList("Stocks", "Bonds", "Real Estate", "Crypto"));
    }

    @FXML
    public void openTypeModal() {
        typeModal.setVisible(true);
        typeModal.setManaged(true);

        // Pré-sélectionner le type si déjà choisi
        if (selectedType != null) {
            typeList.getSelectionModel().select(selectedType);
        }

        FadeTransition ft = new FadeTransition(Duration.millis(250), typeModal);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    @FXML
    public void closeTypeModal() {
        FadeTransition ft = new FadeTransition(Duration.millis(250), typeModal);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            typeModal.setVisible(false);
            typeModal.setManaged(false);
        });
        ft.play();
    }

    @FXML
    public void submitType() {
        String type = typeList.getSelectionModel().getSelectedItem();
        if (type != null) {
            selectedType = type;
            selectedTypeLabel.setText(type);
            closeTypeModal();
        } else {
            showAlert("Erreur", "Veuillez sélectionner un type.");
        }
    }

    @FXML
    public void updateInvestment() {
        String newContent = updateContentField.getText();

        if (newContent == null || newContent.trim().length() < 3) {
            showAlert("Erreur", "Le contenu doit contenir au moins 3 caractères.");
            return;
        }

        if (selectedType == null) {
            showAlert("Erreur", "Veuillez sélectionner un type.");
            return;
        }

        // Update model
        investment.setContent(newContent.trim());
        investment.setInvestmentTypes(Collections.singletonList(selectedType));

        // Update in service
        service.update(investment);

        // Confirmation
        showInfo("Succès", "Investissement mis à jour avec succès.");

        // Close window
        closeWindow();
    }

    @FXML
    public void cancelUpdate() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) updateContentField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
