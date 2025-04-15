package Controllers;

import Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Models.Investment;
import Services.InvestmentServices;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class InvestmentController {
    InvestmentServices service = new InvestmentServices();
    private String typess;

    // Références aux modales
    @FXML private VBox typeModal;
    @FXML private VBox retourModal;

    // Champs d'investissement
    @FXML private TextField contentField;
    @FXML private ListView<String> typeList;
    @FXML private VBox investmentContainer;  // Conteneur des investissements

    // Champs de retour
    @FXML private TextField descriptionField;
    @FXML private TextField typeRetourField;
    @FXML private TextField tauxRendementField;
    @FXML private DatePicker dateDeadlineField;
    @FXML private TextField statusField;

    private InvestmentServices investmentService = new InvestmentServices();

    // ✅ Méthode unique appelée à l'initialisation du FXML
    @FXML
    public void initialize() {
        setupTypeList();      // Configurer la liste des types
        loadInvestments();    // Charger les investissements
    }

    private void setupTypeList() {
        typeList.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");
                }

                setOnMouseEntered(e -> {
                    if (!isEmpty()) setStyle("-fx-background-color: #dbeafe; -fx-font-weight: bold;");
                });
                setOnMouseExited(e -> {
                    if (!isEmpty()) setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 14px; -fx-border-color: #cccccc;");
                });
            }
        });
    }

    // 📌 Type Modal
    @FXML
    public void openTypeModal() {
        typeModal.setVisible(true);
        typeModal.setOpacity(1);
        typeModal.setManaged(true);
    }

    @FXML
    public void closeTypeModal() {
        typeModal.setOpacity(0);
        typeModal.setManaged(false);
        typeModal.setVisible(false);
    }

    @FXML
    public void submitType() {
        String selected = typeList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            typess = selected;
            System.out.println(typess);
            closeTypeModal();
        }
    }

    // 📌 Soumettre un investissement
    @FXML
    public void submitInvestment() {
        if (contentField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un contenu d'investissement.");
            return;
        }

        if (contentField.getText().length() < 3) {
            showAlert("Erreur", "Le contenu doit contenir au moins 3 caractères.");
            return;
        }

        Investment inv = new Investment();
        inv.setContent(contentField.getText());
        inv.setInvestmentTypes(Arrays.asList(typess.split(",")));
        User newUser = new User(1, "john_doe", "john.doe@example.com", "securepassword", 123456789, "profile.jpg", false);
        inv.setUser(newUser);
        service.add(inv);

        System.out.println("Investment created: " + contentField.getText());
        loadInvestments(); // rafraîchir après ajout
    }

    // 📌 Modal Retour
    @FXML
    public void openReturnModal() {
        retourModal.setVisible(true);
        retourModal.setOpacity(1);
        retourModal.setManaged(true);
        resetReturnForm();
    }

    @FXML
    public void closeReturnModal() {
        retourModal.setOpacity(0);
        retourModal.setManaged(false);
        retourModal.setVisible(false);
    }

    @FXML
    public void submitReturn() {
        if (validateReturnForm()) {
            handleReturnSubmission();
            closeReturnModal();
        }
    }

    private void resetReturnForm() {
        descriptionField.clear();
        typeRetourField.clear();
        tauxRendementField.clear();
        dateDeadlineField.setValue(null);
        statusField.clear();
    }

    private boolean validateReturnForm() {
        if (descriptionField.getText().isEmpty() || descriptionField.getText().length() < 5) {
            showAlert("Erreur", "La description doit contenir au moins 5 caractères.");
            return false;
        }

        if (typeRetourField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez spécifier le type de retour.");
            return false;
        }

        if (tauxRendementField.getText().isEmpty() || !tauxRendementField.getText().matches("\\d+(\\.\\d{1,2})?")) {
            showAlert("Erreur", "Veuillez entrer un taux de rendement valide (ex : 5.25)");
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

    // 📌 Chargement des investissements
    private void loadInvestments() {
        investmentContainer.getChildren().clear();
        List<Investment> investments = investmentService.getAll();

        for (Investment investment : investments) {
            VBox investmentBox = createInvestmentBox(investment);
            investmentContainer.getChildren().add(investmentBox);
        }
    }

    private VBox createInvestmentBox(Investment investment) {
        VBox investmentBox = new VBox(10);
        investmentBox.getStyleClass().add("investment-box");

        // Content
        Label contentLabel = new Label(investment.getContent());
        contentLabel.getStyleClass().add("investment-content");
        contentLabel.setWrapText(true);

        // Details
        Label typesLabel = new Label("Types: " + String.join(", ", investment.getInvestmentTypes()));
        Label dateLabel = new Label("Created: " + investment.getCreatedAt().toString());
        typesLabel.getStyleClass().add("investment-detail");
        dateLabel.getStyleClass().add("investment-detail");

        // Buttons
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button viewReturnsButton = new Button("Add Returns");

        editButton.getStyleClass().addAll("button", "edit-button");
        deleteButton.getStyleClass().addAll("button", "delete-button");
        viewReturnsButton.getStyleClass().addAll("button", "returns-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("button-container");
        buttonBox.getChildren().addAll(editButton, deleteButton, viewReturnsButton);

        // Event Handlers
        editButton.setOnAction(event -> updateInvestment(investment));
        deleteButton.setOnAction(event -> deleteInv(investment));
        viewReturnsButton.setOnAction(event -> openReturnsPage(investment));
        investmentBox.setOnMouseClicked(event -> openInvestmentDetailsPage(investment));

        investmentBox.getChildren().addAll(contentLabel, typesLabel, dateLabel, buttonBox);
        return investmentBox;
    }

    private void openInvestmentDetailsPage(Investment investment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/return.fxml"));
            Parent root = loader.load();

            affichageReturns controller = loader.getController();
            controller.setInvestment(investment); // passe directement l'objet ou son id selon ton besoin

            Stage stage = new Stage();
            stage.setTitle("Investment Details #" + investment.getId());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load investment details page.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    private void openReturnsPage(Investment investment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajrutern.fxml"));
            Parent root = loader.load();

            ReturnController controller = loader.getController();
            controller.setInvestmentId(investment);

            Stage stage = new Stage();
            stage.setTitle("Add Returns for Investment #" + investment.getId());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la page des retours.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void deleteInv(Investment investment) {
        investmentService.delete(investment.getId());
        loadInvestments();
    }

    private void updateInvestment(Investment investment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updatinvestment.fxml"));
            Parent root = loader.load();

            UpdateInvestmentController controller = loader.getController();
            controller.setInvestment(investment);

            Stage stage = new Stage();
            stage.setTitle("Update Investment #" + investment.getId());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de mise à jour.");
        }
    }

}
