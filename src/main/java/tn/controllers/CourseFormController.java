package tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.entities.Course;
import tn.services.CourseService;

import java.sql.SQLException;

public class CourseFormController {

    public enum Mode {
        ADD,
        EDIT
    }

    private Mode mode;
    private CourseService courseService = new CourseService();
    private Course courseToEdit;
    private FrontController parentController;

    @FXML
    private TextField nomCoursField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField dureeField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField imageField;

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setCourse(Course course) {
        this.courseToEdit = course;
        if (course != null) {
            nomCoursField.setText(course.getNomCours());
            descriptionField.setText(course.getDescription());
            dureeField.setText(course.getDuree());
            typeField.setText(course.getType());
            priceField.setText(String.valueOf(course.getPrice()));
            imageField.setText(course.getImage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            Course course = new Course();
            if (mode == Mode.EDIT && courseToEdit != null) {
                course.setId(courseToEdit.getId());
            }

            course.setNomCours(nomCoursField.getText());
            course.setDescription(descriptionField.getText());
            course.setDuree(dureeField.getText());
            course.setType(typeField.getText());
            course.setPrice(Float.parseFloat(priceField.getText()));
            course.setImage(imageField.getText());

            if (mode == Mode.EDIT) {
                courseService.updateCourse(course);
            } else {
                courseService.addCourse(course);
            }

            closeWindow();
        } catch (NumberFormatException e) {
            showError("Error saving course: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomCoursField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void setParentController(FrontController parentController) {
        this.parentController = parentController;
    }

    public void setCourseToEdit(Course course) {
        this.courseToEdit = course;
        if (course != null) {
            nomCoursField.setText(course.getNomCours());
            descriptionField.setText(course.getDescription());
            dureeField.setText(course.getDuree());
            typeField.setText(course.getType());
            priceField.setText(String.valueOf(course.getPrice()));
            imageField.setText(course.getImage());
        }
    }

}
