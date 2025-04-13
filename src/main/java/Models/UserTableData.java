package Models;

public class UserTableData {
    private int id;
    private String username;
    private String email;
    private Integer number;
    private String role;
    private boolean banned;

    public UserTableData() {
    }

    public UserTableData(int id, String username, String email, Integer number, String role, boolean banned) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.number = number;
        this.role = role;
        this.banned = banned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;}

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
