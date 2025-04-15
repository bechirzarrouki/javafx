package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import java.io.IOException;

public class SidebarController {

    @FXML
    private Button createInvestmentBtn;

    @FXML
    private Button investmentsBtn;

    @FXML
    private AnchorPane contentArea;

    @FXML
    public void initialize() {
        setupButtonActions();
    }

    private void setupButtonActions() {
        createInvestmentBtn.setOnAction(event -> loadPage("/investment.fxml"));
        investmentsBtn.setOnAction(event -> loadPage("/affichageinvestment.fxml"));
    }

    private void loadPage(String fxmlPath) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);

            // Force l'expansion du contenu
            AnchorPane.setTopAnchor(fxml, 0.0);
            AnchorPane.setBottomAnchor(fxml, 0.0);
            AnchorPane.setLeftAnchor(fxml, 0.0);
            AnchorPane.setRightAnchor(fxml, 0.0);

        } catch (IOException e) {
            System.err.println("Erreur de chargement de la page: " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Fichier FXML introuvable: " + fxmlPath);
        }
    }
}