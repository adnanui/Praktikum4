package library.exception;

/**
 * Exception yang dilempar ketika buku yang ingin dipinjam ternyata
 * statusnya sedang tidak tersedia (sudah dipinjam anggota lain).
 */
public class BookAlreadyBorrowedException extends Exception {
    public BookAlreadyBorrowedException(String message) {
        super(message);
    }
}
