package com.mycompany.mavenproject2;

/**
 * Merepresentasikan entitas Nasabah dalam sistem pegadaian.
 * Kelas ini menyimpan informasi dasar mengenai identitas dan kontak nasabah.
 * 
 * @author lui
 */
public class Nasabah {
    private String nik;
    private String nama;
    private String noHp;

    /**
     * Konstruktor untuk membuat objek Nasabah baru.
     * 
     * @param nik Nomor Induk Kependudukan (KTP) nasabah.
     * @param nama Nama lengkap nasabah.
     * @param noHp Nomor handphone atau WhatsApp nasabah yang dapat dihubungi.
     */
    public Nasabah(String nik, String nama, String noHp) {
        this.nik = nik;
        this.nama = nama;
        this.noHp = noHp;
    }

    /**
     * Mengambil nama lengkap nasabah.
     * 
     * @return Nama nasabah.
     */
    public String getNama() { return nama; }
    
    /**
     * Mengambil NIK nasabah.
     * 
     * @return NIK nasabah.
     */
    public String getNik() { return nik; }
    
    /**
     * Mengambil nomor handphone nasabah.
     * 
     * @return Nomor HP nasabah.
     */
    public String getNoHp() { return noHp; }
}