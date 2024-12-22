import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SharedObject {
    //LinkedLists to store users and reports
    private LinkedList<User> users;
    private LinkedList<Report> reports;

    //sets for checking unique email and employee ids
    private Set<String> emailSet;
    private Set<String> employeeIdSet;

    public SharedObject() {
        //initialising
        users = new LinkedList<>();
        reports = new LinkedList<>();
        emailSet = new HashSet<>();
        employeeIdSet = new HashSet<>();

        //load users and reports from specified files
        loadUsersFromFile("users.txt");
        loadReportsFromFile("reports.txt");
    }

    //add a user if the email and employee id provided are unique
    public synchronized boolean addUser(User user) {
        //check if the email and employee id are unique
        if (!emailSet.contains(user.getEmail()) && !employeeIdSet.contains(user.getEmployeeId())) {
            users.add(user); //add user to list
            emailSet.add(user.getEmail()); //add email to set
            employeeIdSet.add(user.getEmployeeId()); //add employeeid to set
            //save the user to users.txt
            saveUsersToFile("users.txt");
            return true;
        }
        return false; //user not added due to duplicate email or employee id
    }

    //authenticate user input (check if email and password entered match any existing user.)
    public synchronized boolean authenticateUser(String email, String password) {
        for (User user : users) {
            //check if the email and password match
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false; //email and password DONT match
    }

    //saving the user to a file
    private synchronized void saveUsersToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (User user : users) {
                //format user data as a string
                String userData = String.join(" - ",
                        user.getName(),
                        user.getEmployeeId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getDepartmentName(),
                        user.getRole());
                writer.write(userData);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

    //load users from a file
    private synchronized void loadUsersFromFile(String fileName) {
        //check if the file exists or empty, skip if either are true (this just saves having to load something that may not exist)
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //split the line into user fields
                String[] userFields = line.split(" - ");
                if (userFields.length == 6) {
                    //create a new user and add to the list and sets
                    User user = new User(userFields[0], userFields[1], userFields[2], userFields[3],
                            userFields[4], userFields[5]);
                    addUser(user);
                } else {
                    System.err.println("Invalid user data format in file: " + fileName);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users from file: " + e.getMessage());
        }
    }

    //add the report to the list and save it to reports.txt
    public synchronized void addReport(Report report) {
        reports.add(report);
        saveReportsToFile("reports.txt");
    }

    //save the report to a file
    private synchronized void saveReportsToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT' yyyy");
            for (Report report : reports) {
                //format the report data as a strig
                String reportData = String.join(" - ",
                        report.getReportType().toString(),
                        report.getReportId(),
                        report.getDate().format(outputFormatter),
                        report.getEmployeeId(),
                        report.getStatus().toString(),
                        report.getAssignedEmployeeId());
                writer.write(reportData);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving reports to file: " + e.getMessage());
        }
    }

    //load the reports
    private synchronized void loadReportsFromFile(String fileName) {
        //check if the file exists or empty, skip if either are true
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT' yyyy");
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //split lin e into report fields
                String[] reportFields = line.trim().split(" - ");
                if (reportFields.length == 6) {
                    //Parse the date string directly into LocalDateTime
                    LocalDateTime localDateTime = LocalDateTime.parse(reportFields[2], dateFormatter);

                    //Create a new Report using LocalDateTime
                    Report report = new Report(
                            Report.ReportType.valueOf(reportFields[0]),
                            localDateTime,
                            reportFields[1],
                            reportFields[3],
                            reportFields[5]
                    );
                    report.setStatus(Report.Status.valueOf(reportFields[4]));
                    report.setAssignedEmployeeId(reportFields[5]);
                    addReport(report); // Add report to the list
                } else {
                    System.err.println("Invalid report data format in file: " + fileName);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading reports from file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing report data: " + e.getMessage());
        }
    }

    //update report
    public synchronized boolean updateReport(Report report) {
        for (int i = 0; i < reports.size(); i++) {
            //find the report by id
            if (reports.get(i).getReportId().equals(report.getReportId())) {
                reports.set(i, report);
                saveReportsToFile("reports.txt");
                return true;
            }
        }
        return false; //report not found
    }

    public synchronized boolean updatePassword(String email, String newPassword) {
        for (User user : users) {
            //find the user by email and update their password
            if (user.getEmail().equals(email)) {
                user.setPassword(newPassword);
                saveUsersToFile("users.txt");
                return true; //password updated successfully
            }
        }
        return false; //user not found
    }

    public synchronized LinkedList<Report> getAllReports() {
        return new LinkedList<>(reports); //Return a copy of the list of reports
    }

    public synchronized LinkedList<User> getAllUsers() {
        return new LinkedList<>(users); //Return a copy of the list of users
    }
}
