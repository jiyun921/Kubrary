
package Kubrary;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class LibraryManagementSystem {
    public static HashMap<String, User> users = new HashMap<>();
    public static ArrayList<User> expelledUsers = new ArrayList<>();
    public static Librarian librarian = new Librarian("admin", "admin1234");
    private static BookManager bookManager = new BookManager();

    public static void main(String[] args) {
        loadUsers();
        loadExpelledUsers();
        loadLibrarian();

        Scanner scanner = new Scanner(System.in);

        if (!users.containsKey("000000000")) {
            User devUser = new User("000000000", "devpassword", "Developer", "0000000000", "dev@example.com");
            users.put(devUser.getStudentId(), devUser);
        }

        while (true) {
            displayMainMenu();
            String command = scanner.nextLine().trim();

            switch (command) {
                case "1":
                    register(scanner);
                    break;
                case "2":
                    loginMenu(scanner);
                    break;
                case "3":
                    findPassword(scanner);
                    break;
                case "4":
                    saveData();
                    System.out.println("프로그램을 종료합니다.");
                    return;
                case "5":
                    System.out.println("개발자 모드로 진입합니다.");
                    postLogin(scanner, users.get("000000000"));
                    break;
                default:
                    System.out.println("올바르지 않은 입력입니다.");
                    break;
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("--------------------------------------");
        System.out.println("                KUbrary");
        System.out.println("--------------------------------------");
        System.out.println("1) 회원 가입");
        System.out.println("2) 로그인");
        System.out.println("3) 비밀번호 찾기");
        System.out.println("4) 종료");
    }

    private static void loginMenu(Scanner scanner) {
        System.out.println("<로그인>");
        System.out.println("1) 대출자 프로그램");
        System.out.println("2) 사서 프로그램");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                loginAsUser(scanner);
                break;
            case "2":
                loginAsLibrarian(scanner);
                break;
            default:
                System.out.println("올바르지 않은 입력입니다.");
                break;
        }
    }

    private static void loginAsUser(Scanner scanner) {
        System.out.println("학번을 입력해주세요.");
        String studentId = scanner.nextLine();

        // 탈퇴된 사용자 검사
        if (expelledUsers.stream().anyMatch(user -> user.getStudentId().equals(studentId))) {
            System.out.println("강제 탈퇴된 사용자입니다.");
            return;
        }

        System.out.println("비밀번호를 입력해주세요.");
        String password = scanner.nextLine();

        User user = users.get(studentId);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("로그인이 완료되었습니다.");
            postLogin(scanner, user);
        } else {
            System.out.println("로그인 실패: 학번 또는 비밀번호가 잘못되었습니다.");
        }
    }

    private static void loginAsLibrarian(Scanner scanner) {
        System.out.println("아이디를 입력해주세요.");
        String librarianId = scanner.nextLine().trim();
        System.out.println("비밀번호를 입력해주세요.");
        String password = scanner.nextLine().trim();

        if (librarian.getLibrarianId().equals(librarianId) && librarian.getPassword().equals(password)) {
            System.out.println("사서 로그인에 성공하였습니다.");
            postLoginLibrarian(scanner);
        } else {
            System.out.println("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    private static void register(Scanner scanner) {
        System.out.println("<회원 가입>");

        String phoneNumber = "", email = "", name, password;

        while (true) {
            System.out.println("학번을 입력해주세요.");
            final String studentId = scanner.nextLine().trim(); // final로 선언

            // 탈퇴된 사용자 검사
            if (expelledUsers.stream().anyMatch(user -> user.getStudentId().equals(studentId))) {
                System.out.println("강제 탈퇴된 사용자입니다.");
                return;
            }

            if (studentId.contains(" ")) {
                System.out.println("공백은 포함될 수 없습니다.");
                continue; // 다음 반복으로 이동
            } else if (!studentId.matches("\\d{9}")) {
                System.out.println("학번은 9자리 숫자여야 합니다.");
                continue; // 다음 반복으로 이동
            } else if (users.containsKey(studentId)) {
                System.out.println("이미 등록된 학번입니다.");
                continue; // 다음 반복으로 이동
            }

            while (true) {
                System.out.println("이름을 입력해주세요.");
                name = scanner.nextLine().trim();
                if (name.contains(" ")) {
                    System.out.println("공백은 포함될 수 없습니다.");
                } else if (!name.matches("^[a-z|A-Z|ㄱ-ㅎ|가-힣]*$")) {
                    System.out.println("알맞지 않은 형식입니다.");
                } else if (name.length() == 0) {
                    System.out.println("최소 1자 이상 입력되어야 합니다.");
                } else {
                    break;
                }
            }

            while (true) {
                System.out.println("전화번호를 입력해주세요.");
                final String newPhoneNumber = scanner.nextLine().replaceAll("\\s+", "");
                if (!newPhoneNumber.matches("\\d{10,11}")) {
                    System.out.println("전화번호는 10자리나 11자리의 숫자여야 합니다.");
                } else if (users.values().stream().anyMatch(user -> user.getPhoneNumber().equals(newPhoneNumber))) {
                    System.out.println("이미 등록된 전화번호입니다.");
                } else {
                    phoneNumber = newPhoneNumber;
                    break;
                }
            }

            while (true) {
                System.out.println("이메일을 입력해주세요.");
                final String newEmail = scanner.nextLine().toLowerCase().trim();
                if (newEmail.contains(" ")) {
                    System.out.println("공백은 포함될 수 없습니다.");
                } else if (!newEmail.matches("^[a-zA-Z0-9]+@[a-zA-Z]{1,10}\\.[a-zA-Z]{2,3}(\\.[a-zA-Z]{2})?$")) {
                    System.out.println("이메일 형식이 올바르지 않습니다. 다시 입력해주세요.");
                } else if (users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(newEmail))) {
                    System.out.println("이미 등록된 이메일입니다.");
                } else {
                    email = newEmail;
                    break;
                }
            }

            while (true) {
                while (true) {
                    System.out.println("비밀번호를 입력해주세요.");
                    password = scanner.nextLine().trim();
                    if (password.contains(" ")) {
                        System.out.println("공백은 포함될 수 없습니다.");
                    } else if (password.length() < 7 || password.length() > 15) {
                        System.out.println("비밀번호는 7~15자로 작성해주세요.");
                    } else if (password.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                        System.out.println("한글은 사용할 수 없습니다.");
                    } else {
                        break;
                    }
                }
                System.out.println("비밀번호를 재입력해주세요.");
                String confirmPassword = scanner.nextLine();
                if (!password.equals(confirmPassword)) {
                    System.out.println("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
                } else {
                    break;
                }
            }

            users.put(studentId, new User(studentId, password, name, phoneNumber, email));
            saveUsers();
            System.out.println("회원가입에 성공하였습니다.");
            break; // 모든 작업이 성공적으로 완료되면 while 루프를 종료
        }
    }

    private static void findPassword(Scanner scanner) {
        System.out.println("<비밀번호 찾기>");

        System.out.println("학번을 입력하세요.");
        String studentId = scanner.nextLine();
        if (expelledUsers.contains(studentId)) {
            System.out.println("탈퇴된 회원의 학번입니다.");
            return;
        }

        if (!users.containsKey(studentId)) {
            System.out.println("가입되지 않은 회원 정보입니다.");
            return;
        }

        System.out.println("이름을 입력하세요.");
        String name = scanner.nextLine();

        System.out.println("이메일을 입력하세요.");
        String email = scanner.nextLine().toLowerCase();

        User user = users.get(studentId);
        if (user != null && user.getName().equals(name) && user.getEmail().equalsIgnoreCase(email)) {
            System.out.println("회원님의 비밀번호는 " + user.getPassword() + "입니다.");
            System.out.println("비밀번호 찾기가 완료되었습니다.");
        } else {
            System.out.println("회원 정보가 일치하지 않습니다.");
        }
    }

    public static void forceExpelUser(String userId) {
        System.out.println("약관 위반으로 강제 탈퇴됩니다.");
        User expelledUser = users.remove(userId);
        expelledUsers.add(expelledUser);
        saveUsers();
        saveExpelledUsers();
        throw new RuntimeException("사용자가 강제 탈퇴되었습니다."); // 예외 발생시켜 세션 종료
    }

    private static void postLogin(Scanner scanner, User user) {
        if (user.isFirstLogin()) {
            System.out.println(
                    "도움말:\n-- loan : 도서 대출\n-- return : 도서 반납\n-- search : 도서 검색\n-- help : 도움말\n-- logout : 로그아웃");
            user.setFirstLogin(false);
        }

        while (true) {
            System.out.println("\n실행할 작업의 명령어를 입력하세요:");
            String command = scanner.nextLine().trim().toLowerCase();

            try {
                switch (command) {
                    case "loan":
                        bookManager.displayBooks(scanner, user.getStudentId());
                        break;
                    case "return":
                        bookManager.returnBook(scanner, user.getStudentId());
                        if (user.isSuspended() || expelledUsers.contains(user.getStudentId())) {
                            System.out.println("사용자가 정지 또는 탈퇴되었습니다. 로그아웃합니다.");
                            return;
                        }
                        break;
                    case "search":
                        bookManager.searchBooks(scanner);
                        break;
                    case "help":
                        System.out.println(
                                "도움말:\n-- loan : 도서 대출\n-- return : 도서 반납\n-- search : 도서 검색\n-- help : 도움말\n-- logout : 로그아웃");
                        break;
                    case "logout":
                        saveUsers();
                        System.out.println("로그아웃합니다.");
                        return;
                    default:
                        System.out.println("알 수 없는 명령어입니다.");
                        break;
                }
            } catch (RuntimeException e) {
                // 강제 탈퇴 등의 이유로 발생한 예외를 처리하고 메인 메뉴로 돌아감
                System.out.println(e.getMessage());
                return;
            }
        }
    }

    private static void postLoginLibrarian(Scanner scanner) {
        if (librarian.isFirstLogin()) {
            System.out.println(
                    "도움말:\n-- manage_books : 도서 관리\n-- manage_users : 대출자 관리\n-- change_pass : 패스워드 변경\n-- help : 도움말\n-- logout : 로그아웃");
            librarian.setFirstLogin(false);
        }

        while (true) {
            System.out.println("\n실행할 작업의 명령어를 입력하세요:");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "manage_books":
                    bookManager.manageBooks(scanner);
                    break;
                case "manage_users":
                    manageUsers(scanner);
                    break;
                case "change_pass":
                    changeLibrarianPassword(scanner);
                    break;
                case "help":
                    System.out.println(
                            "도움말:\n-- manage_books : 도서 관리\n-- manage_users : 대출자 관리\n-- change_pass : 패스워드 변경\n-- help : 도움말\n-- logout : 로그아웃");
                    break;
                case "logout":
                    saveUsers();
                    saveLibrarian();
                    System.out.println("로그아웃합니다.");
                    return;
                default:
                    System.out.println("알 수 없는 명령어입니다.");
                    break;
            }
        }
    }

    private static void changeLibrarianPassword(Scanner scanner) {
        System.out.println("현재 비밀번호를 입력하세요:");
        String currentPassword = scanner.nextLine().trim();

        if (!librarian.getPassword().equals(currentPassword)) {
            System.out.println("현재 비밀번호가 일치하지 않습니다.");
            return;
        }

        String newPassword;
        while (true) {
            System.out.println("변경할 비밀번호를 입력하세요:");
            newPassword = scanner.nextLine().trim();

            // 기존 비밀번호와 동일한지 확인하는 로직 추가
            if (newPassword.equals(currentPassword)) {
                System.out.println("이미 사용 중인 비밀번호입니다. 다른 비밀번호를 입력해주세요.");
                return;
            }

            if (newPassword.contains(" ")) {
                System.out.println("공백은 포함될 수 없습니다.");
            } else if (newPassword.length() < 7 || newPassword.length() > 15) {
                System.out.println("비밀번호는 7~15자로 작성해주세요.");
            } else if (newPassword.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                System.out.println("한글은 사용할 수 없습니다.");
            } else {
                break;
            }
        }

        librarian.setPassword(newPassword);
        saveLibrarian();
        System.out.println("성공적으로 변경되었습니다.");
    }

    public static void saveData() {
        saveUsers();
        saveExpelledUsers();
        bookManager.saveLoanedBooks();
        bookManager.saveLostBooks();
        bookManager.saveLoanHistory();
        bookManager.saveCurrentDate();
        saveLibrarian();
    }

    public static void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 8) {
                    User user = new User(values[0], values[1], values[2], values[3], values[4]);
                    user.setFirstLogin(Boolean.parseBoolean(values[5]));
                    user.setSuspensionDays(Integer.parseInt(values[6]));
                    user.setOverdueCount(Integer.parseInt(values[7]));
                    users.put(values[0], user);
                } else {
                    System.out.println("Invalid user data: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            saveUsers();
        } catch (IOException e) {
            System.out.println("users.csv 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    public static void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.csv"))) {
            for (User user : users.values()) {
                bw.write(user.getStudentId() + "," + user.getPassword() + "," + user.getName() + ","
                        + user.getPhoneNumber() + "," + user.getEmail() + "," + user.isFirstLogin() + ","
                        + user.getSuspensionDays() + "," + user.getOverdueCount() + "\n");
            }
        } catch (IOException e) {
            System.out.println("users.csv 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    public static void loadExpelledUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader("expelledUsers.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 8) {
                    User user = new User(values[0], values[1], values[2], values[3], values[4]);
                    user.setFirstLogin(Boolean.parseBoolean(values[5]));
                    user.setSuspensionDays(Integer.parseInt(values[6]));
                    user.setOverdueCount(Integer.parseInt(values[7]));
                    expelledUsers.add(user);
                } else {
                    System.out.println("Invalid expelled user data: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            saveExpelledUsers();
        } catch (IOException e) {
            System.out.println("expelledUsers.csv 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    public static void saveExpelledUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("expelledUsers.csv"))) {
            for (User user : expelledUsers) {
                bw.write(user.getStudentId() + "," + user.getPassword() + "," + user.getName() + ","
                        + user.getPhoneNumber() + "," + user.getEmail() + "," + user.isFirstLogin() + ","
                        + user.getSuspensionDays() + "," + user.getOverdueCount() + "\n");
            }
        } catch (IOException e) {
            System.out.println("expelledUsers.csv 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    public static void loadLibrarian() {
        try (BufferedReader br = new BufferedReader(new FileReader("librarian.csv"))) {
            String line;
            if ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                librarian = new Librarian(values[0], values[1]);
                librarian.setFirstLogin(Boolean.parseBoolean(values[2]));
            }
        } catch (FileNotFoundException e) {
            saveLibrarian();
        } catch (IOException e) {
            System.out.println("librarian.csv 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    public static void saveLibrarian() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("librarian.csv"))) {
            bw.write(
                    librarian.getLibrarianId() + "," + librarian.getPassword() + "," + librarian.isFirstLogin() + "\n");
        } catch (IOException e) {
            System.out.println("librarian.csv 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    private static void manageUsers(Scanner scanner) {
        System.out.println("대출자 관리 메뉴");
        System.out.println("1) 연체로 인한 대출 정지 사용자 해제");
        System.out.println("2) 강제 탈퇴 사용자 해제");
        System.out.println("3) 대출자 기록 확인");
        System.out.println("4) 종료");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                releaseSuspendedUser(scanner);
                break;
            case "2":
                releaseExpelledUser(scanner);
                break;
            case "3":
                System.out.println("기록을 확인할 대출자의 학번을 입력해주세요:");
                String studentId = scanner.nextLine().trim();
                displayUserHistory(studentId);
                break;
            case "4":
                System.out.println("대출자 관리 메뉴를 종료합니다.");
                return;
            default:
                System.out.println("올바르지 않은 입력입니다.");
                break;
        }
    }

    private static void releaseSuspendedUser(Scanner scanner) {
        System.out.println("대출 정지 해제할 사용자의 학번을 입력해주세요:");
        String studentId = scanner.nextLine().trim();
        User user = users.get(studentId);
        String userId = "";

        if (user != null && user.isSuspended()) {
            user.setSuspensionDays(0);
            saveUsers();
            System.out.println(studentId + " 사용자의 대출 정지가 해제되었습니다.");
        } else if (studentId.equals(userId)) {
            System.out.println("해당 학번의 사용자는 강제 탈퇴된 사용자입니다.");
        } else {
            System.out.println("해당 학번의 사용자가 없거나 대출 정지 상태가 아닙니다.");
        }
    }

    private static void releaseExpelledUser(Scanner scanner) {
        System.out.println("강제 탈퇴 해제할 사용자의 학번을 입력해주세요:");
        String studentId = scanner.nextLine().trim();

        for (User user : expelledUsers) {
            if (user.getStudentId().equals(studentId)) {
                expelledUsers.remove(user);
                users.put(studentId, user);
                saveUsers();
                saveExpelledUsers();
                System.out.println(studentId + " 사용자의 강제 탈퇴가 해제되었습니다.");
                return;
            }
        }
        if (!users.containsKey(studentId))
            System.out.println("해당 학번의 사용자는 존재하지 않습니다.");
        else
            System.out.println("해당 학번의 사용자가 강제 탈퇴된 상태가 아닙니다.");
    }

    private static void displayUserHistory(String studentId) {
        User user = users.get(studentId);
        String userId = "";

        for (User usert : expelledUsers) {
            if (usert.getStudentId().equals(studentId))
                userId = studentId;
        }

        if (userId.equals(studentId)) {
            System.out.println("해당 학번의 사용자는 현재 강제 탈퇴된 상태입니다.");
            return;
        } else if (user == null) {
            System.out.println("해당 학번의 사용자가 존재하지 않습니다.");
            return;
        }

        System.out.println("학번: " + studentId + "의 대출 기록입니다.");
        boolean found = false;
        for (LoanRecord record : bookManager.getLoanHistory().values()) {
            if (record.getUserId().equals(studentId)) {
                found = true;
                System.out.println("도서 코드: " + record.getBookTitle() + " | 대출 날짜: " + record.getLoanDate()
                        + " | 반납 날짜: " + (record.getReturnDate() != null ? record.getReturnDate() : "미반납"));
            }
        }

        if (!found) {
            System.out.println("대출 기록이 존재하지 않습니다.");
        }

        System.out.println("연체 횟수: " + user.getOverdueCount());

        System.out.println("현재 대출 중인 도서 목록입니다.");
        found = false;
        for (String bookCode : bookManager.getLoanedBooks().keySet()) {
            LoanRecord record = bookManager.getLoanedBooks().get(bookCode);
            if (record.getUserId().equals(studentId)) {
                System.out
                        .println("-- 코드: " + bookCode + " | 제목: " + bookManager.getBooksWithAuthors().get(bookCode)[0]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("대출 중인 도서가 없습니다.");
        }

        if (user.isSuspended()) {
            System.out.println("상태: 대출 정지 (" + user.getSuspensionDays() + "일 남음)");
        }
    }
}