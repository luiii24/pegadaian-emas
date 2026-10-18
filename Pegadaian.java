package com.mycompany.mavenproject2;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Kelas yang menangani logika bisnis dan operasional utama Pegadaian.
 * 
 * @author lui
 */
public class Pegadaian {
    
    /**
     * Memproses penerimaan barang gadai dari nasabah dan menghasilkan kontrak transaksi baru.
     * 
     * @param nasabah Objek Nasabah yang melakukan pengajuan gadai.
     * @param emas Objek Emas yang dijadikan jaminan.
     * @param lamaAngsuran Durasi tenor pinjaman dalam satuan bulan (hanya menerima 3, 6, atau 12).
     * @return Objek TransaksiGadai yang berisi seluruh rincian pembiayaan.
     * @throws IllegalArgumentException Jika tenor angsuran yang dimasukkan tidak sesuai (bukan 3, 6, atau 12 bulan).
     */
    public TransaksiGadai terimaGadai(Nasabah nasabah, Emas emas, int lamaAngsuran) {
        if (lamaAngsuran != 3 && lamaAngsuran != 6 && lamaAngsuran != 12) {
            throw new IllegalArgumentException("Pilihan angsuran tidak valid. Hanya tersedia tenor 3, 6, atau 12 bulan.");
        }

        double nilaiTaksiran = emas.getTaksiran();
        double nominalPinjaman = nilaiTaksiran * 0.85;

        return new TransaksiGadai(nasabah, emas, nilaiTaksiran, nominalPinjaman, lamaAngsuran);
    }
}