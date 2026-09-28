package library.exception;

/**
 * Exception yang dilempar ketika seorang anggota mencoba meminjam buku
 * padahal jumlah pinjaman aktifnya sudah mencapai batas maksimal (3 buku).
 */
public class BorrowLimitExceededException extends Exception {
    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
