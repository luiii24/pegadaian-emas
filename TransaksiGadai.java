package com.mycompany.mavenproject2;

import java.time.LocalDate;
import java.util.UUID;

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

    public String getIdTransaksi() { return idTransaksi; }
    public Nasabah getNasabah() { return nasabah; }
    public Emas getEmas() { return emas; }
    public double getHargaAwal() { return hargaAwal; }
    public double getNominalPinjaman() { return nominalPinjaman; }
    public int getLamaAngsuran() { return lamaAngsuran; }
    public int getSisaAngsuran() { return sisaAngsuran; }
    public double getPersentaseBunga() { return persentaseBunga; }
    public double getCicilanPerBulan() { return cicilanPerBulan; }
    public double getSisaTagihan() { return sisaTagihan; }
    public boolean isLunas() { return isLunas; }
    public LocalDate getTanggalTransaksi() { return tanggalTransaksi; }
    public LocalDate getTanggalJatuhTempo() { return tanggalJatuhTempo; }
    public int getBulanPembayaranKe() { return bulanPembayaranKe; }
    public int getTelahDibayar() { return lamaAngsuran - sisaAngsuran; }

    // --- FITUR BARU: CEK TUNGGAKAN BERDASARKAN TANGGAL SIMULASI ---
    public boolean isMenunggak(LocalDate tanggalSaatIni) {
        if (isLunas) return false; // Kalau sudah lunas, pasti tidak menunggak
        
        // Jatuh tempo cicilan bulan ini adalah (Tanggal Transaksi + bulan pembayaran saat ini)
        LocalDate jatuhTempoBulanIni = tanggalTransaksi.plusMonths(bulanPembayaranKe);
        
        // Jika tanggal simulasi sudah melewati tanggal jatuh tempo cicilan, berarti menunggak
        return tanggalSaatIni.isAfter(jatuhTempoBulanIni);
    }

    public void bayarAngsuran() {
        if (isLunas) return;
        sisaTagihan -= cicilanPerBulan;
        sisaAngsuran--;
        bulanPembayaranKe++;
        if (sisaTagihan < 1) sisaTagihan = 0;
        if (sisaAngsuran == 0) isLunas = true;
    }
}