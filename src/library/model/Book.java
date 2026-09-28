package library.model;

/**
 * Class Book merepresentasikan sebuah buku di perpustakaan.
 * Menunjukkan penggunaan: class, constructor, variabel (primitive & reference),
 * serta manipulasi String dan Character (lihat method getKodeBuku()).
 */
public class Book {

    // Variabel reference type
    private final String judul;
    private final String penulis;
    private final String kategori;

    // Variabel primitive type
    private final int tahunTerbit;
    private boolean tersedia; // status ketersediaan: true = tersedia, false = sedang dipinjam

    // Constructor: inisialisasi buku baru (default: tersedia = true)
    public Book(String judul, String penulis, int tahunTerbit, String kategori) {
        this.judul = judul;
        this.penulis = penulis;
        this.tahunTerbit = tahunTerbit;
        this.kategori = kategori;
        this.tersedia = true;
    }

    // ===== Getter & Setter =====
    public String getJudul() {
        return judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public int getTahunTerbit() {
        return tahunTerbit;
    }

    public String getKategori() {
        return kategori;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    /**
     * Contoh manipulasi CHARACTER & STRING:
     * Membuat kode buku otomatis dari huruf pertama judul (di-uppercase-kan
     * pakai Character.toUpperCase) + 3 huruf pertama kategori + tahun terbit.
     * Contoh: "Laskar Pelangi", kategori "Novel", tahun 2005 -> "L-NOV-2005"
     * @return 
     */
    public String getKodeBuku() {
        char hurufPertama = Character.toUpperCase(judul.charAt(0));
        String kategoriBersih = kategori.trim();
        String kodeKategori = kategoriBersih.length() >= 3
                ? kategoriBersih.substring(0, 3).toUpperCase()
                : kategoriBersih.toUpperCase();
        return hurufPertama + "-" + kodeKategori + "-" + tahunTerbit;
    }

    @Override
    public String toString() {
        String status = tersedia ? "Tersedia" : "Dipinjam";
        return String.format("[%s] %-25s | Penulis: %-15s | Tahun: %-4d | Kategori: %-10s | Status: %s",
                getKodeBuku(), judul, penulis, tahunTerbit, kategori, status);
    }
}
