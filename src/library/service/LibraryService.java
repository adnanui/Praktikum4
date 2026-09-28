package library.service;

import library.exception.BookAlreadyBorrowedException;
import library.exception.BookNotFoundException;
import library.exception.BorrowLimitExceededException;
import library.model.Book;
import library.model.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LibraryService menyimpan seluruh data (buku & anggota) dan berisi semua
 * logika bisnis: pencarian, peminjaman, pengembalian, dan analisis/laporan.
 */
public class LibraryService {

    // Koleksi utama menggunakan ArrayList
    private List<Book> daftarBuku;
    private List<Member> daftarAnggota;

    // HashMap untuk statistik: judul buku (lowercase) -> jumlah kali dipinjam
    private Map<String, Integer> statistikPeminjamanBuku;

    // HashMap untuk statistik: kategori -> jumlah kali buku kategori itu dipinjam
    private Map<String, Integer> statistikPeminjamanKategori;

    // HashMap untuk statistik: id anggota -> total jumlah transaksi peminjaman
    private Map<String, Integer> statistikAktivitasAnggota;

    // Primitive: total seluruh transaksi peminjaman yang pernah terjadi
    private int totalTransaksiPeminjaman;

    public LibraryService() {
        this.daftarBuku = new ArrayList<>();
        this.daftarAnggota = new ArrayList<>();
        this.statistikPeminjamanBuku = new HashMap<>();
        this.statistikPeminjamanKategori = new HashMap<>();
        this.statistikAktivitasAnggota = new HashMap<>();
        this.totalTransaksiPeminjaman = 0;
    }

    // ======================================================
    // 1. MANAJEMEN DATA BUKU
    // ======================================================

    public void tambahBuku(Book buku) {
        daftarBuku.add(buku);
    }

    public void tambahAnggota(Member member) {
        daftarAnggota.add(member);
    }

    public List<Book> getDaftarBuku() {
        return daftarBuku;
    }

    public List<Member> getDaftarAnggota() {
        return daftarAnggota;
    }

    // ======================================================
    // 2. PENCARIAN & ANALISIS BUKU
    // ======================================================

    /**
     * Cari buku berdasarkan judul (partial match, tidak case-sensitive).
     * Manipulasi String: toLowerCase() dan contains().
     */
    public List<Book> cariBukuByJudul(String keyword) {
        List<Book> hasil = new ArrayList<>();
        String keywordLower = keyword.toLowerCase().trim();

        for (Book buku : daftarBuku) {
            if (buku.getJudul().toLowerCase().contains(keywordLower)) {
                hasil.add(buku);
            }
        }
        return hasil;
    }

    /**
     * Cari buku berdasarkan kategori (partial match, tidak case-sensitive).
     */
    public List<Book> cariBukuByKategori(String keyword) {
        List<Book> hasil = new ArrayList<>();
        String keywordLower = keyword.toLowerCase().trim();

        for (Book buku : daftarBuku) {
            if (buku.getKategori().toLowerCase().contains(keywordLower)) {
                hasil.add(buku);
            }
        }
        return hasil;
    }

    /**
     * Menghitung jumlah buku pada setiap kategori menggunakan looping
     * dan HashMap.
     */
    public Map<String, Integer> hitungJumlahBukuPerKategori() {
        Map<String, Integer> hasil = new HashMap<>();

        for (Book buku : daftarBuku) {
            String kategori = buku.getKategori();
            if (hasil.containsKey(kategori)) {
                hasil.put(kategori, hasil.get(kategori) + 1);
            } else {
                hasil.put(kategori, 1);
            }
        }
        return hasil;
    }

    // ======================================================
    // Helper pencarian internal (exact match)
    // ======================================================

    private Book cariBukuExact(String judul) {
        for (Book buku : daftarBuku) {
            if (buku.getJudul().equalsIgnoreCase(judul.trim())) {
                return buku;
            }
        }
        return null;
    }

    public Member cariAnggotaById(String id) {
        for (Member m : daftarAnggota) {
            if (m.getId().equalsIgnoreCase(id.trim())) {
                return m;
            }
        }
        return null;
    }

