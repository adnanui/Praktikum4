package library.main;

import library.exception.BookAlreadyBorrowedException;
import library.exception.BookNotFoundException;
import library.exception.BorrowLimitExceededException;
import library.model.Book;
import library.model.Member;
import library.service.LibraryService;

import java.util.List;
import java.util.Scanner;

/**
 * MainApp adalah titik masuk (entry point) aplikasi.
 * Berisi menu interaktif berbasis Scanner untuk mengelola perpustakaan mini.
 *
 * Cara menjalankan agar ASSERTION aktif:
 *   javac -d bin src/library/model/*.java src/library/exception/*.java src/library/service/*.java src/library/main/*.java
 *   java -ea -cp bin library.main.MainApp
 */
public class MainApp {

    private static Scanner scanner = new Scanner(System.in);
    private static LibraryService service = new LibraryService();

    public static void main(String[] args) {
        muatDataContoh(); // data awal supaya aplikasi langsung bisa dicoba/didemokan

        boolean lanjut = true;

        // Looping utama menu menggunakan do-while
        do {
            tampilkanMenu();
            int pilihan = bacaPilihanMenu();

            // Struktur kondisional: switch-case
            switch (pilihan) {
                case 1:
                    tambahBuku();
                    break;
                case 2:
                    tampilkanDaftarBuku();
                    break;
                case 3:
                    cariBuku();
                    break;
                case 4:
                    prosesPinjamBuku();
                    break;
                case 5:
                    prosesKembalikanBuku();
                    break;
                case 6:
                    service.cetakLaporan();
                    break;
                case 7:
                    lanjut = false;
                    System.out.println("Terima kasih! Sampai jumpa.");
                    break;
                default:
                    System.out.println(">> Pilihan tidak valid, silakan coba lagi.");
            }

            System.out.println();
        } while (lanjut);

        scanner.close();
    }

    private static void tampilkanMenu() {
        System.out.println("===== SISTEM MANAJEMEN PERPUSTAKAAN MINI =====");
        System.out.println("1. Tambah Buku");
        System.out.println("2. Daftar Buku");
        System.out.println("3. Cari Buku");
        System.out.println("4. Pinjam Buku");
        System.out.println("5. Kembalikan Buku");
        System.out.println("6. Laporan Perpustakaan");
        System.out.println("7. Keluar");
        System.out.print("Pilih menu (1-7): ");
    }

    private static int bacaPilihanMenu() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // akan jatuh ke 'default' di switch-case
        }
    }

    // ================= MENU 1: TAMBAH BUKU =================
    private static void tambahBuku() {
        System.out.print("Judul buku      : ");
        String judul = scanner.nextLine().trim();

        System.out.print("Penulis         : ");
        String penulis = scanner.nextLine().trim();

        System.out.print("Tahun terbit    : ");
        int tahun = bacaAngka();

        System.out.print("Kategori        : ");
        String kategori = scanner.nextLine().trim();

        // Validasi sederhana dengan kondisional
        if (judul.isEmpty() || penulis.isEmpty() || kategori.isEmpty()) {
            System.out.println(">> Data tidak boleh kosong. Buku gagal ditambahkan.");
            return;
        }

        Book bukuBaru = new Book(judul, penulis, tahun, kategori);
        service.tambahBuku(bukuBaru);
        System.out.println(">> Buku berhasil ditambahkan: " + bukuBaru);
    }

    private static int bacaAngka() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print(">> Masukkan angka yang valid: ");
            }
        }
    }

    // ================= MENU 2: DAFTAR BUKU =================
    private static void tampilkanDaftarBuku() {
        List<Book> daftar = service.getDaftarBuku();

        if (daftar.isEmpty()) {
            System.out.println(">> Belum ada buku dalam koleksi.");
            return;
        }

        System.out.println("--- Daftar Buku (" + daftar.size() + ") ---");
        int nomor = 1;
        for (Book buku : daftar) {
            System.out.println(nomor + ". " + buku);
            nomor++;
        }
    }

    // ================= MENU 3: CARI BUKU =================
    private static void cariBuku() {
        System.out.println("Cari berdasarkan: 1) Judul   2) Kategori");
        System.out.print("Pilih (1/2): ");
        String pilihan = scanner.nextLine().trim();

        System.out.print("Kata kunci pencarian: ");
        String keyword = scanner.nextLine().trim();

        List<Book> hasil;
        if (pilihan.equals("2")) {
            hasil = service.cariBukuByKategori(keyword);
        } else {
            hasil = service.cariBukuByJudul(keyword);
        }

        if (hasil.isEmpty()) {
            System.out.println(">> Tidak ada buku yang cocok dengan \"" + keyword + "\".");
        } else {
            System.out.println("--- Hasil Pencarian (" + hasil.size() + ") ---");
            for (Book buku : hasil) {
                System.out.println("- " + buku);
            }
        }
    }

    // ================= MENU 4: PINJAM BUKU =================
    private static void prosesPinjamBuku() {
        System.out.print("ID Anggota   : ");
        String idAnggota = scanner.nextLine().trim();

        System.out.print("Judul buku   : ");
        String judulBuku = scanner.nextLine().trim();

        try {
            service.pinjamBuku(idAnggota, judulBuku);
            System.out.println(">> Peminjaman berhasil! Selamat membaca.");
        } catch (BookNotFoundException | BookAlreadyBorrowedException | BorrowLimitExceededException e) {
            System.out.println(">> Peminjaman gagal: " + e.getMessage());
        } catch (AssertionError e) {
            System.out.println(">> Peminjaman gagal: " + e.getMessage());
        }
    }

    // ================= MENU 5: KEMBALIKAN BUKU =================
    private static void prosesKembalikanBuku() {
        System.out.print("ID Anggota   : ");
        String idAnggota = scanner.nextLine().trim();

        System.out.print("Judul buku   : ");
        String judulBuku = scanner.nextLine().trim();

        try {
            service.kembalikanBuku(idAnggota, judulBuku);
            System.out.println(">> Pengembalian berhasil, terima kasih.");
        } catch (BookNotFoundException e) {
            System.out.println(">> Pengembalian gagal: " + e.getMessage());
        } catch (AssertionError e) {
            System.out.println(">> Pengembalian gagal: " + e.getMessage());
        }
    }

    /**
     * Data contoh (dummy) agar aplikasi bisa langsung dicoba tanpa input manual.
     * Boleh dihapus/diubah sesuai kebutuhan.
     */
    private static void muatDataContoh() {
        service.tambahBuku(new Book("Laskar Pelangi", "Andrea Hirata", 2005, "Novel"));
        service.tambahBuku(new Book("Bumi Manusia", "Pramoedya Ananta Toer", 1980, "Novel"));
        service.tambahBuku(new Book("Filosofi Teras", "Henry Manampiring", 2018, "Self-Improvement"));
        service.tambahBuku(new Book("Algoritma Pemrograman", "Rinaldi Munir", 2016, "Teknologi"));
        service.tambahBuku(new Book("Clean Code", "Robert C. Martin", 2008, "Teknologi"));

        service.tambahAnggota(new Member("A01", "Dimas Pratama"));
        service.tambahAnggota(new Member("A02", "Siti Nuraini"));
    }
}
