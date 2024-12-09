
package Kubrary;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String studentId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private boolean firstLogin;
    private int suspensionDays;
    private int overdueCount;

    public User(String studentId, String password, String name, String phoneNumber, String email) {
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstLogin = true;
        this.suspensionDays = 0;
        this.overdueCount = 0;
    }


    public String getStudentId() {
        return studentId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public boolean isSuspended() {
        return suspensionDays > 0;
    }

    public void setSuspensionDays(int days) {
        this.suspensionDays = days;
    }

    public int getSuspensionDays() {
        return suspensionDays;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }

    public void incrementOverdueCount() {
        overdueCount++;
    }
}