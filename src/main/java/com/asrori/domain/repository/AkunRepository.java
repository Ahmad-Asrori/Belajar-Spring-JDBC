package com.asrori.domain.repository;

import com.asrori.domain.Akun;

import java.util.List;

public interface AkunRepository {
    public void insert(Akun akun);
    public void update(Akun akun);
    public void update(List<Akun> akuns);
    public void delete(long akunId);
    public Akun find(long akunId);
    public List<Akun> find(List<Long> akunIds);
    public Akun find(String nama);
    public List<Akun> find(boolean terkunci);
}
