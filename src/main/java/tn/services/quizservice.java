package tn.services;

import tn.entities.quiz;
import tn.entities.question;
import tn.utils.MyDataBase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class quizservice {
    private Connection cnx;
    private ObjectMapper mapper;

    public quizservice() {
        try {
            cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/pi", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mapper = new ObjectMapper();
    }

    public void add(quiz quiz) throws SQLException {
        String sql = "INSERT INTO quiz (id_cours_id, title, questions, score) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String jsonQuestions = mapper.writeValueAsString(quiz.getQuestions());
            ps.setInt(1, quiz.getIdCours());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, jsonQuestions);
            ps.setInt(4, quiz.getScore());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quiz.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Failed to add quiz: " + e.getMessage());
        }
    }

    public void modify(quiz quiz) throws SQLException {
        String sql = "UPDATE quiz SET id_cours_id=?, title=?, questions=?, score=? WHERE id=?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            String jsonQuestions = mapper.writeValueAsString(quiz.getQuestions());
            ps.setInt(1, quiz.getIdCours());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, jsonQuestions);
            ps.setInt(4, quiz.getScore());
            ps.setInt(5, quiz.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Failed to update quiz: " + e.getMessage());
        }
    }

    public List<quiz> afficher() throws SQLException {
        List<quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quiz";
        
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quiz quiz = new quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setIdCours(rs.getInt("id_cours_id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setScore(rs.getInt("score"));

                String jsonQuestions = rs.getString("questions");
                List<question> questionList = mapper.readValue(jsonQuestions, new TypeReference<List<question>>() {});
                quiz.setQuestions(questionList);

                quizzes.add(quiz);
            }
        } catch (Exception e) {
            throw new SQLException("Failed to retrieve quizzes: " + e.getMessage());
        }
        return quizzes;
    }

    public List<quiz> getQuizzesByCourseId(int courseId) throws SQLException {
        List<quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quiz WHERE id_cours_id = ?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    quiz quiz = new quiz();
                    quiz.setId(rs.getInt("id"));
                    quiz.setIdCours(rs.getInt("id_cours_id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setScore(rs.getInt("score"));

                    String jsonQuestions = rs.getString("questions");
                    List<question> questionList = mapper.readValue(jsonQuestions, new TypeReference<List<question>>() {});
                    quiz.setQuestions(questionList);

                    quizzes.add(quiz);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Failed to retrieve quizzes for course: " + e.getMessage());
        }
        return quizzes;
    }

    public quiz getQuizById(int quizId) throws SQLException {
        String sql = "SELECT * FROM quiz WHERE id = ?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    quiz quiz = new quiz();
                    quiz.setId(rs.getInt("id"));
                    quiz.setIdCours(rs.getInt("id_cours_id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setScore(rs.getInt("score"));

                    String jsonQuestions = rs.getString("questions");
                    List<question> questionList = mapper.readValue(jsonQuestions, new TypeReference<List<question>>() {});
                    quiz.setQuestions(questionList);

                    return quiz;
                }
            }
        } catch (Exception e) {
            throw new SQLException("Failed to retrieve quiz: " + e.getMessage());
        }
        return null;
    }

    public void deleteQuiz(int quizId) throws SQLException {
        String sql = "DELETE FROM quiz WHERE id = ?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to delete quiz: " + e.getMessage());
        }
    }
}

