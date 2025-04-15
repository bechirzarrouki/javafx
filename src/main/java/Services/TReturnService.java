package Services;

import Models.TReturn;
import Main.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TReturnService {

    private final Connection cnx;

    public TReturnService() {
        cnx = DatabaseConnection.instance.getCnx();
    }

    // Ajouter un retour
    public void add(TReturn tReturn) {
        String query = "INSERT INTO return_entity (investment_id, description, type_return, tax_rendement, date_deadline, status) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stm.setInt(1, tReturn.getInvestment().getId());
            stm.setString(2, tReturn.getDescription());
            stm.setString(3, tReturn.getReturnType());
            stm.setDouble(4, tReturn.getRendement());
            stm.setDate(5, Date.valueOf(tReturn.getDeadline()));
            stm.setString(6, tReturn.getStatus());

            stm.executeUpdate();

            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                tReturn.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Modifier un retour
    public void update(TReturn tReturn) {
        String query = "UPDATE return_entity SET description = ?, type_return = ?, tax_rendement = ?, date_deadline = ?, status = ? WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, tReturn.getDescription());
            stm.setString(2, tReturn.getReturnType());
            stm.setDouble(3, tReturn.getRendement());
            stm.setDate(4, Date.valueOf(tReturn.getDeadline()));
            stm.setString(5, tReturn.getStatus());
            stm.setInt(6, tReturn.getId());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Supprimer un retour
    public void delete(int id) {
        String query = "DELETE FROM return_entity WHERE id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtenir tous les retours
    public List<TReturn> getAll() {
        List<TReturn> list = new ArrayList<>();
        String query = "SELECT * FROM return_entity";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                TReturn tReturn = mapResultSetToReturn(rs);
                list.add(tReturn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // Obtenir les retours d’un investissement spécifique
    public List<TReturn> getByInvestmentId(int investmentId) {
        List<TReturn> list = new ArrayList<>();
        String query = "SELECT * FROM return_entity WHERE investment_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, investmentId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                TReturn tReturn = mapResultSetToReturn(rs);
                list.add(tReturn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // Utilitaire de mapping
    private TReturn mapResultSetToReturn(ResultSet rs) throws SQLException {
        TReturn tReturn = new TReturn();
        tReturn.setId(rs.getInt("id"));
        tReturn.setDescription(rs.getString("description"));
        tReturn.setReturnType(rs.getString("type_return"));
        tReturn.setRendement(rs.getDouble("tax_rendement"));
        tReturn.setDeadline(rs.getDate("date_deadline").toLocalDate());
        tReturn.setStatus(rs.getString("status"));
        return tReturn;
    }
}
