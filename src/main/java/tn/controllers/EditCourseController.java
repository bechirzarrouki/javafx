package tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.entities.Course;
import tn.services.CourseService;

import java.sql.SQLException;
import java.time.LocalDate;

public class EditCourseController {
    @FXML private TextField nomCoursField;
    @FXML private TextArea descriptionField;
    @FXML private TextField dureeField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceField;


    @FXML private Label nomCoursError;
    @FXML private Label descriptionError;
    @FXML private Label dureeError;
    @FXML private Label typeError;
    @FXML private Label priceError;

    
    private CourseService courseService;
    private Course course;
    private BackController backController;

    public void setBackController(BackController backController) {
        this.backController = backController;
    }

    public void initialize() {
        courseService = new CourseService();
        typeComboBox.getItems().addAll("free", "premium");
        
        // Add real-time validation listeners
        nomCoursField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                nomCoursError.setText("Course name is required");
            } else {
                nomCoursError.setText("");
            }
        });
        
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                descriptionError.setText("Description is required");
            } else {
                descriptionError.setText("");
            }
        });
        
        dureeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                dureeError.setText("Duration is required");
            } else if (!newValue.matches("\\d+h")) {
                dureeError.setText("Duration must be in format 'Xh' (e.g., '10h')");
            } else {
                dureeError.setText("");
            }
        });
        
        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                typeError.setText("Type is required");
            } else if (!newValue.equals("free") && !newValue.equals("premium")) {
                typeError.setText("Type must be either 'free' or 'premium'");
            } else {
                typeError.setText("");
            }
        });
        
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                priceError.setText("Price is required");
            } else if (!newValue.matches("\\d*\\.?\\d*")) {
                priceError.setText("Price must be a number");
            } else {
                priceError.setText("");
            }
        });




    }

    public void setCourse(Course course) {
        this.course = course;
        populateFields();
    }
    
    private void populateFields() {
        if (course != null) {
            nomCoursField.setText(course.getNomCours());
            descriptionField.setText(course.getDescription());
            dureeField.setText(course.getDuree());
            typeComboBox.setValue(course.getType());
            priceField.setText(String.valueOf(course.getPrice()));

        }
    }
    
    @FXML
    private void handleSave() {
        clearAllErrors();
        boolean isValid = true;
        
        if (nomCoursField.getText().trim().isEmpty()) {
            nomCoursError.setText("Course name is required");
            isValid = false;
        }
        
        if (descriptionField.getText().trim().isEmpty()) {
            descriptionError.setText("Description is required");
            isValid = false;
        }
        
        if (dureeField.getText().trim().isEmpty()) {
            dureeError.setText("Duration is required");
            isValid = false;
        } else if (!dureeField.getText().matches("\\d+h")) {
            dureeError.setText("Duration must be in format 'Xh' (e.g., '10h')");
            isValid = false;
        }
        
        if (typeComboBox.getValue() == null) {
            typeError.setText("Type is required");
            isValid = false;
        }
        
        if (priceField.getText().trim().isEmpty()) {
            priceError.setText("Price is required");
            isValid = false;
        } else if (!priceField.getText().matches("\\d*\\.?\\d*")) {
            priceError.setText("Price must be a number");
            isValid = false;
        }






        
        if (!isValid) {
            return;
        }

        try {
            course.setNomCours(nomCoursField.getText().trim());
            course.setDescription(descriptionField.getText().trim());
            course.setDuree(dureeField.getText().trim());
            course.setType(typeComboBox.getValue());
            course.setPrice(Float.parseFloat(priceField.getText().trim()));

            
            courseService.updateCourse(course);
            backController.refreshCourses();

            showAlert("Success", "Course updated successfully", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (NumberFormatException e) {
            priceError.setText("Invalid price format");
        }
    }
    
    private void clearAllErrors() {
        nomCoursError.setText("");
        descriptionError.setText("");
        dureeError.setText("");
        typeError.setText("");
        priceError.setText("");

    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) nomCoursField.getScene().getWindow();
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