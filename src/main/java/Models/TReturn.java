package Models;

import java.time.LocalDateTime;

public class TReturn {
    private Integer id;
    private Integer investmentId; // Clé étrangère vers Investment
    private String description;
    private String returnType;
    private Integer Rendement;
    private LocalDateTime deadline;
    private String status;

    public TReturn() {
    }

    public TReturn(Integer id, Integer investmentId, String description, String returnType, Integer Rendement, LocalDateTime deadline, String status) {
        this.id = id;
        this.investmentId = investmentId;
        this.description = description;
        this.returnType = returnType;
        this.Rendement = Rendement;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(Integer investmentId) {
        this.investmentId = investmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Integer getRendement() {
        return Rendement;
    }

    public void setRendement(Integer taxRendement) {
        this.Rendement = taxRendement;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Return{" +
                "id=" + id +
                ", investmentId=" + investmentId +
                ", description='" + description + '\'' +
                ", returnType='" + returnType + '\'' +
                ", Rendement=" + Rendement +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                '}';
    }
}
