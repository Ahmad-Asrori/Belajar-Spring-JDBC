package com.asrori.domain;

import java.sql.Timestamp;

public class Akun {
    private long id;
    private String nama;
    private double saldo;
    private Timestamp waktuAkses;
    private boolean terkunci;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Timestamp getWaktuAkses() {
        return waktuAkses;
    }

    public void setWaktuAkses(Timestamp waktuAkses) {
        this.waktuAkses = waktuAkses;
    }

    public boolean isTerkunci() {
        return terkunci;
    }

    public void setTerkunci(boolean terkunci) {
        this.terkunci = terkunci;
    }
}
