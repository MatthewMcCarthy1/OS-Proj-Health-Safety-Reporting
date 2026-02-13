import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class MenuHandler {

    private ServerThread serverThread;
    private SharedObject sharedObject;

    //constructor initializing menuhandler with serverthread and sharedobject
    public MenuHandler(ServerThread serverThread, SharedObject sharedObject) {
        this.serverThread = serverThread;
        this.sharedObject = sharedObject;
    }

    //handle menu interactions
    public void menuHandler() throws IOException, ClassNotFoundException {
        while (true) {
            try {
                //if the current user is not logged in get them to sign in, register or exit
                if (serverThread.getCurrentUser() == null) {
                    sendMessage("Welcome to the Health and Safety Reporting System");
                    sendMessage("Please enter 1 to Sign in, 2 to Register an account or 0 to exit.");
                    sendPrompt("Enter your choice:");

                    String choice = receiveInput();
                    switch (choice) {
                        case "0":
                            sendMessage("Exiting Application.");
                            return;
                        case "1":
                            sendMessage("Login");
                            handleSignIn();
                            break;
                        case "2":
                            sendMessage("Register");
                            handleRegistration();
                            break;
                        default:
                            sendMessage("Invalid choice. Please try again.");
                    }
                } else {
                    //if the user is logged in then they can access the menu
                    sendMessage("\nUser Menu:");
                    sendMessage("3. Create a health and safety report");
                    sendMessage("4. Retrieve all registered accident reports");
                    sendMessage("5. Assign a health & safety report");
                    sendMessage("6. View all health and safety reports assigned to you");
                    sendMessage("7. Update your password");
                    sendMessage("0. Logout");
                    sendPrompt("Enter your choice:");

                    String choice = receiveInput();
                    switch (choice) {
                        case "3":
                            sendMessage("Create Report");
                            createReport();
                            break;
                        case "4":
                            sendMessage("Show All Reports");
                            retrieveAccidentReports();
                            break;
                        case "5":
                            sendMessage("Assign Employee");
                            assignReport();
                            break;
                        case "6":
                            sendMessage("Show Assigned Reports");
                            viewAssignedReports();
                            break;
                        case "7":
                            sendMessage("Update Password");
                            updatePassword();
                            break;
                        case "0":
                            sendMessage("Logged out successfully.");
                            serverThread.setCurrentUser(null);
                            break;
                        default:
                            sendMessage("Invalid choice. Please try again.");
                    }
                }
            } catch (IOException e) {
                sendMessage("An error occurred, please try again.");
            }
        }
    }


    //SIGN IN
    private void handleSignIn() throws IOException, ClassNotFoundException {
        sendPrompt("Enter email: ");
        String email = receiveInput();

        sendPrompt("Enter password: ");
        String password = receiveInput();

        if (sharedObject.authenticateUser(email, password)) {
            User authenticatedUser = null;
            for (User u : sharedObject.getAllUsers()) {
                if (u.getEmail().equals(email)) {
                    authenticatedUser = u; // Set currentUser if email matches
                    break; // Exit the loop once the user is found
                }
            }
            serverThread.setCurrentUser(authenticatedUser);
            sendMessage("Login Successful.");
        } else {
            serverThread.sendMessage("Invalid login. Please try again.");
        }
    }

    //REGISTER
    private void handleRegistration() throws IOException, ClassNotFoundException {

        sendPrompt("Enter name:");
        String name = receiveInput();

        String employeeId;
        while (true) {
            sendPrompt("Enter employee ID (must be an integer):");
            employeeId = receiveInput();
            try {
                Integer.parseInt(employeeId);
                break;
            } catch (NumberFormatException e) {
                sendMessage("Invalid input. Please enter a valid integer for employee ID.");
            }
        }

        sendPrompt("Enter email:");
        String email = receiveInput();

        sendPrompt("Enter password:");
        String password = receiveInput();

        sendPrompt("Enter department name:");
        String departmentName = receiveInput();

        sendPrompt("Enter role:");
        String role = receiveInput();

        User newUser = new User(name, employeeId, email, password, departmentName, role);
        if (sharedObject.addUser(newUser)) {
            sendMessage("Registration successful!");
        } else {
            sendMessage("Registration failed. Email or Employee ID already exists.");
        }
    }

    //CREATE REPORT
    private void createReport() throws IOException, ClassNotFoundException {
        try {
            sendPrompt("Enter report type (ACCIDENT or RISK):");
            Report.ReportType reportType = Report.ReportType.valueOf(receiveInput().toUpperCase());

            LocalDateTime localDateTime = LocalDateTime.now();
            Report newReport = new Report(reportType, localDateTime, "", serverThread.getCurrentUser().getEmployeeId(), "UNASSIGNED");
            sharedObject.addReport(newReport);
            sendMessage("Report created successfully with ID: " + newReport.getReportId());
        } catch (IOException | IllegalArgumentException e) {
            sendMessage("An error occurred, please try again ");
        }
    }

    //RETRIEVE ACCIDENT REPORTS
    private void retrieveAccidentReports() throws IOException {
        LinkedList<Report> reports = sharedObject.getAllReports();
        boolean found = false;

        //go through the reports and display only the accidents
        for (Report report : reports) {
            if (report.getReportType() == Report.ReportType.ACCIDENT) {
                sendMessage(report.toString());
                found = true;
            }
        }

        if (!found) {
            sendMessage("No accident reports found.");
        }
    }

    //ASSIGN A REPORT
    private void assignReport() throws IOException, ClassNotFoundException {
        try {
            sendPrompt("Enter Report ID to assign:");
            String reportId = receiveInput();

            LinkedList<Report> reports = sharedObject.getAllReports();
            Report targetReport = null;

            //find report by id
            for (Report report : reports) {
                if (report.getReportId().equals(reportId)) {
                    targetReport = report;
                    break;
                }
            }

            if (targetReport == null) {
                sendMessage("Report not found.");
                return;
            }

            sendPrompt("Enter Employee ID to assign the report to:");
            String employeeId = receiveInput();

            boolean employeeExists = false;
            for (User user : sharedObject.getAllUsers()) {
                //check if the employee id exists
                if (user.getEmployeeId().equals(employeeId)) {
                    employeeExists = true;
                    break;
                }
            }

            if (!employeeExists) {
                sendMessage("Employee not found.");
                return;
            }

            targetReport.setAssignedEmployeeId(employeeId); //assign the report to that employee

            Report.Status newStatus = null;
            while (newStatus == null) {
                sendPrompt("Enter status (OPEN, ASSIGNED, or CLOSED):");
                String statusInput = receiveInput().toUpperCase();
                try {
                    newStatus = Report.Status.valueOf(statusInput);
                } catch (IllegalArgumentException e) {
                    sendMessage("Invalid status. Please enter OPEN, ASSIGNED, or CLOSED.");
                }
            }

            targetReport.setStatus(newStatus);

            //update the report and save it
            if (sharedObject.updateReport(targetReport)) {
                sendMessage("Report assigned successfully.");
                sendMessage("Report ID: " + targetReport.getReportId());
                sendMessage("Assigned to Employee ID: " + targetReport.getAssignedEmployeeId());
                sendMessage("Status: " + targetReport.getStatus());
            } else {
                sendMessage("Failed to update report.");
            }

        } catch (IOException e) {
            sendMessage("Error occurred, please try again");
        }
    }

    //VIEW REPORTS ASSIGNED TO YOU
    private void viewAssignedReports() throws IOException {
        LinkedList<Report> reports = sharedObject.getAllReports();
        boolean found = false;

        //go through the reports and display the ones assigned to you
        for (Report report : reports) {
            if (report.getAssignedEmployeeId().equals(serverThread.getCurrentUser().getEmployeeId())) {
                sendMessage(report.toString());
                found = true;
            }
        }

        if (!found) {
            sendMessage("No reports assigned to you.");
        }
    }

    //UPDATE PASSWORD
    private void updatePassword() throws IOException, ClassNotFoundException {
        try {
            sendPrompt("Enter new password:");
            String newPassword = receiveInput();

            //update the password and save it
            if (sharedObject.updatePassword(serverThread.getCurrentUser().getEmail(), newPassword)) {
                serverThread.getCurrentUser().setPassword(newPassword);
                sendMessage("Password updated successfully.");
            } else {
                sendMessage("Failed to update password.");
            }
        } catch (IOException e) {
            sendMessage("Error occurred, please try again");
        }
    }

    //send and receive message to and from the client
    private void sendMessage(String message) throws IOException {
        serverThread.sendMessage(message);
    }

    private void sendPrompt(String message) throws IOException {
        serverThread.sendPrompt(message);
    }

    private String receiveInput() throws IOException, ClassNotFoundException {
        return serverThread.receiveMessage();
    }
}