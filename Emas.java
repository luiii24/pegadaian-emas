package com.mycompany.mavenproject2;

/**
 * Merepresentasikan entitas barang jaminan berupa perhiasan atau logam mulia Emas.
 * 
 * @author lui
 */
class Emas {
    private String kodeBarang;
    private double berat;
    private int karat;

    /**
     * Konstruktor untuk membuat data jaminan Emas.
     * 
     * @param kodeBarang Kode unik identifikasi barang (misal: LM01).
     * @param berat Berat emas dalam satuan gram.
     * @param karat Tingkat kemurnian emas dalam satuan karat (maksimal 24).
     */
    public Emas(String kodeBarang, double berat, int karat) {
        this.kodeBarang = kodeBarang;
        this.berat = berat;
        this.karat = karat;
    }

    /**
     * Mengambil kode barang emas.
     * 
     * @return String kode barang.
     */
    public String getKodeBarang() { return kodeBarang; }
    
    /**
     * Mengambil informasi berat emas.
     * 
     * @return Berat emas dalam gram.
     */
    public double getBerat() { return berat; }
    
    /**
     * Mengambil informasi kadar karat emas.
     * 
     * @return Kadar karat emas.
     */
    public int getKarat() { return karat; }

    /**
     * Menghitung nilai taksiran rupiah dari emas yang dijaminkan.
     * Perhitungan didasarkan pada rumus: berat * 1.000.000 * (karat / 24).
     * 
     * @return Total nilai taksiran emas dalam Rupiah.
     */
    public double getTaksiran() {
        return berat * 1000000 * ((double) karat / 24);
    }
}