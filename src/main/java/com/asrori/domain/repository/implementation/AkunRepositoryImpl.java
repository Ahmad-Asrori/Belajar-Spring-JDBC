package com.asrori.domain.repository.implementation;

import com.asrori.domain.Akun;
import com.asrori.domain.repository.AkunRepository;
import com.asrori.exception.DeleteFailedException;
import com.asrori.exception.InsertFailedException;
import com.asrori.exception.UpdateFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AkunRepositoryImpl implements AkunRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private MappingSqlQuery<Akun> akunByIdQuery;

    @Override
    public void insert(Akun akun) {
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory("insert into akun (nama, saldo, waktuakses, terkunci) values (?, ?, ?, ?)", new int[]{Types.VARCHAR, Types.DOUBLE, Types.TIMESTAMP, Types.BOOLEAN});
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int count = jdbcTemplate.update(factory.newPreparedStatementCreator(new Object[]{akun.getNama(), akun.getSaldo(), akun.getWaktuAkses(), akun.isTerkunci()}), keyHolder);
        if (count != 1) throw new InsertFailedException("gagal memasukkan data akun");

        akun.setId(keyHolder.getKey().longValue());
    }

    @Override
    public void update(Akun akun) {
        int count = jdbcTemplate.update("update akun set (nama, saldo, waktuakses, terkunci) = (?, ?, ?, ?) where id = ?",
                akun.getNama(), akun.getSaldo(), akun.getWaktuAkses(), akun.isTerkunci(), akun.getId());

        if (count != 1) throw new UpdateFailedException("gagal mengupdate akun");
    }

    @Override
    public void update(final List<Akun> akuns) {
        int[] counts = jdbcTemplate.batchUpdate("update akun set (nama, saldo, waktuakses, terkunci) = (?, ?, ?, ?) where id = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        Akun akun = akuns.get(i);
                        preparedStatement.setString(1, akun.getNama());
                        preparedStatement.setDouble(2, akun.getSaldo());
                        preparedStatement.setTimestamp(3, akun.getWaktuAkses());
                        preparedStatement.setBoolean(4, akun.isTerkunci());
                        preparedStatement.setLong(5, akun.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return akuns.size();
                    }
                });

        int i = 0;
        for(int count:counts) {
            if(count == 0) throw new UpdateFailedException("Row not updated :" + i);
            i++;
        }
    }

    @Override
    public void delete(long akunId) {
        int count = jdbcTemplate.update("delete from akun where id = ?", akunId);

        if (count != 1) throw new DeleteFailedException("gagal menghapus");
    }

    @Override
    public Akun find(long akunId) {
        return jdbcTemplate.queryForObject("select * from akun where id = ?", new AkunMapper(), akunId);

        //with query encapsulation
        //return akunByIdQuery.findObject(akunId);
    }

    @Override
    public List<Akun> find(List<Long> akunIds) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("akunId", akunIds);

        return namedParameterJdbcTemplate.query("select * from akun where id in (:akunId)", sqlParameterSource, new AkunMapper());
    }

    @Override
    public Akun find(String nama) {
        Map<String, Object> params = new HashMap<>();
        params.put("nama", nama);

        return namedParameterJdbcTemplate.queryForObject("select * from akun where nama = :nama", params, new AkunMapper());
    }

    @Override
    public List<Akun> find(boolean terkunci) {
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory("select * from akun where terkunci = ?", new int[]{Types.BOOLEAN});

        return jdbcTemplate.query(factory.newPreparedStatementCreator(new Object[]{terkunci}), new AkunMapper());
    }

    private static final class AkunMapper implements RowMapper<Akun> {
        @Override
        public Akun mapRow(ResultSet resultSet, int i) throws SQLException {
            Akun akun = new Akun();
            akun.setId(resultSet.getInt("id"));
            akun.setNama(resultSet.getString("nama"));
            akun.setSaldo(resultSet.getDouble("saldo"));
            akun.setWaktuAkses(resultSet.getTimestamp("waktuakses"));
            akun.setTerkunci(resultSet.getBoolean("terkunci"));
            return akun;
        }
    }
}
