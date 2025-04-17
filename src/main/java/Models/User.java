package Models;

public abstract class User {
    protected Integer id;
    protected String username;
    protected String email;
    protected String password;
    protected Integer number;
    protected String profileImage;
    protected Boolean banned = false;
    protected String resetToken;
    protected java.time.LocalDateTime resetTokenExpiry;

    public User() {}

    public User(Integer id, String username, String email, String password, Integer number, String profileImage, Boolean banned, String resetToken, java.time.LocalDateTime resetTokenExpiry) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.number = number;
        this.profileImage = profileImage;
        this.banned = banned;
        this.resetToken = resetToken;
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public abstract String getRole();

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Boolean isBanned() { return banned; }
    public void setBanned(Boolean banned) { this.banned = banned; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public java.time.LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(java.time.LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", number=" + number +
                ", profileImage='" + profileImage + '\'' +
                ", role='" + getRole() + '\'' +
                ", banned=" + banned +
                ", resetToken='" + resetToken + '\'' +
                ", resetTokenExpiry=" + resetTokenExpiry +
                '}';
    }
}
