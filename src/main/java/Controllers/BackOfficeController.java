package Controllers;

import Models.Investment;
import Services.InvestmentServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

public class BackOfficeController {

    @FXML
    private VBox investmentContainer; // Conteneur des investissements

    private InvestmentServices investmentService = new InvestmentServices();

    @FXML
    public void initialize() {
        loadInvestments(); // Charger les investissements au démarrage
    }

    // Méthode pour charger tous les investissements
    private void loadInvestments() {
        investmentContainer.getChildren().clear(); // Effacer les anciens investissements
        List<Investment> investments = investmentService.getAll(); // Récupérer tous les investissements

        for (Investment investment : investments) {
            VBox investmentBox = createInvestmentBox(investment); // Créer la box pour chaque investissement
            investmentContainer.getChildren().add(investmentBox); // Ajouter la box à l'écran
        }
    }

    // Méthode pour créer la box d'un investissement
    private VBox createInvestmentBox(Investment investment) {
        VBox investmentBox = new VBox(10);
        investmentBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: #ffffff;");
        investmentBox.setMaxWidth(500);

        // Labels
        Label contentLabel = new Label(investment.getContent());
        contentLabel.setWrapText(true);

        Label typesLabel = new Label("Types: " + String.join(", ", investment.getInvestmentTypes()));

        Label dateLabel = new Label("Created At: " + investment.getCreatedAt().toString());
        dateLabel.setStyle("-fx-font-size: 10; -fx-font-weight: lighter;");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button deleteButton = new Button("Delete");
        Button viewReturnsButton = new Button("Add Returns");

        deleteButton.setOnAction(event -> deleteInv(investment));
        viewReturnsButton.setOnAction(event -> openReturnsPage(investment));

        buttonBox.getChildren().addAll(deleteButton);

        investmentBox.getChildren().addAll(contentLabel, typesLabel, dateLabel, buttonBox);

        // Make clicking the entire box open the returns page
        investmentBox.setOnMouseClicked(event -> {
            openInvestmentDetailsPage(investment);
        });

        // Optional: hover effect for better UX
        investmentBox.setOnMouseEntered(e -> investmentBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: #f5f5f5;"));
        investmentBox.setOnMouseExited(e -> investmentBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: #ffffff;"));

        return investmentBox;
    }

    private void openInvestmentDetailsPage(Investment investment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Adminreturn.fxml"));
            Parent root = loader.load();

            AdminaffichageReturns controller = loader.getController();
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
    private void openReturnsPage(Investment investmentId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajrutern.fxml"));
            Parent root = loader.load();

            ReturnController controller = loader.getController();
            controller.setInvestmentId(investmentId);

            Stage stage = new Stage();
            stage.setTitle("Add Returns for Investment #" + investmentId);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Optional: show an alert if you want
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load returns page.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void deleteInv(Investment investment) {
        investmentService.delete(investment.getId());
        loadInvestments();
    }
}
