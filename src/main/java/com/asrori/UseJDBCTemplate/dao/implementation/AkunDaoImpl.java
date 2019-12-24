package com.asrori.UseJDBCTemplate.dao.implementation;

import com.asrori.UseJDBCTemplate.dao.Akun;
import com.asrori.UseJDBCTemplate.dao.AkunDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AkunDaoImpl implements AkunDao {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(Akun Akun) {

    }

    @Override
    public void update(Akun Akun) {

    }

    @Override
    public void update(List<Akun> Akuns) {

    }

    @Override
    public void delete(long AkunId) {

    }

    @Override
    public Akun find(long AkunId) {
        return jdbcTemplate.queryForObject("select * from akun where id = ?", new AkunMapper(), AkunId);
    }

    @Override
    public List<Akun> find(List<Long> AkunIds) {
        return null;
    }

    @Override
    public List<Akun> find(String ownerName) {
        return null;
    }

    @Override
    public List<Akun> find(boolean locked) {
        return null;
    }

    private static final class AkunMapper implements RowMapper<Akun> {
        @Override
        public Akun mapRow(ResultSet resultSet, int i) throws SQLException {
            Akun akun = new Akun();
            akun.setId(resultSet.getInt("id"));
            akun.setNama(resultSet.getString("nama"));
            akun.setSaldo(resultSet.getDouble("saldo"));
            akun.setWaktuAkses(resultSet.getDate("waktuakses"));
            akun.setTerkunci(resultSet.getBoolean("terkunci"));
            return akun;
        }
    }
}
