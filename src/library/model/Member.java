package library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Member merepresentasikan anggota perpustakaan.
 * daftarPinjaman menyimpan buku-buku yang SEDANG dipinjam anggota ini
 * (menggunakan ArrayList / Collection, bukan Array biasa).
 */
public class Member {

    // Batas maksimal buku yang boleh dipinjam sekaligus (constant / final)
    public static final int MAX_PINJAM = 3;

    // Variabel reference type
    private String id;
    private String nama;
    private List<Book> daftarPinjaman;

    // Constructor
    public Member(String id, String nama) {
        this.id = id;
        this.nama = nama;
        this.daftarPinjaman = new ArrayList<>();
    }

    // ===== Getter =====
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public List<Book> getDaftarPinjaman() {
        return daftarPinjaman;
    }

    // Primitive return: jumlah buku yang sedang dipinjam saat ini
    public int getJumlahPinjaman() {
        return daftarPinjaman.size();
    }

    // Cek apakah anggota sudah meminjam buku tertentu (manipulasi String: equalsIgnoreCase)
    public boolean sedangMeminjam(String judulBuku) {
        for (Book b : daftarPinjaman) {
            if (b.getJudul().equalsIgnoreCase(judulBuku)) {
                return true;
            }
        }
        return false;
    }

    public void tambahPinjaman(Book buku) {
        daftarPinjaman.add(buku);
    }

    public void hapusPinjaman(Book buku) {
        daftarPinjaman.remove(buku);
    }

    @Override
    public String toString() {
        return String.format("ID: %-5s | Nama: %-20s | Sedang meminjam: %d/%d buku",
                id, nama, getJumlahPinjaman(), MAX_PINJAM);
    }
}
