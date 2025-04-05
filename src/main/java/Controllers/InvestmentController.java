package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class InvestmentController {

    // Références aux modales
    @FXML private VBox typeModal;
    @FXML private VBox retourModal;

    // Liste des types d'investissement
    @FXML private ListView<String> typeList;

    // Champs du formulaire principal
    @FXML private TextField contentField;

    // Champs du formulaire de retour
    @FXML private TextField descriptionField;
    @FXML private TextField typeRetourField;
    @FXML private TextField tauxRendementField;
    @FXML private DatePicker dateDeadlineField;
    @FXML private TextField statusField;

    // Méthode d'initialisation
    @FXML
    public void initialize() {
        // Remplacer l'initialisation par :
        if (typeList != null) {
            typeList.getItems().addAll(
                    "Stocks",
                    "Bonds",
                    "Real Estate",
                    "Crypto"
            );
        } else {
            System.err.println("Erreur: typeList non initialisé !");
        }
    }

    // Gestion des types d'investissement
    @FXML
    public void openTypeModal() {
        typeModal.setVisible(true);
        typeModal.setOpacity(1);  // Assurer que le modal est totalement opaque
        typeModal.setManaged(true); // Le modal est géré et visible
    }

    @FXML
    public void closeTypeModal() {
        typeModal.setOpacity(0);  // Réduire l'opacité à 0
        typeModal.setManaged(false); // Cacher le modal
        typeModal.setVisible(false);  // Le modal n'est plus visible
    }

    @FXML
    public void submitType() {
        String selected = typeList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            contentField.setText(selected);
            closeTypeModal();  // Fermer le modal après sélection
        }
    }

    // Gestion des retours
    @FXML
    public void openReturnModal() {
        retourModal.setVisible(true);  // Afficher le modal
        retourModal.setOpacity(1);  // Rendre le modal totalement opaque
        retourModal.setManaged(true);  // Le modal est géré
        resetReturnForm();  // Réinitialiser les champs du formulaire
    }

    @FXML
    public void closeReturnModal() {
        retourModal.setOpacity(0);  // Réduire l'opacité à 0
        retourModal.setManaged(false);  // Le modal n'est plus géré
        retourModal.setVisible(false);  // Cacher le modal
    }

    @FXML
    public void submitReturn() {
        if (validateReturnForm()) {
            handleReturnSubmission();
            closeReturnModal();  // Fermer le modal après soumission
        }
    }

    // Soumission de l'investissement principal
    @FXML
    public void submitInvestment() {
        // Vérifier si un type d'investissement a été sélectionné
        if (contentField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un type d'investissement");
            return;
        }

        // Vérifier que le type d'investissement est valide (par exemple, minimum 3 caractères)
        if (contentField.getText().length() < 3) {
            showAlert("Erreur", "Le type d'investissement doit contenir au moins 3 caractères");
            return;
        }

        System.out.println("Investment created: " + contentField.getText());
    }

    // Méthodes utilitaires
    private void resetReturnForm() {
        descriptionField.clear();
        typeRetourField.clear();
        tauxRendementField.clear();
        dateDeadlineField.setValue(null);
        statusField.clear();
    }

    private boolean validateReturnForm() {
        // Vérification des champs obligatoires
        if (descriptionField.getText().isEmpty() || descriptionField.getText().length() < 5) {
            showAlert("Erreur", "La description doit être remplie et contenir au moins 5 caractères");
            return false;
        }

        if (typeRetourField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez spécifier le type de retour");
            return false;
        }

        if (tauxRendementField.getText().isEmpty() || !tauxRendementField.getText().matches("\\d+(\\.\\d{1,2})?")) {
            showAlert("Erreur", "Veuillez entrer un taux de rendement valide (exemple : 5.25)");
            return false;
        }

        return true;
    }

    private void handleReturnSubmission() {
        System.out.println("Return submitted:");
        System.out.println("- Description: " + descriptionField.getText());
        System.out.println("- Type: " + typeRetourField.getText());
        System.out.println("- Taux: " + tauxRendementField.getText());
        System.out.println("- Date: " + dateDeadlineField.getValue());
        System.out.println("- Statut: " + statusField.getText());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
