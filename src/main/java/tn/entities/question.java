package tn.entities;

import java.util.List;

public class question {
    private String question;
    private List<String> answers;
    private String correct;
    private String userAnswer;

    public question() {}

    public question(String question, List<String> answers, String correct, String userAnswer) {
        this.question = question;
        this.answers = answers;
        this.correct = correct;
        this.userAnswer = userAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
