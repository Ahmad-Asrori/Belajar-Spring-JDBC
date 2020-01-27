### Mendapatkan Koneksi Dari Database Menggunakan Spring JDBC
Kita mempunyai 2 cara untuk mendapatkan koneksi dari database dengan **JDBC API.**. Cara yang pertama adalah dengan menggunakan ***DriverManager***. Cara yang kedua adalah dengan menggunakan ***DataSource.*** Lebih disarankan untuk menggunakan DataSource karena dia menyediakan fungsi fungsi koneksi yang dapat membuat kita menyembunyikan parameter koneksi dari database, menerapkan connection pooling dan memberikan transaction management ke aplikasi. Spring sendiri menggunakan DataSource untuk mendapatkan koneksi dari database. Spring memiliki beberapa implementasi dari interface DataSource.

Setelah membuat maven project, kita tambahkan 3 dependency berikut : 
[Spring-Context](https://mvnrepository.com/artifact/org.springframework/spring-context)
[Spring-JDBC](https://mvnrepository.com/artifact/org.springframework/spring-jdbc)
[PostgreSQL JDBC Driver](https://mvnrepository.com/artifact/org.postgresql/postgresql)

Setelah itu, kita membuat database di PostgreSQL. Kita beri nama database kita dengan nama _bank_. didalam database tersebut kita buat sebuah tabel dengan nama tabelnya adalah _akun_. lengkapnya seperti ini.
```sql  
CREATE TABLE akun (
    id SERIAL,
    nama VARCHAR(255),
    saldo DOUBLE,
    waktuakses TIMESTAMP,
    terkunci BOOLEAN,
    CONSTRAINT akun_PK PRIMARY KEY (id)
)
```
Kemudian, untuk mendapatkan koneksi ke database bank kita perlu mengkonfigurasi DataSource. Ingat, spring memiliki beberapa implemetasi dari interface DataSource. Disini kita menggunakan salah satu implementasi dari interface DataSource yaitu _**DriverManagerDataSource**_. Untuk konfigurasinya seperti yang tertulis di file BeanConfiguration.java

```java
@Configuration
public class BeanConfiguration {
	    
    @Bean
    public DataSource dataSource() {
	    DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName("org.postgresql.Driver");
	    dataSource.setUrl("jdbc:postgresql://l27.0.0.1:5432/bank");
	    dataSource.setUsername("YOUR_USERNAME");
	    dataSource.setPassword("YOUR_PASSWORD");
	    return dataSource;
	}
		
}
```
Untuk mengetes koneksi, kita buat kelas TesKoneksi yang berisi kode sebagai berikut : 
```java
public class TesKoneksi {
    public static void main( String[] args ) throws SQLException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
        Connection connection = dataSource.getConnection();
        System.out.println("koneksi ditutup : " + connection.isClosed());
        connection.close();
        System.out.println("koneksi ditutup : " + connection.isClosed());

        applicationContext.close();
    }
}
```
##### Penjelasan
Kita mendefinisikan sebuah bean DataSource menggunakan kelas DriverManagerDataSource yang dimana kelas ini adalah kelas yang sudah disediakan oleh Spring Framework itu sendiri. kelas DriverManagerDataSource merupakan implementasi sederhana dari interface DataSource (javax.sql.DataSource). Dia mengembalikan sebuah koneksi baru setiap waktu ketika kita memanggil method getConnection(). Penggunaan kelas DriverManagerDataSource ditujukan **untuk tes dan standalone environment**. kita perlu memberikan kelas DriverManagerDataSource beberapa informasi mengenai database sebagai parameter, beberapa diantaranya adalah _driverClassName_, _url_, _username_ dan _password_.

Untuk mengetes konfigurasi dari bean DataSource agar kita dapat mendapatkan koneksi dari database, kita membuat kelas TesKoneksi yang pada dasarnya didalam kelas tersebut kita memuat konfigurasi yang terdapat pada kelas BeanConfiguration dan mendapatkan bean DataSource dari ApplicationContext. Kemudian kita memanggil method getConnection() untuk mendapatkan sebuah Connection dan mengecek apakah koneksi terbuka atau tidak. 

implementasi yang lain dari interface DataSource adalah _**SingleConnectionDataSource**_. Kelas SingleConnectionDataSource juga digunakan untuk keperluan tes dan standalone environment. Kelas SingleConnectionDataSource menggunakan kembali koneksi yang ada secara terus menerus. Kita dapat mendefinisikan bean DataSource menggunakan kelas SingleConnectionDataSource dengan cara sebagai berikut : 
```java
public class BeanConfiguration {
        
    @Bean
    public DataSource dataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setSuppressClose(true);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1/bank");
        dataSource.setUsername("YOUR_USERNAME");
        dataSource.setPassword("YOUR_PASSWORD");
        return dataSource;
    }
    
}
```
Mengatur property _SuppressClose_ menjadi _true_ membuat proxy dari objek Connection dikembalikan. yang mana tujuannya adalah untuk memotong pemanggilan method _close()_. hal ini sangat penting jika data access technology atau framework tiba tiba memenaggil method _close()_. 

##### Jangan menggunakan DriverManagerDataSource di Production Environemnt

> DriverManagerDataSource tidak mempunyai kemampuan Connection Pooling. DriverManagerDataSource mencoba membuka koneksi JDBC baru ketika kita memintanya. Membuka koneksi JDBC itu adalah proses yang membutuhkan banyak cost. Jadi, lebih baik menggunakan implementasi DataSource yang lain yang menyediakan kemampuan Connection Pooling. Library [C3P0](https://mvnrepository.com/artifact/com.mchange/c3p0) atau [Apache Commons DBCP](https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2) adalah pilihan yang bagus. 

##### Menggunakan Connection Pooling
Untuk production environment. lebih cocok untuk menggunakan objek DataSource yang mempunyai kemampuan connection pooling. objek DataSOurce yang dikelola oleh application server biasanya mempunyai fitur connection pooling. kita dapat dengan mudah mendefinisikan bean DataSource connection pooling dari third party library seperti C3P0 atau Apache COmmons DBCP : 

```java
// jika menggunakan library Apache Commons DBCP
@Configuration
public class BeanConfiguration {
    @Bean(destroyMethod="close")
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql:tcp://127.0.0.1:5432/bank");
        dataSource.setUsername("YOUR_USERNAME");
        dataSource.setPassword("YOUR_PASSWORD");
        return dataSource;
    }
}

// jika menggunakan library C3P0
@Configuration
public class BeanConfiguration {
    @Bean(destroyMethod="close")
    public DataSource dataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql:tcp://127.0.0.1:5432/bank");
        dataSource.setUsername("YOUR_USERNAME");
        dataSource.setPassword("YOUR_PASSWORD");
        return dataSource;
    }
}
```
_@Bean(destroyMethod="close")_ berarti saat application context ditutup, maka ia akan memanggil method close pada objek BasicDataSource atau ComboPooledDataSource tergantung dari library mana yang digunakan. Untuk mematikan destroyMethod pada suatu bean kita cukup mengosongkan nilai string seperti contohnya : @Bean(destroyMethod="")

