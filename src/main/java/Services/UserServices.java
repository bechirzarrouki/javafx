package Services;

import Main.DatabaseConnection;
import Models.User;
import Services.InterfaceServices;
import Models.Admin;
import Models.Employee;
import Models.ProjectLeader;
import Models.Investor;



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
        String query = "INSERT INTO user (username, email, password, number, profile_image, role, banned, reset_token, reset_token_expiry) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, user.getUsername());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword());
            stm.setInt(4, user.getNumber() != null ? user.getNumber() : 0);
            stm.setString(5, user.getProfileImage());
            stm.setString(6, user.getRole());
            stm.setBoolean(7, user.isBanned());
            stm.setString(8, user.getResetToken());
            if (user.getResetTokenExpiry() != null) {
                stm.setTimestamp(9, java.sql.Timestamp.valueOf(user.getResetTokenExpiry()));
            } else {
                stm.setNull(9, java.sql.Types.TIMESTAMP);
            }

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void modify(User user) {
        String query = "UPDATE user SET username=?, email=?, password=?, number=?, profileImage=?, role=?, banned=?, reset_token=?, reset_token_expiry=? WHERE id=?";

        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, user.getUsername());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword());
            stm.setInt(4, user.getNumber() != null ? user.getNumber() : 0);
            stm.setString(5, user.getProfileImage());
            stm.setString(6, user.getRole());
            stm.setBoolean(7, user.isBanned());
            stm.setString(8, user.getResetToken());
            if (user.getResetTokenExpiry() != null) {
                stm.setTimestamp(9, java.sql.Timestamp.valueOf(user.getResetTokenExpiry()));
            } else {
                stm.setNull(9, java.sql.Types.TIMESTAMP);
            }
            stm.setInt(10, user.getId());

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
                String role = rs.getString("role");
                User user;

                switch (role) {
                    case "ROLE_ADMIN":
                        user = new Admin();
                        break;
                    case "ROLE_EMPLOYEE":
                        user = new Employee();
                        break;
                    case "ROLE_PROJECT_LEADER":
                        user = new ProjectLeader();
                        break;
                    case "ROLE_INVESTOR":
                        user = new Investor();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown role: " + role);
                }

                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setNumber(rs.getInt("number"));
                user.setProfileImage(rs.getString("profileImage"));
                user.setBanned(rs.getBoolean("banned"));
                user.setResetToken(rs.getString("reset_token"));
                java.sql.Timestamp resetTokenExpiry = rs.getTimestamp("reset_token_expiry");
                if (resetTokenExpiry != null) {
                    user.setResetTokenExpiry(resetTokenExpiry.toLocalDateTime());
                }

                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

}
