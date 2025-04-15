package tn.entities;

import java.util.List;

public class quiz {
    private int id;
    private int idCours;
    private String title;
    private List<question> questions;
    private int score;

    public quiz() {}

    public quiz(int id, int idCours, String title, List<question> questions, int score) {
        this.id = id;
        this.idCours = idCours;
        this.title = title;
        this.questions = questions;
        this.score = score;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCours() {
        return idCours;
    }

    public void setIdCours(int idCours) {
        this.idCours = idCours;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<question> questions) {
        this.questions = questions;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
