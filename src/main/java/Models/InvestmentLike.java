package Models;

public class InvestmentLike {
    private Integer userId; // Clé étrangère vers User
    private Integer investmentId; // Clé étrangère vers Investment

    public InvestmentLike() {
    }

    public InvestmentLike(Integer userId, Integer investmentId) {
        this.userId = userId;
        this.investmentId = investmentId;
    }

    // Getters et Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(Integer investmentId) {
        this.investmentId = investmentId;
    }

    @Override
    public String toString() {
        return "InvestmentLike{" +
                "userId=" + userId +
                ", investmentId=" + investmentId +
                '}';
    }
}
