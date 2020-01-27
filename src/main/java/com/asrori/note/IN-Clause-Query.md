##IN Clause Query

SQL mendukung untuk menjalankan statement query yang mengandung beberapa variabel dari nilai parameter input
sebagai contoh kita bisa menulis query seperti ini

```sql
SELECT FROM akun WHERE id IN (1,2,3,4,5)
```

Sayangnya, JDBC secara langsung tidak mendukung fitur ini, jadi kita bisa mendeklarasikan beberapa variabel. namn spring JdbcTemplate
dan NamedParameterJdbcTemplate dapat mengatasi kekurangan tersebut. kelas tersebut bisa 