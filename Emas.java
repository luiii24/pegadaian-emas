/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject2;

/**
 *
 * @author THINKPAD L13 YOGA
 */
class Emas {
    private String kodeBarang;
    private double berat;
    private int karat;

    public Emas(String kodeBarang, double berat, int karat) {
        this.kodeBarang = kodeBarang;
        this.berat = berat;
        this.karat = karat;
    }

    public String getKodeBarang() { return kodeBarang; }
    // TAMBAHAN UNTUK GUI:
    public double getBerat() { return berat; }
    public int getKarat() { return karat; }

    public double getTaksiran() {
        return berat * 1000000 * ((double) karat / 24);
    }
}
