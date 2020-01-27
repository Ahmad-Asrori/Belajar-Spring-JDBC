## Melakukan Operasi Data Access Dengan Spring

#### Menjalankan Berbagai Query
JabcTemplate menawarkan berbagai macam method untuk menjalankan query dan menangani hasil dari query tersebut dengan berbagai tipe
objek didalam aplikasi kita. kita biasanya menggunakan method query(), queryForObject(), queryForList(), queryForMap() dan
queryForRowSet().

#### Menjalankan Query dengan Menggunakan JdbcTemplate
```text
@Repository
public class AkunRepositoryImpl implements AkunRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Akun find(long akunId) {
        return jdbcTemplate.queryForObject("select * from akun where id = ?", new AkunMapper(), akunId);
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
```
```text
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        AkunRepository akunRepository = applicationContext.getBean(AkunRepository.class);

        Akun akun = akunRepository.find(200);
        System.out.println(akun.getId());
        System.out.println(akun.getNama());
        System.out.println(akun.getSaldo());
        System.out.println(akun.getWaktuAkses());
        System.out.println(akun.isTerkunci());

        applicationContext.close();
    }
}
```
##### Penjelasan
[dalam pengerjaan]

#### Menjalankan Query dengan Menggunakan NamedParameter
```text
@Repository
public class AkunRepositoryImpl implements AkunRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Akun find(String nama) {
        Map<String, Object> params = new HashMap<>();
        params.put("nama", nama);

        return namedParameterJdbcTemplate.queryForObject("select * from akun where nama = :nama", params, new AkunMapper());
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
```

```text
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        AkunRepository akunRepository = applicationContext.getBean(AkunRepository.class);

        Akun akun = akunRepository.find("budi");
        System.out.println(akun.getId());
        System.out.println(akun.getNama());
        System.out.println(akun.getSaldo());
        System.out.println(akun.getWaktuAkses());
        System.out.println(akun.isTerkunci());

        applicationContext.close();
    }
}
```
##### Penjelasan 
[dalam pengerjaan]

#### Menulis Query Menggunakan IN clause

```text
   @Override
   public List<Akun> find(List<Long> akunIds) {
       SqlParameterSource sqlParameterSource = new MapSqlParameterSource("akunId", akunIds);

       return namedParameterJdbcTemplate.query("select * from akun where id in (:akunId)", sqlParameterSource, new AkunMapper());
   }
```

##### Penjelasan
[dalam pengerjaan]

#### Menggunakan PreparedStatement didalam JdbcTemplate

```text
@Override
public List<Akun> find(boolean terkunci) {
    PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory("select * from akun where terkunci = ?", new int[]{Types.BOOLEAN});

    return jdbcTemplate.query(factory.newPreparedStatementCreator(new Object[]{terkunci}), new AkunMapper());
}
```

##### Penjelasan

[dalam pengerjaan]

#### Insert, Update, Delete dengan JdbcTemplate

```text
@Override
public void insert(Akun akun) {
    PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory("insert into akun (nama, saldo, waktuakses, terkunci) values (?, ?, ?, ?)", new int[]{Types.VARCHAR, Types.DOUBLE, Types.TIMESTAMP, Types.BOOLEAN});
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int count = jdbcTemplate.update(factory.newPreparedStatementCreator(new Object[]{akun.getNama(), akun.getSaldo(), akun.getWaktuAkses(), akun.isTerkunci()}), keyHolder);
    if (count != 1) throw new InsertFailedException("gagal memasukkan data akun");

    akun.setId(keyHolder.getKey().longValue());
}
```

```text
public class InsertFailedException extends DataAccessException {

    public InsertFailedException(String msg) {
        super(msg);
    }

}

```

```text
@Override
public void update(Akun akun) {
    int count = jdbcTemplate.update("update akun set (nama, saldo, waktuakses, terkunci) = (?, ?, ?, ?) where id = ?",
    akun.getNama(), akun.getSaldo(), akun.getWaktuAkses(), akun.isTerkunci(), akun.getId());

    if (count != 1) throw new UpdateFailedException("gagal mengupdate akun");
}
```

```text
public class UpdateFailedException extends DataAccessException {

    public UpdateFailedException(String msg) {
        super(msg);
    }

}
```

```text
@Override
public void delete(long akunId) {
    int count = jdbcTemplate.update("delete from akun where id = ?", akunId);

    if (count != 1) throw new DeleteFailedException("gagal menghapus");
}
```

```text
public class DeleteFailedException extends DataAccessException {

    public DeleteFailedException(String msg) {
        super(msg);
    }
}

```

#### Batch Operation

```text
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
```


#### DDL Operation
```text
jdbcTemplate.execute(
    "CREATE TABLE ACCOUNT (
        ID BIGINT IDENTITY PRIMARY KEY,
        OWNER_NAME VARCHAR(255), 
        BALANCE DOUBLE,
        ACCESS_TIME TIMESTAMP, 
        LOCKED BOOLEAN,
        OWNER_PHOTO BLOB, 
        ACCOUNT_DESC CLOB)"
);
```


#### BLOB dan CLOB

```text
jdbcTemplate.update("update account set (owner_photo,account_desc) = (?,?) where id = ? ",
    new PreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
        LobCreator lobCreator = lobHandler.getLobCreator();
        lobCreator.setBlobAsBytes(ps, 1, binaryContent);
        lobCreator.setClobAsString(ps, 2, textualContent);
        ps.setInt(3,accountId);
    }

});
```

#### Enkapsulasi SQL Query

```text
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

```

```text
@Bean
public MappingSqlQuery<Akun> akunByIdQuery(){
    AkunByIdQuery akunByIdQuery = new AkunByIdQuery(dataSource());
    return akunByIdQuery;
}
```

```text
@Repository
public class AkunRepositoryImpl implements AkunRepository {    
    ...
    @Autowired
    private MappingSqlQuery<Akun> akunByIdQuery;

    ...
    @Override
    public Akun find(long akunId) {
        return akunByIdQuery.findObject(akunId);
    }
    ...
}
```








