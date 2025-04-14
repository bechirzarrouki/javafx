package Models;

import java.time.LocalDateTime;
import java.time.LocalDate;
public class TReturn {
    private Integer id;
    private Investment investment; // Clé étrangère vers Investment
    private String description;
    private String returnType;
    private Double Rendement;
    private LocalDate deadline;
    private String status;

    public TReturn() {
    }

    public TReturn(Integer id, Investment investment, String description, String returnType, Double Rendement, LocalDate deadline, String status) {
        this.id = id;
        this.investment = investment;
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

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestmentId(Investment investment) {
        this.investment = investment;
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

    public Double getRendement() {
        return Rendement;
    }

    public void setRendement(Double taxRendement) {
        this.Rendement = taxRendement;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
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
                ", investmentId=" + investment +
                ", description='" + description + '\'' +
                ", returnType='" + returnType + '\'' +
                ", Rendement=" + Rendement +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                '}';
    }
}
