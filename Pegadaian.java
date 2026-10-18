/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject2;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
/**
 *
 * @author THINKPAD L13 YOGA
 */
class Pegadaian {
    public TransaksiGadai terimaGadai(Nasabah nasabah, Emas emas, int lamaAngsuran) {
        // PERUBAHAN: Lempar error agar bisa ditangkap form GUI
        if (lamaAngsuran != 3 && lamaAngsuran != 6 && lamaAngsuran != 12) {
            throw new IllegalArgumentException("Pilihan angsuran tidak valid. Hanya tersedia tenor 3, 6, atau 12 bulan.");
        }

        double nilaiTaksiran = emas.getTaksiran();
        double nominalPinjaman = nilaiTaksiran * 0.85;

        return new TransaksiGadai(nasabah, emas, nilaiTaksiran, nominalPinjaman, lamaAngsuran);
    }
}