package Models;

public class Investor extends User {
    public Investor() { super(); }

    public Investor(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned) {
        super(id, username, email, password, number, profileImage, banned, null, null);
    }
    
    public Investor(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned, String resetToken, java.time.LocalDateTime resetTokenExpiry) {
        super(id, username, email, password, number, profileImage, banned, resetToken, resetTokenExpiry);
    }

    @Override
    public String getRole() {
        return "ROLE_INVESTOR";
    }
}
