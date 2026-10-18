/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject2;

/**
 *
 * @author THINKPAD L13 YOGA
 */
class Nasabah {
    private String nik;
    private String nama;
    private String noHp;

    public Nasabah(String nik, String nama, String noHp) {
        this.nik = nik;
        this.nama = nama;
        this.noHp = noHp;
    }

    public String getNama() { return nama; }
    // TAMBAHAN UNTUK GUI:
    public String getNik() { return nik; }
    public String getNoHp() { return noHp; }
}

