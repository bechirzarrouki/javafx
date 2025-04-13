package Models;

public class Employee extends User {
    public Employee() { super(); }

    public Employee(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned) {
        super(id, username, email, password, number, profileImage, banned);
    }

    @Override
    public String getRole() {
        return "ROLE_EMPLOYEE";
    }
}
