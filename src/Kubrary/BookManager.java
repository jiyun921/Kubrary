package Kubrary;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BookManager {
    private HashMap<String, String[]> booksWithAuthors = new HashMap<>();
    private HashMap<String, LoanRecord> loanedBooks = new HashMap<>();
    private HashMap<String, String[]> lostBooks = new HashMap<>();
    private HashMap<String, LoanRecord> loanHistory = new HashMap<>();
    private String currentDate = "0001-01-01";

    public BookManager() {
        loadBookList();
        loadLoanedBooks();
        loadLostBooks();
        loadLoanHistory();
        loadCurrentDate();
    }

    public HashMap<String, String[]> getBooksWithAuthors() {
        return booksWithAuthors;
    }

    public HashMap<String, LoanRecord> getLoanedBooks() {
        return loanedBooks;
    }

    public HashMap<String, LoanRecord> getLoanHistory() {
        return loanHistory;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    private void loadBookList() {
        try {
            File file = new File("booklist.txt");
            if (!file.exists()) {
                saveBookList();
                return;
            }
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\t");
                if (parts.length >= 4) {
                    booksWithAuthors.put(parts[0], new String[] { parts[1], parts[2], parts[3] });
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("booklist.txt 파일을 찾을 수 없습니다.");
        }
    }

    private void loadLoanedBooks() {
        try (BufferedReader br = new BufferedReader(new FileReader("loanedBooks.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                LoanRecord record = new LoanRecord(values[1], values[0], values[2]);
                loanedBooks.put(values[0], record);
            }
        } catch (FileNotFoundException e) {
            saveLoanedBooks();
        } catch (IOException e) {
            System.out.println("loanedBooks.csv 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private void loadLostBooks() {
        try (BufferedReader br = new BufferedReader(new FileReader("lostBooks.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                lostBooks.put(values[0], new String[] { values[1], values[2], values[3] });
            }
        } catch (FileNotFoundException e) {
            saveLostBooks();
        } catch (IOException e) {
            System.out.println("lostBooks.csv 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private void loadLoanHistory() {
        try (BufferedReader br = new BufferedReader(new FileReader("loanHistory.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                LoanRecord record = new LoanRecord(values[1], values[0], values[2]);
                record.setReturnDate(values[3].equals("null") ? null : values[3]);
                loanHistory.put(values[0] + "_" + values[1] + "_" + values[2], record);
            }
        } catch (FileNotFoundException e) {
            saveLoanHistory();
        } catch (IOException e) {
            System.out.println("loanHistory.csv 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private void loadCurrentDate() {
        try (BufferedReader br = new BufferedReader(new FileReader("currentDate.txt"))) {
            currentDate = br.readLine();
        } catch (FileNotFoundException e) {
            saveCurrentDate();
        } catch (IOException e) {
            System.out.println("currentDate.txt 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    public void saveLoanedBooks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("loanedBooks.csv"))) {
            for (Map.Entry<String, LoanRecord> entry : loanedBooks.entrySet()) {
                LoanRecord record = entry.getValue();
                bw.write(record.getBookTitle() + "," + record.getUserId() + "," + record.getLoanDate() + "\n");
            }
        } catch (IOException e) {
            System.out.println("loanedBooks.csv 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    public void saveLostBooks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("lostBooks.csv"))) {
            for (Map.Entry<String, String[]> entry : lostBooks.entrySet()) {
                String[] bookInfo = entry.getValue();
                bw.write(entry.getKey() + "," + bookInfo[0] + "," + bookInfo[1] + "," + bookInfo[2] + "\n");
            }
        } catch (IOException e) {
            System.out.println("lostBooks.csv 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    public void saveLoanHistory() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("loanHistory.csv"))) {
            for (Map.Entry<String, LoanRecord> entry : loanHistory.entrySet()) {
                LoanRecord record = entry.getValue();
                bw.write(record.getBookTitle() + "," + record.getUserId() + "," + record.getLoanDate() + ","
                        + (record.getReturnDate() == null ? "null" : record.getReturnDate()) + "\n");
            }
        } catch (IOException e) {
            System.out.println("loanHistory.csv 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    public void saveCurrentDate() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("currentDate.txt"))) {
            bw.write(currentDate);
        } catch (IOException e) {
            System.out.println("currentDate.txt 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    public boolean setCurrentDate(String newDate, boolean forceUpdate) {
        if (!forceUpdate && newDate.compareTo(currentDate) < 0) {
            System.out.println("이전 날짜입니다. 날짜를 확인해주세요.");
            return false;
        }
        currentDate = newDate;
        saveCurrentDate();
        return true;
    }

    private boolean isValidDate(int year, int month, int day) {
        int[] daysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if (month < 1 || month > 12)
            return false;
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0 && month == 2)
            daysInMonth[1] = 29;
        return day >= 1 && day <= daysInMonth[month - 1];
    }

    public void displayBooks(Scanner scanner, String userId) {
        User user = LibraryManagementSystem.users.get(userId);
        if (user.isSuspended()) {
            System.out.println("대출 정지 기간입니다. 정지 기간이 종료된 후 이용 가능합니다.");
            return;
        }

        System.out.println("현재 대출할 수 있는 도서 목록입니다.");
        TreeMap<String, String[]> sortedBooks = new TreeMap<>(booksWithAuthors);

        ArrayList<Map.Entry<String, String[]>> availableBooks = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : sortedBooks.entrySet()) {
            if (!loanedBooks.containsKey(entry.getKey()) && !lostBooks.containsKey(entry.getKey())) {
                availableBooks.add(entry);
            }
        }

        if (availableBooks.isEmpty()) {
            System.out.println("대출 가능한 도서가 없습니다.");
            return;
        }

        int pageSize = 10;
        int totalBooks = availableBooks.size();
        int totalPages = (totalBooks + pageSize - 1) / pageSize;
        int currentPage = 1;

        while (true) {
            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, totalBooks);

            System.out.println("\n페이지 " + currentPage + " / " + totalPages);
            for (int i = start; i < end; i++) {
                Map.Entry<String, String[]> entry = availableBooks.get(i);
                System.out.println("-- 코드: " + entry.getKey() + " | 제목: " + entry.getValue()[0] + " | 저자: "
                        + entry.getValue()[1] + " | 출판연도: " + entry.getValue()[2]);
            }

            System.out.println("\n다음 작업을 선택해주세요:");
            System.out.println("1) 다음 페이지");
            System.out.println("2) 이전 페이지");
            System.out.println("3) 도서 코드 입력");
            System.out.println("4) 종료");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("마지막 페이지입니다.");
                    }
                    break;
                case "2":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("첫 페이지입니다.");
                    }
                    break;
                case "3":
                    System.out.println("대출하고자 하는 책의 코드를 입력해 주세요.");
                    String bookCode = scanner.nextLine().trim();
                    if (booksWithAuthors.containsKey(bookCode) && !loanedBooks.containsKey(bookCode)
                            && !lostBooks.containsKey(bookCode)) {
                        System.out.println("대출 날짜를 입력하세요. (연도-월-일)");
                        while (true) {
                            String inputDate = scanner.nextLine().trim();
                            try {
                                String[] dateParts = inputDate.split("-");
                                int year = Integer.parseInt(dateParts[0]);
                                int month = Integer.parseInt(dateParts[1]);
                                int day = Integer.parseInt(dateParts[2]);
                                if (dateParts.length == 3 && isValidDate(year, month, day)
                                        && inputDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                                    String newDate = String.format("%d-%02d-%02d", year, month, day);
                                    if (setCurrentDate(newDate, false)) {
                                        LoanRecord loanRecord = new LoanRecord(userId, bookCode, currentDate);
                                        loanedBooks.put(bookCode, loanRecord);
                                        loanHistory.put(bookCode + "_" + userId + "_" + currentDate, loanRecord);
                                        saveLoanedBooks();
                                        saveLoanHistory();
                                        System.out.println("성공적으로 대출하였습니다.");
                                        return;
                                    }
                                } else {
                                    System.out.println("잘못된 날짜 형식입니다. 다시 입력해주세요. (예: 2024-04-07)");
                                }
                            } catch (Exception e) {
                                System.out.println("잘못된 날짜 형식입니다. 다시 입력해주세요. (예: 2024-04-07)");
                            }
                        }
                    } else {
                        System.out.println(bookCode + " 코드는 현재 대출할 수 없습니다.");
                    }
                    break;
                case "4":
                    return;
                default:
                    System.out.println("알 수 없는 명령어입니다.");
                    break;
            }
        }
    }

    public void returnBook(Scanner scanner, String userId) {
        System.out.println("현재 대출 중인 도서 목록입니다.");
        boolean hasLoanedBooks = false;
        for (String bookCode : loanedBooks.keySet()) {
            LoanRecord record = loanedBooks.get(bookCode);
            if (record.getUserId().equals(userId)) {
                System.out.println("-- 코드: " + bookCode + " | 제목: " + booksWithAuthors.get(bookCode)[0]);
                hasLoanedBooks = true;
            }
        }

        if (!hasLoanedBooks) {
            System.out.println("대출 중인 도서가 없습니다.");
            return;
        }

        System.out.println("반납할 도서의 코드를 입력해주세요.");
        String bookCode = scanner.nextLine().trim();

        if (loanedBooks.containsKey(bookCode) && loanedBooks.get(bookCode).getUserId().equals(userId)) {
            System.out.println("반납 날짜를 입력하세요. (연도-월-일)");
            while (true) {
                String inputDate = scanner.nextLine().trim();
                try {
                    String[] dateParts = inputDate.split("-");
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    int day = Integer.parseInt(dateParts[2]);
                    if (dateParts.length == 3 && isValidDate(year, month, day)
                            && inputDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        String newDate = String.format("%d-%02d-%02d", year, month, day);
                        if (setCurrentDate(newDate, false)) {
                            LocalDate dueDate = LocalDate.parse(loanedBooks.get(bookCode).getLoanDate()).plusDays(15);
                            LocalDate returnDate = LocalDate.parse(newDate);
                            long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
                            if (daysOverdue > 0) {
                                User user = LibraryManagementSystem.users.get(userId);
                                user.setSuspensionDays((int) daysOverdue);
                                user.incrementOverdueCount();
                                LibraryManagementSystem.saveUsers();
                                System.out.println(booksWithAuthors.get(bookCode)[0] + " 도서가 성공적으로 반납되었습니다.");
                                System.out.println(daysOverdue + "일 연체되었습니다. " + daysOverdue + "일 동안 대출 기능이 정지됩니다.");
                                if (user.getOverdueCount() > 5 || daysOverdue > 50) {
                                    LibraryManagementSystem.forceExpelUser(userId);
                                    return;
                                }
                            } else {
                                System.out.println(booksWithAuthors.get(bookCode)[0] + " 도서가 성공적으로 반납되었습니다.");
                            }
                            loanedBooks.get(bookCode).setReturnDate(newDate);
                            loanedBooks.remove(bookCode);
                            saveLoanedBooks();
                            saveLoanHistory();
                            break;
                        }
                    } else {
                        System.out.println("잘못된 날짜 형식입니다. 다시 입력해주세요. (예: 2024-04-07)");
                    }
                } catch (RuntimeException e) {
                    // 강제 탈퇴 시 예외 처리 및 메인 메뉴로 돌아감
                    System.out.println(e.getMessage());
                    return;
                } catch (Exception e) {
                    System.out.println("잘못된 날짜 형식입니다. 다시 입력해주세요. (예: 2024-04-07)");
                }
            }
        } else {
            System.out.println("반납할 수 없는 도서입니다. 코드를 확인해주세요.");
        }
    }

    public void searchBooks(Scanner scanner) {
        while (true) {
            System.out.println("번호를 입력해주세요.\n");
            System.out.println("1. 책 제목 검색 \n2. 도서 코드 검색 \n3. 저자명 검색 \n4. 종료");
            String searchNum = scanner.nextLine().trim();
            switch (searchNum) {
                case "1":
                    searchName(scanner);
                    break;
                case "2":
                    searchCode(scanner);
                    break;
                case "3":
                    searchAuthor(scanner);
                    break;
                case "4":
                    System.out.println("도서 검색을 종료합니다.");
                    return;
                default:
                    System.out.println("알 수 없는 명령어입니다.");
                    break;
            }
        }
    }

    public void searchName(Scanner scanner) {
        System.out.println("검색하고자 하는 책 제목을 입력해주세요.");
        String query = scanner.nextLine().trim().toLowerCase().replaceAll(" ", "");

        boolean found = false;
        System.out.println("검색 결과는 다음과 같습니다.");
        for (Map.Entry<String, String[]> entry : booksWithAuthors.entrySet()) {
            if (entry.getValue()[0].toLowerCase().replaceAll(" ", "").contains(query)) {
                found = true;
                String availability = loanedBooks.containsKey(entry.getKey()) ? "대출 불가" : "대출 가능";
                if (lostBooks.containsKey(entry.getKey())) {
                    availability = "도서 분실";
                }
                System.out.println("-- 코드: " + entry.getKey() + " | 제목: " + entry.getValue()[0] + " | 저자: "
                        + entry.getValue()[1] + " | 출판연도: " + entry.getValue()[2] + " | " + availability);
            }
        }

        if (!found) {
            System.out.println("존재하지 않는 도서입니다.");
        }
    }

    public void searchCode(Scanner scanner) {
        System.out.println("검색하고자 하는 도서 코드를 입력해주세요.");
        String query = scanner.nextLine().trim().toLowerCase();

        boolean found = false;
        System.out.println("검색 결과는 다음과 같습니다.");
        for (Map.Entry<String, String[]> entry : booksWithAuthors.entrySet()) {
            if (entry.getKey().toLowerCase().compareTo(query) == 0) {
                found = true;
                String availability = loanedBooks.containsKey(entry.getKey()) ? "대출 불가" : "대출 가능";
                if (lostBooks.containsKey(entry.getKey())) {
                    availability = "도서 분실";
                }
                System.out.println("-- 코드: " + entry.getKey() + " | 제목: " + entry.getValue()[0] + " | 저자: "
                        + entry.getValue()[1] + " | 출판연도: " + entry.getValue()[2] + " | " + availability);
            }
        }

        if (!found) {
            System.out.println("존재하지 않는 도서입니다.");
        }
    }

    public void searchAuthor(Scanner scanner) {
        System.out.println("검색하고자 하는 저자를 입력해주세요.");
        String query = scanner.nextLine().trim().toLowerCase();

        boolean found = false;
        System.out.println("검색 결과는 다음과 같습니다.");
        for (Map.Entry<String, String[]> entry : booksWithAuthors.entrySet()) {
            if (entry.getValue()[1].toLowerCase().contains(query)) {
                found = true;
                String availability = loanedBooks.containsKey(entry.getKey()) ? "대출 불가" : "대출 가능";
                if (lostBooks.containsKey(entry.getKey())) {
                    availability = "도서 분실";
                }
                System.out.println("-- 코드: " + entry.getKey() + " | 제목: " + entry.getValue()[0] + " | 저자: "
                        + entry.getValue()[1] + " | 출판연도: " + entry.getValue()[2] + " | " + availability);
            }
        }

        if (!found) {
            System.out.println("존재하지 않는 도서입니다.");
        }
    }

    public void addBook(Scanner scanner) {
        System.out.println("추가할 도서의 코드를 입력해주세요 (5자리 숫자):");
        String code = scanner.nextLine().trim();

        if (!booksWithAuthors.containsKey(code)) {
            if (!code.matches("\\d{5}")) {
                System.out.println("도서 코드는 5자리 숫자여야 합니다. 다시 입력해주세요.");
                return;
            }

            System.out.println("추가할 도서의 제목을 입력해주세요:");
            String title = scanner.nextLine().trim();

            if (title.isEmpty()) {
                System.out.println("도서 제목을 공백으로 입력할 수 없습니다. 다시 입력해주세요.");
                return;
            }
            System.out.println("추가할 도서의 저자를 입력해주세요:");
            String author = scanner.nextLine().trim();

            if (!author.matches("^[가-힣\\s]+$")) {
                System.out.println("잘못된 저자명입니다. 다시 입력해주세요.");
                return;
            }

            System.out.println("추가할 도서의 출판연도를 입력해주세요:");
            String year = scanner.nextLine().trim();

            if (!year.matches("\\d{4}")) {
                System.out.println("출판 연도는 4자리 숫자여야 합니다. 다시 입력해주세요.");
                return;
            }

            booksWithAuthors.put(code, new String[] { title, author, year });
            saveBookList();
            System.out.println("도서가 성공적으로 추가되었습니다.");
        } else {
            System.out.println("이미 존재하는 도서 코드입니다.");
        }
    }

    public void changeBookStatus(Scanner scanner) {
        System.out.println("변경할 도서의 상태를 선택해주세요.");
        System.out.println("1) 도서 삭제");
        System.out.println("2) 도서 분실 처리");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("삭제할 도서의 코드를 입력해주세요:");
                String deleteCode = scanner.nextLine().trim();

                if (!deleteCode.matches("\\d{5}")) {
                    System.out.println("도서 코드는 5자리 숫자여야 합니다. 다시 입력해주세요.");
                    return;
                }

                if (booksWithAuthors.containsKey(deleteCode)) {
                    booksWithAuthors.remove(deleteCode);
                    loanedBooks.remove(deleteCode);
                    lostBooks.remove(deleteCode);
                    saveBookList();
                    saveLoanedBooks();
                    saveLostBooks();
                    System.out.println("도서가 성공적으로 삭제되었습니다.");
                } else {
                    System.out.println("존재하지 않는 도서 코드입니다.");
                }
                break;
            case "2":
                System.out.println("분실 처리할 도서의 코드를 입력해주세요:");
                String lostCode = scanner.nextLine().trim();

                if (!lostCode.matches("\\d{5}")) {
                    System.out.println("도서 코드는 5자리 숫자여야 합니다. 다시 입력해주세요.");
                    return;
                }
                if (booksWithAuthors.containsKey(lostCode)) {
                    if (lostBooks.containsKey(lostCode))
                        System.out.println("이미 분실된 도서입니다.");
                    else {
                        lostBooks.put(lostCode, booksWithAuthors.get(lostCode));
                        loanedBooks.remove(lostCode);
                        saveLostBooks();
                        saveLoanedBooks();
                        System.out.println("도서가 분실 처리되었습니다.");
                    }
                } else {
                    System.out.println("존재하지 않는 도서 코드입니다.");
                }
                break;
            default:
                System.out.println("올바르지 않은 입력입니다.");
                break;
        }
    }

    public void historyBook(Scanner scanner) {
        System.out.println("기록을 확인할 도서의 코드를 입력해주세요:");
        String code = scanner.nextLine().trim();
        if (!booksWithAuthors.containsKey(code))
            System.out.println("현재 존재하지 않는 도서입니다.");
        else {
            displayBookHistory(code);
            if(lostBooks.containsKey(code))
                System.out.println("현재 상태: 분실");
        }
    }

    private void saveBookList() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("booklist.txt"))) {
            for (Map.Entry<String, String[]> entry : booksWithAuthors.entrySet()) {
                String[] bookInfo = entry.getValue();
                bw.write(entry.getKey() + "\t" + bookInfo[0] + "\t" + bookInfo[1] + "\t" + bookInfo[2] + "\n");
            }
        } catch (IOException e) {
            System.out.println("booklist.txt 파일을 저장하는 중 오류가 발생했습니다.");
        }
    }

    public void displayBookHistory(String bookCode) {
        if (!bookCode.matches("\\d{5}")) {
            System.out.println("도서 코드는 5자리 숫자여야 합니다. 다시 입력해주세요.");
            return;
        }

        System.out.println("도서 코드: " + bookCode + "의 대출 기록입니다.");
        boolean found = false;
        for (LoanRecord record : loanHistory.values()) {
            if (record.getBookTitle().equals(bookCode)) {
                found = true;
                System.out.println("대출자: " + record.getUserId() + " | 대출 날짜: " + record.getLoanDate() + " | 반납 날짜: "
                        + (record.getReturnDate() != null ? record.getReturnDate() : "미반납"));
            }
        }
        if (!found) {
            System.out.println("기록이 존재하지 않습니다.");
        }
    }

    public void manageBooks(Scanner scanner) {
        System.out.println("번호를 입력해주세요.");
        System.out.println("1) 도서 추가");
        System.out.println("2) 도서 상태 변경");
        System.out.println("3) 도서 기록 확인");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                addBook(scanner);
                break;
            case "2":
                changeBookStatus(scanner);
                break;
            case "3":
                historyBook(scanner);
                break;
            default:
                System.out.println("올바르지 않은 입력입니다.");
                break;
        }
    }
}
