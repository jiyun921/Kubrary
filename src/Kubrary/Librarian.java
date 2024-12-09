package Kubrary;

public class Librarian {
    private String librarianId;
    private String password;
    private boolean firstLogin;

    public Librarian(String librarianId, String password) {
        this.librarianId = librarianId;
        this.password = password;
        this.firstLogin = true;
    }

    public String getLibrarianId() {
        return librarianId;
    }

    public String getPassword() {
        return password;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
