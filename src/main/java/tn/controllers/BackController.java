package tn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.entities.Course;
import tn.services.CourseService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BackController {
    @FXML
    private FlowPane coursesGrid;

    private CourseService courseService;

    public void initialize() {

            courseService = new CourseService();
            loadCourses();

    }

    private void loadCourses() {

            List<Course> courses = courseService.getAllCourses();
            coursesGrid.getChildren().clear();
            
            for (Course course : courses) {
                VBox card = createCourseCard(course);
                coursesGrid.getChildren().add(card);
            }
    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(300);

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
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Course Description
        Label descriptionLabel = new Label(course.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(270);

        // Course Details
        HBox detailsBox = new HBox(10);
        Label durationLabel = new Label("Duration: " + course.getDuree());
        Label typeLabel = new Label("Type: " + course.getType());
        Label priceLabel = new Label("Price: $" + course.getPrice());
        detailsBox.getChildren().addAll(durationLabel, typeLabel, priceLabel);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button detailsButton = new Button("Details");
        detailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        detailsButton.setOnAction(e -> showCourseDetails(course));

        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        editButton.setOnAction(e -> showEditCourseForm(course));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteCourse(course.getId()));

        buttonBox.getChildren().addAll(detailsButton, editButton, deleteButton);

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, detailsBox, buttonBox);
        return card;
    }

    @FXML
    private void showAddCourseForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/addCourse.fxml"));
            Parent root = loader.load();
            AddCourseController controller = loader.getController();
            controller.setBackController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Add New Course");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load add course form: " + e.getMessage());
        }
    }

    private void showEditCourseForm(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/editCourse.fxml"));
            Parent root = loader.load();
            EditCourseController controller = loader.getController();
            controller.setCourse(course);
            controller.setBackController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Course");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load edit course form: " + e.getMessage());
        }
    }

    private void deleteCourse(int courseId) {

            courseService.deleteCourse(courseId);
            loadCourses(); // Refresh the courses grid
            showAlert("Success", "Course deleted successfully");

    }

    public void refreshCourses() {
        loadCourses();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadDefaultImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/tn/images/default-course.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("Failed to load default image: " + e.getMessage());
        }
    }

    private void showCourseDetails(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/backCourseDetails.fxml"));
            Parent root = loader.load();
            BackCourseDetailsController controller = loader.getController();
            controller.setCourse(course);

            Stage stage = new Stage();
            stage.setTitle("Course Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load course details: " + e.getMessage());
        }
    }
} 