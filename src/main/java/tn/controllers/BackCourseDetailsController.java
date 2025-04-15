package tn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.entities.Course;
import tn.entities.quiz;
import tn.services.CourseService;
import tn.services.quizservice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BackCourseDetailsController {
    @FXML private ImageView courseImage;
    @FXML private Label courseName;
    @FXML private Label courseDescription;
    @FXML private Label durationLabel;
    @FXML private Label typeLabel;
    @FXML private Label priceLabel;
    @FXML private TableView<quiz> quizzesTable;
    @FXML private TableColumn<quiz, String> titleColumn;
    @FXML private TableColumn<quiz, String> questionsColumn;

    private CourseService courseService;
    private quizservice quizService;
    private Course currentCourse;

    public void initialize() {
        courseService = new CourseService();
        quizService = new quizservice();
        
        // Set up table columns
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        questionsColumn.setCellValueFactory(cellData -> {
            int questionCount = cellData.getValue().getQuestions().size();
            return new javafx.beans.property.SimpleStringProperty(questionCount + " questions");
        });
    }

    public void setCourse(Course course) {
        this.currentCourse = course;
        updateUI();
        refreshQuizzes();
    }

    private void refreshQuizzes() {
        if (currentCourse != null) {
            try {
                List<quiz> quizzes = quizService.getQuizzesByCourseId(currentCourse.getId());
                quizzesTable.getItems().setAll(quizzes);
            } catch (SQLException e) {
                showAlert("Error", "Failed to load quizzes: " + e.getMessage());
            }
        }
    }

    private void updateUI() {
        if (currentCourse != null) {
            courseName.setText(currentCourse.getNomCours());
            courseDescription.setText(currentCourse.getDescription());
            durationLabel.setText(currentCourse.getDuree());
            typeLabel.setText(currentCourse.getType());
            priceLabel.setText(String.format("%.2f", currentCourse.getPrice()));

            try {
                // Load course image
                if (currentCourse.getImage() != null && !currentCourse.getImage().isEmpty()) {
                    Image image = new Image(getClass().getResourceAsStream(currentCourse.getImage()));
                    courseImage.setImage(image);
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to load course image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddQuiz() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/quizForm.fxml"));
            Parent root = loader.load();
            QuizController controller = loader.getController();
            controller.setCourseId(currentCourse.getId());
            controller.setOnQuizSaved(v -> refreshQuizzes());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Quiz");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open quiz form: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditQuiz() {
        quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/quizForm.fxml"));
                Parent root = loader.load();
                QuizController controller = loader.getController();
                controller.setQuiz(selectedQuiz);
                controller.setOnQuizSaved(v -> refreshQuizzes());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit Quiz");
                stage.show();
            } catch (IOException e) {
                showAlert("Error", "Failed to open quiz form: " + e.getMessage());
            }
        } else {
            showAlert("Warning", "Please select a quiz to edit");
        }
    }

    @FXML
    private void handleDeleteQuiz() {
        quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            try {
                quizService.deleteQuiz(selectedQuiz.getId());
                refreshQuizzes();
                showAlert("Success", "Quiz deleted successfully");
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete quiz: " + e.getMessage());
            }
        } else {
            showAlert("Warning", "Please select a quiz to delete");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/views/back.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) courseName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate back: " + e.getMessage());
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