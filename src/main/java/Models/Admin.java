package Models;

public class Admin extends User {
    public Admin() { super(); }

    public Admin(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned) {
        super(id, username, email, password, number, profileImage, banned, null, null);
    }
    
    public Admin(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned, String resetToken, java.time.LocalDateTime resetTokenExpiry) {
        super(id, username, email, password, number, profileImage, banned, resetToken, resetTokenExpiry);
    }

    @Override
    public String getRole() {
        return "ROLE_ADMIN";
    }
}
