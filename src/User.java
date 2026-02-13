import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String name;
    private final String employeeId; //unique
    private final String email; //unique
    private String password;
    private String departmentName;
    private String role;

    public User(String name, String employeeId, String email, String password, String departmentName, String role) {
        this(name, employeeId, email, password, departmentName, role, false);
    }

    //internal constructor that can accept already hashed passwords (for loading from file)
    public User(String name, String employeeId, String email, String password, String departmentName, String role, boolean isAlreadyHashed) {
        this.name = name;
        this.employeeId = employeeId;
        this.email = email;
        this.password = isAlreadyHashed ? password : hashPassword(password);
        this.departmentName = departmentName;
        this.role = role;
    }

    //hash password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
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
        this.password = hashPassword(password);
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
                ", password='[HASHED]'" +
                ", departmentName='" + departmentName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}
