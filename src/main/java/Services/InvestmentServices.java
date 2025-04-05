package Services;

import Models.Investment;
import Main.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvestmentServices {
    private Connection cnx;

    public InvestmentServices() {
        cnx = DatabaseConnection.instance.getCnx();
    }

    public void add(Investment investment) {
        String query = "INSERT INTO investment (content, investment_types, created_at) VALUES (?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, investment.getContent());
            stm.setString(2, String.join(",", investment.getInvestmentTypes()));
            stm.setTimestamp(3, Timestamp.valueOf(investment.getCreatedAt()));

            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                investment.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Investment investment) {
        String query = "UPDATE investment SET content = ?, investment_types = ? WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, investment.getContent());
            stm.setString(2, String.join(",", investment.getInvestmentTypes()));
            stm.setInt(3, investment.getId());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int investmentId) {
        String query = "DELETE FROM investment WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, investmentId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Investment> getAll() {
        List<Investment> investments = new ArrayList<>();
        String query = "SELECT * FROM investment";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                Investment investment = new Investment();
                investment.setId(rs.getInt("id"));
                investment.setContent(rs.getString("content"));
                String types = rs.getString("investment_types");
                investment.setInvestmentTypes(List.of(types.split(",")));
                investment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                investments.add(investment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return investments;
    }

    public Investment getById(int investmentId) {
        String query = "SELECT * FROM investment WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, investmentId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Investment investment = new Investment();
                investment.setId(rs.getInt("id"));
                investment.setContent(rs.getString("content"));
                String types = rs.getString("investment_types");
                investment.setInvestmentTypes(List.of(types.split(","))); // Convert string back to list
                investment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return investment;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
