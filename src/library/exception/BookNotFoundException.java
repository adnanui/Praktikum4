package library.exception;

/**
 * Exception yang dilempar ketika buku yang dicari/diproses tidak ditemukan
 * di dalam koleksi perpustakaan.
 */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}
