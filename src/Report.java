import java.time.LocalDateTime;

public class Report {
    //enums for report type and status to restrict possible values
    public enum ReportType {
        ACCIDENT,
        RISK
    }

    public enum Status {
        OPEN,
        ASSIGNED,
        CLOSED
    }

    private ReportType reportType; //Accident Report or New Health and Safety Report
    private final String reportId; //Unique ID created by the system
    private final LocalDateTime date; //report creation time
    private final String employeeId; //employeeId
    private Status status; //Open, Assigned or Closed
    private String assignedEmployeeId; //Initially empty until the report has been assigned
    private static int idCounter = 0;

    public Report(ReportType reportType, LocalDateTime date, String reportId, String employeeId, String assignedEmployeeId) {
        this.reportType = reportType;
        this.reportId = generateReportID(); //reportId set in constructor as it is unique
        this.date = date;
        this.employeeId = employeeId;
        this.status = Status.OPEN; //Case is open by default
        this.assignedEmployeeId = employeeId;
    }

    //Getters and Setters
    public ReportType getReportType() {
        return reportType;
    }

    public String generateReportID() {
        return "R-" + (++idCounter);
    }

    public String getReportId() {
        return reportId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    //toString method for returning the report details
    @Override
    public String toString() {
        return "Report{" +
                "reportType=" + reportType +
                ", reportId='" + reportId + '\'' +
                ", date=" + date +
                ", employeeId='" + employeeId + '\'' +
                ", status=" + status +
                ", assignedEmployeeId='" + assignedEmployeeId + '\'' +
                '}';
    }
}
