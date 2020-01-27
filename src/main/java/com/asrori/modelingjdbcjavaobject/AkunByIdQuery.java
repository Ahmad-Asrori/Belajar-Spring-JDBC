package com.asrori.modelingjdbcjavaobject;

import com.asrori.domain.Akun;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
public class AkunByIdQuery extends MappingSqlQuery<Akun> {

    String SQL = "select id, nama, saldo, waktuakses, terkunci from akun where id = ?";

    public AkunByIdQuery(DataSource ds) {
        super(ds, "select id, nama, saldo, waktuakses, terkunci from akun where id = ?");
        declareParameter(new SqlParameter(Types.BIGINT));
        compile();
    }

    @Override
    protected Akun mapRow(ResultSet resultSet, int i) throws SQLException {
        Akun akun = new Akun();
        akun.setId(resultSet.getLong("id"));
        akun.setNama(resultSet.getString("nama"));
        akun.setSaldo(resultSet.getDouble("saldo"));
        akun.setWaktuAkses(resultSet.getTimestamp("waktuakses"));
        akun.setTerkunci(resultSet.getBoolean("terkunci"));
        return akun;
    }
}
