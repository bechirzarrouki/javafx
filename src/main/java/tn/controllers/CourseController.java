package tn.controllers;

import tn.entities.Course;
import tn.services.CourseService;
import tn.utils.MyDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CourseController {
    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, Integer> idColumn;
    @FXML
    private TableColumn<Course, String> nomCoursColumn;
    @FXML
    private TableColumn<Course, String> descriptionColumn;
    @FXML
    private TableColumn<Course, String> dureeColumn;
    @FXML
    private TableColumn<Course, String> typeColumn;
    @FXML
    private TableColumn<Course, Float> priceColumn;
    @FXML
    private TableColumn<Course, String> filenameColumn;
    @FXML
    private TableColumn<Course, String> objectifsColumn;
    @FXML
    private TableColumn<Course, String> dateColumn;
    @FXML
    private TableColumn<Course, String> imageColumn;

    @FXML
    private TextField searchField;
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
    private TextField filenameField;
    @FXML
    private TextArea objectifsField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField imageField;

    private CourseService courseService;
    private ObservableList<Course> courseList;

    @FXML
    public void initialize() {
        courseService = new CourseService();
        courseList = FXCollections.observableArrayList();
        
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCoursColumn.setCellValueFactory(new PropertyValueFactory<>("nomCours"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        filenameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        objectifsColumn.setCellValueFactory(new PropertyValueFactory<>("objectifs"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Load initial data
        loadCourses();
    }

    private void loadCourses() {
        courseList.clear();
        List<Course> courses = courseService.getAllCourses();
        courseList.addAll(courses);
        courseTable.setItems(courseList);
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadCourses();
        } else {
            courseList.clear();
            List<Course> filteredCourses = courseService.searchCourses(searchText);
            courseList.addAll(filteredCourses);
        }
    }

    @FXML
    private void handleAddCourse() {
        try {
            Course course = new Course();
            course.setNomCours(nomCoursField.getText());
            course.setDescription(descriptionField.getText());
            course.setDuree(dureeField.getText());
            course.setType(typeField.getText());
            course.setPrice(Float.parseFloat(priceField.getText()));
            course.setFilename(filenameField.getText());
            course.setObjectifs(objectifsField.getText());
            course.setDate(java.time.LocalDate.parse(dateField.getText()));
            course.setImage(imageField.getText());

            courseService.addCourse(course);
            loadCourses();
            clearFields();
        } catch (Exception e) {
            showError("Error adding course: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            try {
                selectedCourse.setNomCours(nomCoursField.getText());
                selectedCourse.setDescription(descriptionField.getText());
                selectedCourse.setDuree(dureeField.getText());
                selectedCourse.setType(typeField.getText());
                selectedCourse.setPrice(Float.parseFloat(priceField.getText()));
                selectedCourse.setFilename(filenameField.getText());
                selectedCourse.setObjectifs(objectifsField.getText());
                selectedCourse.setDate(java.time.LocalDate.parse(dateField.getText()));
                selectedCourse.setImage(imageField.getText());

                courseService.updateCourse(selectedCourse);
                loadCourses();
                clearFields();
            } catch (Exception e) {
                showError("Error updating course: " + e.getMessage());
            }
        } else {
            showError("Please select a course to update");
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            try {
                courseService.deleteCourse(selectedCourse.getId());
                loadCourses();
                clearFields();
            } catch (Exception e) {
                showError("Error deleting course: " + e.getMessage());
            }
        } else {
            showError("Please select a course to delete");
        }
    }

    @FXML
    private void handleCourseSelection() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            nomCoursField.setText(selectedCourse.getNomCours());
            descriptionField.setText(selectedCourse.getDescription());
            dureeField.setText(selectedCourse.getDuree());
            typeField.setText(selectedCourse.getType());
            priceField.setText(String.valueOf(selectedCourse.getPrice()));
            filenameField.setText(selectedCourse.getFilename());
            objectifsField.setText(selectedCourse.getObjectifs());
            dateField.setText(selectedCourse.getDate().toString());
            imageField.setText(selectedCourse.getImage());
        }
    }

    private void clearFields() {
        nomCoursField.clear();
        descriptionField.clear();
        dureeField.clear();
        typeField.clear();
        priceField.clear();
        filenameField.clear();
        objectifsField.clear();
        dateField.clear();
        imageField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 