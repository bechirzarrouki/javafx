package tn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.entities.Course;
import tn.entities.quiz;
import tn.services.quizservice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CourseDetailsController {
    @FXML
    private ImageView courseImage;
    @FXML
    private Label courseName;
    @FXML
    private Label courseDescription;
    @FXML
    private Label durationLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label levelLabel;

    private Course course;
    private quizservice quizService;

    public void initialize() {
        quizService = new quizservice();
    }

    public void setCourse(Course course) {
        this.course = course;
        updateUI();
    }

    private void updateUI() {
        if (course != null) {
            // Load course image
            try {
                if (course.getImage() != null && !course.getImage().isEmpty()) {
                    try {
                        // First try to load from resources
                        Image image = new Image(getClass().getResourceAsStream("/tn/images/" + course.getImage()));
                        courseImage.setImage(image);
                    } catch (Exception e) {
                        // If that fails, try to load as a file path
                        try {
                            Image image = new Image("file:" + course.getImage());
                            courseImage.setImage(image);
                        } catch (Exception ex) {
                            // If both fail, use default image
                            loadDefaultImage();
                        }
                    }
                } else {
                    loadDefaultImage();
                }
            } catch (Exception e) {
                loadDefaultImage();
            }

            courseName.setText(course.getNomCours());
            courseDescription.setText(course.getDescription());
            durationLabel.setText(course.getDuree());
            typeLabel.setText(course.getType());
            priceLabel.setText("$" + course.getPrice());

        }
    }

    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/tn/images/course01.jpg"));
            courseImage.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("Failed to load default image: " + e.getMessage());
        }
    }

    @FXML
    private void startQuiz() {
        try {
            // Get the quiz for this course
            List<quiz> quizzes = quizService.getQuizzesByCourseId(course.getId());
            if (quizzes.isEmpty()) {
                showAlert("No Quiz Available", "There are no quizzes available for this course yet.", Alert.AlertType.INFORMATION);
                return;
            }

            // Load the take quiz view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/takeQuiz.fxml"));
            Parent root = loader.load();
            TakeQuizController controller = loader.getController();
            controller.setQuiz(quizzes.get(0)); // For now, just take the first quiz

            Stage stage = new Stage();
            stage.setTitle("Take Quiz");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load quiz: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException e) {
            showAlert("Error", "Failed to load quiz view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) courseImage.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 