package com.mycompany.mavenproject2;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Merepresentasikan detail kontrak transaksi pembiayaan gadai emas.
 * Menyimpan data rincian bunga, sisa tagihan, jadwal jatuh tempo, dan status pelunasan.
 */
public class TransaksiGadai {
    private String idTransaksi;
    private Nasabah nasabah;
    private Emas emas;
    private double hargaAwal;
    private double nominalPinjaman;
    private int lamaAngsuran;
    private int sisaAngsuran;
    private double persentaseBunga;
    private double cicilanPerBulan;
    private double sisaTagihan;
    private boolean isLunas;

    private LocalDate tanggalTransaksi;
    private LocalDate tanggalJatuhTempo;
    private int bulanPembayaranKe;

    /**
     * Konstruktor untuk membuat kontrak transaksi gadai baru beserta kalkulasi bunga dan cicilan otomatis.
     * 
     * @param nasabah Objek nasabah peminjam.
     * @param emas Objek jaminan emas.
     * @param hargaAwal Nilai taksiran murni emas sebelum dikenakan LTV (Loan to Value).
     * @param nominalPinjaman Jumlah dana riil (plafon) yang cair ke nasabah (85% dari taksiran).
     * @param lamaAngsuran Jangka waktu angsuran dalam bulan (3, 6, atau 12).
     */
    public TransaksiGadai(Nasabah nasabah, Emas emas, double hargaAwal, double nominalPinjaman, int lamaAngsuran) {
        this.idTransaksi = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.nasabah = nasabah;
        this.emas = emas;
        this.hargaAwal = hargaAwal;
        this.nominalPinjaman = nominalPinjaman;
        this.lamaAngsuran = lamaAngsuran;
        this.sisaAngsuran = lamaAngsuran;
        this.isLunas = false;
        this.bulanPembayaranKe = 1;

        this.tanggalTransaksi = LocalDate.now();
        this.tanggalJatuhTempo = this.tanggalTransaksi.plusMonths(lamaAngsuran);

        if (lamaAngsuran == 3) {
            this.persentaseBunga = 1.15;
        } else if (lamaAngsuran == 6) {
            this.persentaseBunga = 1.25;
        } else if (lamaAngsuran == 12) {
            this.persentaseBunga = 1.40;
        }

        double totalBunga = ((this.persentaseBunga / 100) * lamaAngsuran) * nominalPinjaman;
        double totalPinjaman = nominalPinjaman + totalBunga;
        this.cicilanPerBulan = totalPinjaman / lamaAngsuran;
        this.sisaTagihan = totalPinjaman;
    }

    /** @return ID unik dari transaksi. */
    public String getIdTransaksi() { return idTransaksi; }
    
    /** @return Data entitas nasabah peminjam. */
    public Nasabah getNasabah() { return nasabah; }
    
    /** @return Data jaminan emas. */
    public Emas getEmas() { return emas; }
    
    /** @return Nilai taksiran asli emas. */
    public double getHargaAwal() { return hargaAwal; }
    
    /** @return Plafon pinjaman yang dicairkan. */
    public double getNominalPinjaman() { return nominalPinjaman; }
    
    /** @return Durasi kontrak angsuran dalam bulan. */
    public int getLamaAngsuran() { return lamaAngsuran; }
    
    /** @return Jumlah sisa angsuran yang belum dibayar. */
    public int getSisaAngsuran() { return sisaAngsuran; }
    
    /** @return Persentase bunga yang ditetapkan per bulan. */
    public double getPersentaseBunga() { return persentaseBunga; }
    
    /** @return Nominal tagihan cicilan per bulan. */
    public double getCicilanPerBulan() { return cicilanPerBulan; }
    
    /** @return Sisa total tagihan yang wajib dilunasi. */
    public double getSisaTagihan() { return sisaTagihan; }
    
    /** @return Status apakah transaksi sudah lunas atau belum. */
    public boolean isLunas() { return isLunas; }
    
    /** @return Tanggal kontrak transaksi diterbitkan. */
    public LocalDate getTanggalTransaksi() { return tanggalTransaksi; }
    
    /** @return Tanggal kontrak berakhir. */
    public LocalDate getTanggalJatuhTempo() { return tanggalJatuhTempo; }
    
    /** @return Siklus bulan pembayaran cicilan yang sedang berjalan. */
    public int getBulanPembayaranKe() { return bulanPembayaranKe; }
    
    /** @return Jumlah angsuran yang sudah dibayar. */
    public int getTelahDibayar() { return lamaAngsuran - sisaAngsuran; }

    /**
     * Memeriksa apakah nasabah memiliki status menunggak berdasarkan tanggal simulasi/sistem saat ini.
     * 
     * @param tanggalSaatIni Tanggal sistem berjalan yang ingin dicek terhadap jatuh tempo bulanan.
     * @return true jika tanggal sistem melewati batas jatuh tempo bulanan dan cicilan belum lunas.
     */
    public boolean isMenunggak(LocalDate tanggalSaatIni) {
        if (isLunas) return false;
        LocalDate jatuhTempoBulanIni = tanggalTransaksi.plusMonths(bulanPembayaranKe);
        return tanggalSaatIni.isAfter(jatuhTempoBulanIni);
    }

    /**
     * Memproses pembayaran satu kali angsuran bulanan.
     * Akan memotong sisa tagihan, mengurangi sisa angsuran, dan memvalidasi kelunasan kontrak otomatis.
     */
    public void bayarAngsuran() {
        if (isLunas) return;
        sisaTagihan -= cicilanPerBulan;
        sisaAngsuran--;
        bulanPembayaranKe++;
        if (sisaTagihan < 1) sisaTagihan = 0;
        if (sisaAngsuran == 0) isLunas = true;
    }
}