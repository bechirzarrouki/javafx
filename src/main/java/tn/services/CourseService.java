package tn.services;

import tn.entities.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseService {
    private Connection connection;

    public CourseService() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pi", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM cours";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                courses.add(mapResultSetToCourse(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course getCourseById(int id) {
        String query = "SELECT * FROM cours WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCourse(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addCourse(Course course) {
        String query = "INSERT INTO cours (nom_cours, description, duree, type, filename, objectifs, date, image, price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            setCourseParameters(statement, course);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCourse(Course course) {
        String query = "UPDATE cours SET nom_cours = ?, description = ?, duree = ?, type = ?, " +
                "filename = ?, objectifs = ?, date = ?, image = ?, price = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            setCourseParameters(statement, course);
            statement.setInt(10, course.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse(int id) {
        String query = "DELETE FROM cours WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> searchCourses(String searchText) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM cours WHERE nom_cours LIKE ? OR description LIKE ? OR type LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courses.add(mapResultSetToCourse(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<Course> filterCourses(String category, String level, String language) {
        List<Course> courses = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM cours WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            query.append(" AND type = ?");
            parameters.add(category);
        }
        if (level != null && !level.isEmpty()) {
            query.append(" AND level = ?");
            parameters.add(level);
        }
        if (language != null && !language.isEmpty()) {
            query.append(" AND language = ?");
            parameters.add(language);
        }

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courses.add(mapResultSetToCourse(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    private Course mapResultSetToCourse(ResultSet resultSet) throws SQLException {
        Course course = new Course();
        course.setId(resultSet.getInt("id"));
        course.setNomCours(resultSet.getString("nom_cours"));
        course.setDescription(resultSet.getString("description"));
        course.setDuree(resultSet.getString("duree"));
        course.setType(resultSet.getString("type"));
        course.setObjectifs(resultSet.getString("objectifs"));
        course.setPrice(resultSet.getFloat("price"));
        return course;
    }

    private void setCourseParameters(PreparedStatement statement, Course course) throws SQLException {
        statement.setString(1, course.getNomCours());
        statement.setString(2, course.getDescription());
        statement.setString(3, course.getDuree());
        statement.setString(4, course.getType());
        statement.setString(5, course.getFilename());
        statement.setString(6, course.getObjectifs());
        statement.setDate(7, Date.valueOf(course.getDate()));
        statement.setString(8, course.getImage());
        statement.setDouble(9, course.getPrice());
    }
} 