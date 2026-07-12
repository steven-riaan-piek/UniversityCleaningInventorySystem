package za.co.bc.inventory.model;

public class Cleaner {
    private int id;
    private String fullName;
    private String employeeNumber;
    private String phone;
    private String email;
    private String department;

    public Cleaner() {}

    public Cleaner(int id, String fullName, String employeeNumber, String phone, String email, String department) {
        this.id = id;
        this.fullName = fullName;
        this.employeeNumber = employeeNumber;
        this.phone = phone;
        this.email = email;
        this.department = department;
    }

    public Cleaner(String fullName, String employeeNumber, String phone, String email, String department) {
        this(0, fullName, employeeNumber, phone, email, department);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
