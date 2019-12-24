package com.asrori.UseJDBCTemplate.dao;

import java.util.List;

public interface AkunDao {
    public void insert(Akun Akun);
    public void update(Akun Akun);
    public void update(List<Akun> Akuns);
    public void delete(long AkunId);
    public Akun find(long AkunId);
    public List<Akun> find(List<Long> AkunIds);
    public List<Akun> find(String ownerName);
    public List<Akun> find(boolean locked);
}
