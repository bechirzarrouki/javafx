package Models;

import java.time.LocalDateTime;
import java.util.List;

public class Investment {
    private Integer id;
    private String content;
    private List<String> investmentTypes;
    private User user;
    private LocalDateTime createdAt;

    public Investment() {
        this.createdAt = LocalDateTime.now(); // Date automatique à la création
    }

    public Investment(Integer id, String content, List<String> investmentTypes) {
        this.id = id;
        this.content = content;
        this.investmentTypes = investmentTypes;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public User getUser(){
        return this.user;
    }
    public void setUser(User user){
        this.user = user;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getInvestmentTypes() {
        return investmentTypes;
    }

    public void setInvestmentTypes(List<String> investmentTypes) {
        this.investmentTypes = investmentTypes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter for createdAt
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Investment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", investmentTypes=" + investmentTypes +
                ", createdAt=" + createdAt +
                '}';
    }
}
