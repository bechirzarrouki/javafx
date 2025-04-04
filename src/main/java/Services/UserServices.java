package Services;

import Main.DatabaseConnection;
import Models.User;
import Services.InterfaceServices;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices implements InterfaceServices<User> {

    private final Connection cnx;

    public UserServices() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(User user) {
        String query = "INSERT INTO user (username, email, password, number, profile_image, banned) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, user.getUsername());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword());
            stm.setInt(4, user.getNumber() != null ? user.getNumber() : 0);
            stm.setString(5, user.getProfileImage());
            stm.setBoolean(6, user.isBanned());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void modify(User user) {
        String query = "UPDATE user SET username=?, email=?, password=?, number=?, profileImage=?, role=?, banned=? WHERE id=?";

        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, user.getUsername());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword());
            stm.setInt(4, user.getNumber() != null ? user.getNumber() : 0);
            stm.setString(5, user.getProfileImage());
            stm.setString(6, user.getRole());
            stm.setBoolean(7, user.isBanned());
            stm.setInt(8, user.getId());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> afficher() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setNumber(rs.getInt("number"));
                user.setProfileImage(rs.getString("profileImage"));
                user.setRole(rs.getString("role"));
                user.setBanned(rs.getBoolean("banned"));

                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }
}