    /**
     * Validasi sederhana ID anggota memakai manipulasi CHARACTER:
     * ID dianggap valid jika hanya terdiri dari huruf dan/atau angka
     * (tidak ada spasi atau simbol aneh).
     */
    private boolean idAnggotaValid(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        char[] karakter = id.trim().toCharArray();
        for (char c : karakter) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    // ======================================================
    // 3 & 4. PEMINJAMAN & PENGEMBALIAN
    // ======================================================

    /**
     * Proses peminjaman buku oleh seorang anggota.
     *
     * @throws BookNotFoundException        jika buku tidak ditemukan
     * @throws BookAlreadyBorrowedException jika buku sedang dipinjam anggota lain
     * @throws BorrowLimitExceededException jika anggota sudah meminjam >= 3 buku
     */
    public void pinjamBuku(String idAnggota, String judulBuku)
            throws BookNotFoundException, BookAlreadyBorrowedException, BorrowLimitExceededException {

        Member anggota = cariAnggotaById(idAnggota);

        // ASSERTION: memastikan data anggota valid sebelum transaksi dilakukan.
        // Jalankan program dengan flag -ea agar assertion aktif (java -ea ...)
        assert anggota != null : "Anggota dengan ID '" + idAnggota + "' tidak valid / tidak ditemukan!";
        assert idAnggotaValid(idAnggota) : "Format ID anggota tidak valid (hanya boleh huruf/angka)!";

        // Jaga-jaga bila assertion sedang dimatikan (default Java), tetap dicegah manual:
        if (anggota == null) {
            System.out.println(">> Anggota dengan ID \"" + idAnggota + "\" tidak ditemukan. Transaksi dibatalkan.");
            return;
        }

        Book buku = cariBukuExact(judulBuku);
        if (buku == null) {
            throw new BookNotFoundException("Buku dengan judul \"" + judulBuku + "\" tidak ditemukan di perpustakaan.");
        }

        if (!buku.isTersedia()) {
            throw new BookAlreadyBorrowedException(
                    "Buku \"" + buku.getJudul() + "\" sedang dipinjam dan belum tersedia.");
        }

        if (anggota.getJumlahPinjaman() >= Member.MAX_PINJAM) {
            throw new BorrowLimitExceededException(
                    "Anggota " + anggota.getNama() + " sudah meminjam " + Member.MAX_PINJAM
                            + " buku (batas maksimal). Kembalikan salah satu buku terlebih dahulu.");
        }

        // Semua validasi lolos -> proses peminjaman
        buku.setTersedia(false);
        anggota.tambahPinjaman(buku);
        catatStatistikPeminjaman(buku, anggota);
    }

    private void catatStatistikPeminjaman(Book buku, Member anggota) {
        totalTransaksiPeminjaman++;

        String judulKey = buku.getJudul().toLowerCase();
        statistikPeminjamanBuku.put(judulKey, statistikPeminjamanBuku.getOrDefault(judulKey, 0) + 1);

        String kategoriKey = buku.getKategori();
        statistikPeminjamanKategori.put(kategoriKey, statistikPeminjamanKategori.getOrDefault(kategoriKey, 0) + 1);

        statistikAktivitasAnggota.put(anggota.getId(),
                statistikAktivitasAnggota.getOrDefault(anggota.getId(), 0) + 1);
    }

    /**
     * Proses pengembalian buku oleh seorang anggota.
     *
     * @throws BookNotFoundException jika buku tidak ditemukan di koleksi,
     *                                atau anggota tidak sedang meminjam buku tsb.
     */
    public void kembalikanBuku(String idAnggota, String judulBuku) throws BookNotFoundException {
        Member anggota = cariAnggotaById(idAnggota);
        assert anggota != null : "Anggota dengan ID '" + idAnggota + "' tidak valid / tidak ditemukan!";

        if (anggota == null) {
            System.out.println(">> Anggota dengan ID \"" + idAnggota + "\" tidak ditemukan. Transaksi dibatalkan.");
            return;
        }

        Book buku = cariBukuExact(judulBuku);
        if (buku == null) {
            throw new BookNotFoundException("Buku dengan judul \"" + judulBuku + "\" tidak ditemukan di perpustakaan.");
        }

        if (!anggota.sedangMeminjam(buku.getJudul())) {
            throw new BookNotFoundException(
                    "Anggota " + anggota.getNama() + " tidak sedang meminjam buku \"" + buku.getJudul() + "\".");
        }

        buku.setTersedia(true);
        anggota.hapusPinjaman(buku);
    }

    // ======================================================
    // 5. ANALISIS AKTIVITAS & LAPORAN
    // ======================================================

    /** Mengembalikan judul buku yang paling sering dipinjam, atau null jika belum ada transaksi. */
    public String bukuPalingSeringDipinjam() {
        String bukuTerpopuler = null;
        int maxJumlah = 0;

        for (Map.Entry<String, Integer> entry : statistikPeminjamanBuku.entrySet()) {
            if (entry.getValue() > maxJumlah) {
                maxJumlah = entry.getValue();
                bukuTerpopuler = entry.getKey();
            }
        }
        return bukuTerpopuler;
    }

    /** Mengembalikan Member paling aktif (transaksi peminjaman terbanyak), atau null jika belum ada transaksi. */
    public Member anggotaPalingAktif() {
        String idTeraktif = null;
        int maxJumlah = 0;

        for (Map.Entry<String, Integer> entry : statistikAktivitasAnggota.entrySet()) {
            if (entry.getValue() > maxJumlah) {
                maxJumlah = entry.getValue();
                idTeraktif = entry.getKey();
            }
        }

        return idTeraktif == null ? null : cariAnggotaById(idTeraktif);
    }

    /** Mengembalikan kategori buku paling populer berdasarkan aktivitas peminjaman. */
    public String kategoriPalingPopuler() {
        String kategoriTerpopuler = null;
        int maxJumlah = 0;

        for (Map.Entry<String, Integer> entry : statistikPeminjamanKategori.entrySet()) {
            if (entry.getValue() > maxJumlah) {
                maxJumlah = entry.getValue();
                kategoriTerpopuler = entry.getKey();
            }
        }
        return kategoriTerpopuler;
    }

    public int getTotalTransaksiPeminjaman() {
        return totalTransaksiPeminjaman;
    }

    /** Mencetak laporan lengkap aktivitas perpustakaan ke layar. */
    public void cetakLaporan() {
        System.out.println("===== LAPORAN PERPUSTAKAAN =====");
        System.out.println("Total buku dalam koleksi   : " + daftarBuku.size());
        System.out.println("Total anggota terdaftar    : " + daftarAnggota.size());
        System.out.println("Jumlah total pinjaman      : " + totalTransaksiPeminjaman);

        Member teraktif = anggotaPalingAktif();
        if (teraktif != null) {
            int jumlah = statistikAktivitasAnggota.get(teraktif.getId());
            System.out.println("Anggota paling aktif       : " + teraktif.getNama()
                    + " (" + jumlah + " kali transaksi)");
        } else {
            System.out.println("Anggota paling aktif       : belum ada transaksi");
        }

        String kategoriPopuler = kategoriPalingPopuler();
        if (kategoriPopuler != null) {
            System.out.println("Kategori paling populer     : " + kategoriPopuler
                    + " (" + statistikPeminjamanKategori.get(kategoriPopuler) + " kali dipinjam)");
        } else {
            System.out.println("Kategori paling populer     : belum ada transaksi");
        }

        String bukuPopuler = bukuPalingSeringDipinjam();
        if (bukuPopuler != null) {
            System.out.println("Buku paling sering dipinjam : " + bukuPopuler
                    + " (" + statistikPeminjamanBuku.get(bukuPopuler) + " kali dipinjam)");
        } else {
            System.out.println("Buku paling sering dipinjam : belum ada transaksi");
        }

        System.out.println("\n--- Jumlah Buku per Kategori ---");
        Map<String, Integer> perKategori = hitungJumlahBukuPerKategori();
        for (Map.Entry<String, Integer> entry : perKategori.entrySet()) {
            System.out.println("  " + entry.getKey() + " : " + entry.getValue() + " buku");
        }
        System.out.println("=================================");
    }
}
