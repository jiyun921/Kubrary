
package Kubrary;

import java.io.Serializable;

public class LoanRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String bookTitle;
    private String loanDate;
    private String returnDate; // 반납 날짜 추가

    public LoanRecord(String userId, String bookTitle, String loanDate) {
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.loanDate = loanDate;
        this.returnDate = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
