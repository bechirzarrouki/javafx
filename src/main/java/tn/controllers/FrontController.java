package tn.controllers;
import javafx.scene.layout.HBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tn.entities.Course;
import tn.services.CourseService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FrontController {
    @FXML
    private FlowPane coursesGrid;

    private CourseService courseService;

    public void initialize() {

        courseService = new CourseService();
        loadCourses();
    }

    public void loadCourses() {
          List<Course> courses = courseService.getAllCourses();
            coursesGrid.getChildren().clear();

            for (Course course : courses) {
                VBox card = createCourseCard(course);
                coursesGrid.getChildren().add(card);
            }

    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(300);
        card.setPrefHeight(400);

        // Course Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/tn/images/" + course.getImage()));
            imageView.setImage(image);
        } catch (Exception e) {
            // Use default image if course image not found
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/tn/images/course01.jpg"));
                imageView.setImage(defaultImage);
            } catch (Exception ex) {
                System.out.println("Failed to load default image: " + ex.getMessage());
            }
        }
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Course Name
        Label nameLabel = new Label(course.getNomCours());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web("#53348D"));

        // Description
        Label descLabel = new Label(course.getDescription());
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(270);

        // Duration
        Label durationLabel = new Label("Duration: " + course.getDuree());
        durationLabel.setTextFill(Color.web("#8B19A4"));

        // Type
        Label typeLabel = new Label("Type: " + course.getType());
        typeLabel.setTextFill(Color.web("#8B19A4"));

        // Price
        Label priceLabel = new Label("Price: $" + course.getPrice());
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        priceLabel.setTextFill(Color.web("#53348D"));

        // Action Buttons
        HBox buttonBox = new HBox(10);

        Button detailsButton = new Button("Details");
        detailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        detailsButton.setOnAction(e -> showCourseDetails(course));

        buttonBox.getChildren().addAll(detailsButton);

        card.getChildren().addAll(imageView, nameLabel, descLabel, durationLabel, typeLabel, priceLabel, buttonBox);
        return card;
    }

    private void showCourseDetails(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/courseDetails.fxml"));
            Parent root = loader.load();
            CourseDetailsController controller = loader.getController();
            controller.setCourse(course);

            Stage stage = new Stage();
            stage.setTitle("Course Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load course details: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 