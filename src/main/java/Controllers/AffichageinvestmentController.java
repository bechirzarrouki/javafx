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

public class AffichageinvestmentController {

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
        investmentBox.getStyleClass().add("investment-box");

        // Contenu de l'investissement
        Label contentLabel = new Label(investment.getContent());
        contentLabel.getStyleClass().add("investment-content");
        contentLabel.setWrapText(true);

        // Affichage du type d'investissement
        Label typesLabel = new Label("Types: " + String.join(", ", investment.getInvestmentTypes()));
        typesLabel.getStyleClass().add("investment-types");

        // Affichage de la date de création
        Label dateLabel = new Label("Created At: " + investment.getCreatedAt().toString());
        dateLabel.getStyleClass().add("investment-date");

        // Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("button-container");

        // Compteur de likes
        final int[] likesCount = {0};
        Label likesLabel = new Label("Likes: " + likesCount[0]);
        likesLabel.getStyleClass().add("likes-label");

        Button likeDislikeButton = new Button("Like");
        likeDislikeButton.getStyleClass().add("like-button");




        Button openMessagesButton = new Button("Open Messages");
        openMessagesButton.getStyleClass().add("action-button");

        // Gestion des événements
        likeDislikeButton.setOnAction(event -> {
            if (likeDislikeButton.getText().equals("Like")) {
                likesCount[0]++;
                likeDislikeButton.setText("Dislike");
            } else {
                likesCount[0]--;
                likeDislikeButton.setText("Like");
            }
            likesLabel.setText("Likes: " + likesCount[0]);
        });


        openMessagesButton.setOnAction(event -> openMessagesPage(investment));

        buttonBox.getChildren().addAll(likeDislikeButton, likesLabel, openMessagesButton);
        investmentBox.getChildren().addAll(contentLabel, typesLabel, dateLabel, buttonBox);

        investmentBox.setOnMouseClicked(event -> openInvestmentDetailsPage(investment));

        return investmentBox;
    }
    private void openInvestmentDetailsPage(Investment investment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Userreturn.fxml"));
            Parent root = loader.load();

            UseraffichageReturns controller = loader.getController();
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


    // Ouvrir la page pour ajouter des returns
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load returns page.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Ouvrir la page des messages
    private void openMessagesPage(Investment investmentId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/message.fxml"));
            Parent root = loader.load();

            MessageController controller = loader.getController();
            controller.setInvestmentId(investmentId);

            Stage stage = new Stage();
            stage.setTitle("Messages for Investment #" + investmentId);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load messages page.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
