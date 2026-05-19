package movieRental;

public class Staff {
    private int staffId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;
    private String dateCreated;
    private String lastUpdated;

    // Constructor
    public Staff(int staffId, String firstName, String lastName, String username,
                 String password, String role, String dateCreated, String lastUpdated) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
    }

    // Getters and setters
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDateCreated() { return dateCreated; }
    public String getLastUpdated() { return lastUpdated; }
}
