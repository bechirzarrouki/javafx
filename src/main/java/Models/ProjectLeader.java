package Models;

public class ProjectLeader extends User {
    public ProjectLeader() { super(); }

    public ProjectLeader(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned) {
        super(id, username, email, password, number, profileImage, banned);
    }

    @Override
    public String getRole() {
        return "ROLE_PROJECTLEADER";
    }
}
