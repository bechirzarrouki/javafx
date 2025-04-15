package tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.entities.quiz;
import tn.entities.question;
import tn.services.quizservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TakeQuizController {
    @FXML private VBox quizContainer;
    @FXML private Label titleLabel;
    @FXML private VBox questionsContainer;
    @FXML private Button submitButton;
    @FXML private Label scoreLabel;

    private quizservice quizService;
    private quiz currentQuiz;
    private List<ToggleGroup> answerGroups;
    private int score;

    public void initialize() {
        quizService = new quizservice();
        answerGroups = new ArrayList<>();
    }

    public void setQuiz(quiz quiz) {
        this.currentQuiz = quiz;
        if (quiz != null) {
            titleLabel.setText(quiz.getTitle());
            displayQuestions();
        }
    }

    private void displayQuestions() {
        questionsContainer.getChildren().clear();
        answerGroups.clear();
        score = 0;

        for (question q : currentQuiz.getQuestions()) {
            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 5;");

            Label questionLabel = new Label(q.getQuestion());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            ToggleGroup answerGroup = new ToggleGroup();
            answerGroups.add(answerGroup);

            VBox answersBox = new VBox(5);
            for (String answer : q.getAnswers()) {
                RadioButton radioButton = new RadioButton(answer);
                radioButton.setToggleGroup(answerGroup);
                radioButton.setUserData(answer);
                answersBox.getChildren().add(radioButton);
            }

            questionBox.getChildren().addAll(questionLabel, answersBox);
            questionsContainer.getChildren().add(questionBox);
        }

        submitButton.setOnAction(e -> calculateScore());
    }

    private void calculateScore() {
        score = 0;
        int totalQuestions = currentQuiz.getQuestions().size();

        for (int i = 0; i < totalQuestions; i++) {
            question q = currentQuiz.getQuestions().get(i);
            ToggleGroup group = answerGroups.get(i);
            RadioButton selectedButton = (RadioButton) group.getSelectedToggle();

            if (selectedButton != null) {
                String selectedAnswer = (String) selectedButton.getUserData();
                if (selectedAnswer.equals(q.getCorrect())) {
                    score++;
                }
            }
        }

        double percentage = (double) score / totalQuestions * 100;
        scoreLabel.setText(String.format("Your score: %d/%d (%.1f%%)", score, totalQuestions, percentage));
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #4CAF50;");

        // Disable the submit button after submission
        submitButton.setDisable(true);

        // Disable all radio buttons
        for (ToggleGroup group : answerGroups) {
            for (Toggle toggle : group.getToggles()) {
                RadioButton radioButton = (RadioButton) toggle;
                radioButton.setDisable(true); // Disable individual radio buttons
            }
        }

        // Show the results
        showAlert("Quiz Results",
                String.format("You scored %d out of %d questions (%.1f%%)",
                        score, totalQuestions, percentage),
                Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
