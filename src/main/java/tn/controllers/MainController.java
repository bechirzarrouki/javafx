package tn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private void openFrontOffice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/front.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Front Office - Course Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openBackOffice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/back.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Back Office - Course Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 