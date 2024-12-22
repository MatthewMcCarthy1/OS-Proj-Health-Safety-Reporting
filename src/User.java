public class User {
    private String name;
    private final String employeeId; //unique
    private final String email; //unique
    private String password;
    private String departmentName;
    private String role;

    public User(String name, String employeeId, String email, String password, String departmentName, String role) {
        this.name = name;
        this.employeeId = employeeId;
        this.email = email;
        this.password = password;
        this.departmentName = departmentName;
        this.role = role;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}
