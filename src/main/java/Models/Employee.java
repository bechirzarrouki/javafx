package Models;

public class Employee extends User {
    public Employee() { super(); }

    public Employee(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned) {
        super(id, username, email, password, number, profileImage, banned, null, null);
    }
    
    public Employee(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned, String resetToken, java.time.LocalDateTime resetTokenExpiry) {
        super(id, username, email, password, number, profileImage, banned, resetToken, resetTokenExpiry);
    }

    @Override
    public String getRole() {
        return "ROLE_EMPLOYEE";
    }
}
