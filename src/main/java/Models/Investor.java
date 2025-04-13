package Models;

public class Investor extends User {
    public Investor() { super(); }

    public Investor(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned) {
        super(id, username, email, password, number, profileImage, banned);
    }

    @Override
    public String getRole() {
        return "ROLE_INVESTOR";
    }
}
