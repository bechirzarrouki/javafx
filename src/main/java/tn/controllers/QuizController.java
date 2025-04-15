package tn.controllers;
import javafx.scene.layout.HBox;
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
import java.util.function.Consumer;

public class QuizController {
    @FXML private VBox quizContainer;
    @FXML private TextField titleField;
    @FXML private VBox questionsContainer;
    @FXML private Button addQuestionButton;
    @FXML private Button saveQuizButton;
    @FXML private Button cancelButton;
    @FXML private Label titleErrorLabel;
    @FXML private VBox questionsErrorContainer;

    private quizservice quizService;
    private int courseId;
    private quiz currentQuiz;
    private List<question> questions;
    private boolean validationMode;
    private Consumer<Void> onQuizSaved;

    public void initialize() {
        quizService = new quizservice();
        questions = new ArrayList<>();
        setupQuestionForm();
        setupValidation();
    }

    private void setupValidation() {
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTitle();
        });
    }

    private void validateTitle() {
        if (validationMode) {
            if (titleField.getText().trim().isEmpty()) {
                titleErrorLabel.setText("Quiz title is required");
                titleErrorLabel.setVisible(true);
                return;
            }
            titleErrorLabel.setVisible(false);
        }
    }

    public void setValidationMode(boolean validationMode) {
        this.validationMode = validationMode;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setQuiz(quiz quiz) {
        this.currentQuiz = quiz;
        if (quiz != null) {
            titleField.setText(quiz.getTitle());
            questions = quiz.getQuestions();
            displayQuestions();
        }
    }

    public void setOnQuizSaved(Consumer<Void> callback) {
        this.onQuizSaved = callback;
    }

    private void setupQuestionForm() {
        addQuestionButton.setOnAction(e -> addQuestionForm());
        saveQuizButton.setOnAction(e -> handleSaveQuiz());
        cancelButton.setOnAction(e -> closeWindow());
    }

    private void addQuestionForm() {
        VBox questionBox = new VBox(10);
        questionBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        Label questionLabel = new Label("Question " + (questionsContainer.getChildren().size() + 1));
        questionLabel.setStyle("-fx-font-weight: bold;");

        TextField questionField = new TextField();
        questionField.setPromptText("Enter question text");
        questionField.setStyle("-fx-padding: 5;");

        Label questionErrorLabel = new Label();
        questionErrorLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 12px;");
        questionErrorLabel.setVisible(false);

        VBox answersBox = new VBox(5);
        Button addAnswerButton = new Button("Add Answer");
        addAnswerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        addAnswerButton.setOnAction(e -> {
            HBox answerBox = new HBox(5);
            RadioButton radioButton = new RadioButton();
            TextField answerField = new TextField();
            answerField.setPromptText("Enter answer text");
            answerField.setStyle("-fx-padding: 5;");

            Label answerErrorLabel = new Label();
            answerErrorLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 12px;");
            answerErrorLabel.setVisible(false);

            answerField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (validationMode) {
                    if (newValue.trim().isEmpty()) {
                        answerErrorLabel.setText("Answer text is required");
                        answerErrorLabel.setVisible(true);
                    } else {
                        answerErrorLabel.setVisible(false);
                    }
                }
            });

            answerBox.getChildren().addAll(radioButton, answerField, answerErrorLabel);
            answersBox.getChildren().add(answerBox);
        });

        questionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (validationMode) {
                if (newValue.trim().isEmpty()) {
                    questionErrorLabel.setText("Question text is required");
                    questionErrorLabel.setVisible(true);
                } else {
                    questionErrorLabel.setVisible(false);
                }
            }
        });

        questionBox.getChildren().addAll(questionLabel, questionField, questionErrorLabel, answersBox, addAnswerButton);
        questionsContainer.getChildren().add(questionBox);
    }

    private void displayQuestions() {
        questionsContainer.getChildren().clear();
        for (question q : questions) {
            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 5;");

            TextField questionField = new TextField(q.getQuestion());
            
            VBox answersBox = new VBox(5);
            List<String> answers = q.getAnswers();
            for (int i = 0; i < answers.size(); i++) {
                HBox answerBox = new HBox(5);
                RadioButton radioButton = new RadioButton();
                radioButton.setSelected(answers.get(i).equals(q.getCorrect()));
                TextField answerField = new TextField(answers.get(i));
                answerBox.getChildren().addAll(radioButton, answerField);
                answersBox.getChildren().add(answerBox);
            }

            Button removeButton = new Button("Remove Question");
            removeButton.setOnAction(e -> questionsContainer.getChildren().remove(questionBox));

            questionBox.getChildren().addAll(
                new Label("Question:"),
                questionField,
                new Label("Answers:"),
                answersBox,
                removeButton
            );

            questionsContainer.getChildren().add(questionBox);
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate title
        if (titleField.getText().trim().isEmpty()) {
            titleErrorLabel.setText("Quiz title is required");
            titleErrorLabel.setVisible(true);
            isValid = false;
        }

        // Validate questions
        if (questionsContainer.getChildren().isEmpty()) {
            showAlert("Error", "Please add at least one question", Alert.AlertType.ERROR);
            return false;
        }

        for (javafx.scene.Node node : questionsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox questionBox = (VBox) node;
                TextField questionField = (TextField) questionBox.getChildren().get(1);
                Label questionErrorLabel = (Label) questionBox.getChildren().get(2);
                VBox answersBox = (VBox) questionBox.getChildren().get(3);

                if (questionField.getText().trim().isEmpty()) {
                    questionErrorLabel.setText("Question text is required");
                    questionErrorLabel.setVisible(true);
                    isValid = false;
                }

                if (answersBox.getChildren().isEmpty()) {
                    showAlert("Error", "Please add at least one answer for each question", Alert.AlertType.ERROR);
                    return false;
                }

                boolean hasCorrectAnswer = false;
                for (javafx.scene.Node answerNode : answersBox.getChildren()) {
                    if (answerNode instanceof HBox) {
                        HBox answerBox = (HBox) answerNode;
                        RadioButton radioButton = (RadioButton) answerBox.getChildren().get(0);
                        TextField answerField = (TextField) answerBox.getChildren().get(1);
                        Label answerErrorLabel = (Label) answerBox.getChildren().get(2);

                        if (answerField.getText().trim().isEmpty()) {
                            answerErrorLabel.setText("Answer text is required");
                            answerErrorLabel.setVisible(true);
                            isValid = false;
                        }

                        if (radioButton.isSelected()) {
                            hasCorrectAnswer = true;
                        }
                    }
                }

                if (!hasCorrectAnswer) {
                    showAlert("Error", "Please select a correct answer for each question", Alert.AlertType.ERROR);
                    return false;
                }
            }
        }

        return isValid;
    }

    @FXML
    private void handleSaveQuiz() {
        if (!validateForm()) {
            return;
        }

        try {
            List<question> questions = new ArrayList<>();
            for (int i = 0; i < questionsContainer.getChildren().size(); i++) {
                VBox questionBox = (VBox) questionsContainer.getChildren().get(i);
                TextField questionField = (TextField) questionBox.getChildren().get(1);
                VBox answersBox = (VBox) questionBox.getChildren().get(3);

                List<String> answers = new ArrayList<>();
                String correctAnswer = null;

                for (int j = 0; j < answersBox.getChildren().size(); j++) {
                    HBox answerRow = (HBox) answersBox.getChildren().get(j);
                    TextField answerField = (TextField) answerRow.getChildren().get(0);
                    RadioButton radioButton = (RadioButton) answerRow.getChildren().get(1);

                    String answer = answerField.getText().trim();
                    if (!answer.isEmpty()) {
                        answers.add(answer);
                        if (radioButton.isSelected()) {
                            correctAnswer = answer;
                        }
                    }
                }

                if (!answers.isEmpty() && correctAnswer != null) {
                    question q = new question();
                    q.setQuestion(questionField.getText().trim());
                    q.setAnswers(answers);
                    q.setCorrect(correctAnswer);
                    questions.add(q);
                }
            }

            if (currentQuiz == null) {
                currentQuiz = new quiz();
                currentQuiz.setIdCours(courseId);
            }

            currentQuiz.setTitle(titleField.getText().trim());
            currentQuiz.setQuestions(questions);

            if (currentQuiz.getId() == 0) {
                quizService.add(currentQuiz);
            } else {
                quizService.modify(currentQuiz);
            }

            if (onQuizSaved != null) {
                onQuizSaved.accept(null);
            }

            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert("Error", "Failed to save quiz: " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    private void closeWindow() {
        Stage stage = (Stage) quizContainer.getScene().getWindow();
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