### KONFIGURASI DAN PENGGUNAAN JDBCTEMPLATE UNTUK MELAKUKAN DATA ACCESS OPERATION


#### Konfigurasi JdbcTemplate
Pertama tama, kita akan mengurus bagian domain layernya terlebih dulu, kita akan membuat kelas Akun
untuk isi dari kelas Akun terlihar seperti ini
```java
public class Akun {
    private long id;
    private String nama;
    private double saldo;
    private Timestamp waktuAkses;
    private boolean terkunci;

    // implementasi setter dan getter 
}

```
kemudian kita membuat interface AkunDao untuk mendefinisikan persistence operations yang
akan dilakukan pada objek Akun

```java
public interface AkunDao {
    public void insert(Akun akun);
    public void update(Akun akun);
    public void update(List<Akun> akuns);
    public void delete(long akunId);
    public Akun find(long akunId);
    public List<Akun> find(List<Long> akunIds);
    public List<Akun> find(String nama);
    public List<Akun> find(boolean terkunci);
}
```
Buat kelas AkunDaoImpl. Kelas ini mengimplementasikan method method yang
terdapat pada interface AkunDao
```java
public class AkunDaoImpl implements AkunDao {
    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    //implementasi method
}
```
Definisikan bean jdbcTemplate dan isi dependency terhadap DataSource
```java
@Configuration
public class BeanConfiguration{
    ...
    @Bean
    public JdbcTemplate jdbcTemplate(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }  
    ...
}

```
Definisikan bean akunRepository menggunakan kelas AkunDaoImpl dan inject
bean jdbcTemplate ke kelas AkunDaoImpl
```java
@Configuration
public class BeanConfiguration {
    ...
    @Bean
    public AkunDao akunRepository(){
        AkunDaoImpl akunRepository = new AkunDaoImpl();
        akunRepository.setJdbcTemplate(jdbcTemplate());
        return akunRepository;
    }
    ...
}
```
Sekarang kita bisa melakukan bean lookup terhadap bean akunRepository
```java
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);
        AccountDao accountDao = applicationContext.getBean(AkunDao.class);
    }
}
```

##### Penjelasan
JdbcTemplate kita defisikan sebagai bean yang dikelola oleh Spring. bean ini
bersifat thread-safe dan bean ini dapat dishare ke berbagai data access object yang berbeda.
Oleh karena itu, bean ini harus didefinisikan sebagai **singleton.** dependency utama
yang dibutuhkan oleh bean ini adalah objek DataSource. Langkah terakhir adalah menginject bean
jdbcTemplate ke bean akunRepository.


#### Melakukan Query menggunakan JdbcTemplate
Disini fokus kita adalah menggunakan JdbTemplate dan bentuk khususnya yaitu _NamedParameterJdbcTemplate_
untuk mennjukkan beragam cara data access operation. bagaimanapun kita nanti akan mencoba kelas kelas
lain yang di sediakan oleh spring, seperti _SimpleJdbcCall_, kelas digunakan untuk menyederhanakan query 
menggunakan database metadata. _MappingSqlQuery_, _SqlUpdate_, _StoredProcedure_ adalah kelas kelas yang digunakan untuk 
menunjukkan bagaimana operasi operasi pada SQL dapat dibentuk sebagai objek dan digunakan berulang kali.

#### Menjalankan Query Dengan JdbcTemplate
JdbcTemplate menawarkan berbagai method yang sifatnya overloading untuk menjalankan query dan menangani hasil query
dalam berbagai bentuk. kita biasanya menggunakan method _query(...)_, _queryForObject(...)_, dan _queryForRowSet(...)_ 
dengan beberapa bentuk dari method tersebut yang menerima parameter masukan yang berbeda beda seperti query string,
nilai input parameter query, tipenya, tipe result objectnya, dan seterusnya. Kita dapat dapat menggunakan berbagai macam
bentuk sesuai kebutuhan kita.

Kita coba unruk mengimplementasikan method find(akunId) untuk tujuan menemukan akun berdasarkan id
pertama tama kita insert data dulu ke tabel akun

```sql
insert into akun (id, nama, saldo, waktuakses, terkunci) values (100, 'john doe', 10.0,'2014-01-01',false);
```

setelah itu kita isi method find dengan kode sebagai berikut

```java
public Akun find(long akunId){
    return jdbcTemplate.queryForObject("select id, nama, saldo, waktu_akses, terkunci from akun where id = ?",
    new RowMapper<Akun>() {
        @Override
        public Akun mapRow(ResultSet rs, int rowNum) throws SQLException {
            Akun akun = new Akun();
            akun.setId(rs.getLong("id"));
            akun.setNama(rs.getString("nama"));
            akun.setSaldo(rs.getDouble("saldo"));
            akun.setWaktuAkses(rs.getTimestamp("waktuakses"));
            akun.setTerkunci(rs.getBoolean("terkunci"));
            return akun;
        }
    }, akunId);
}
``` 
lalu pada kelas App
```java
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
JdbcTemplate menggunakan template method pattern, dimana pattern ini mencoba membungkus/merangkum langkah langkah utama 
dari algoritma. sehingga developer dapat mengubah bagian bagian individual dari JdbcTemplate dengan melewatkan bagian
bagian tersebut melalui parameter masukan.

```text
try {
    //obtain database connection
    //start a transaction
    //create and execute the query
    //process query result
    //commit the transaction
} catch (SQLException e) {
    //handle SQL exceptions, perform transaction rollback
} finally {
    //close db resources like connections, statements
}
```

Dapat kita lihat dari pseudocode diatas bahwa jika kita menggunakan vanilla JDBC kita akan mengetahui bahwa hanya
ada 2 bagian yang berubah jika kita menulis berbagai method data access yaitu :
1. query dan parameternya
2. pemrosesan hasil query
jika query mengembalikan data berupa list, maka kita harus menggunakan while loop dan memproses setiap baris dengan
perulangan pada result set

kita melewatkan logika pemrosesan hasil ke template method menggunakan callback object, callbank object biasanya
memiliki satu method dimana logika untuk memproses hasil query berdasarkan bentuk data tertentu diimplementasikan. 
callback biasanya diimplementasikan sebegai anonymous class dan langsung di lewatkan ke dalam template method sebagai
parameter inputan, dan template method menjalankannya ketika dibutuhkan.

JdbcTemplate, sebagai implementasi dari template method pattern, terlebih dahulu membungkus data access logic kedalam 
method query execution. method query(...) dan queryForObject(...) mengikuti pendekatan yang sama. mereka menerima query string,
query parameter, dan callback object dari tipe RowMapper sebagai parameter inputan.

RowMapper digunakan untuk memetakan setiap baris yang dikembalikan dari ResultSet ke result object (objek yang dapat
menampung data dalam bentuk tertentu). RowMapper biasanya digunakan sebagai parameter masukan yang diberikan kepada JdbcTemplate.
tetapi RowMapper bisajuga digunakan sebagai parameter dari sebuah store procedure. Method MapRow(ResultSet rs, int rowNum)
dipanggil didalam while loop dalam JdbcTemplate untuk setiap baris dalam ResultSet, kemudian setiap baris tersebut diubah 
kedalam objek yang bersangkutan.

Implementasi RowMapper bersifat stateless dan reusable. oleh karena itu, selalu bagus untuk dilakukan untuk membuat sebuah
implementasi RowMapper untuk setiap domain object yang berbeda dan menggunakannya di tempat yang berbeda beda.

####Melakukan Query Menggunakan  NamedParameterJdbcTemplate
Dibandingkan dengan menggunakan tanda "?", kita bisa menggunakan named parameter, dimulai dari nama kolom 