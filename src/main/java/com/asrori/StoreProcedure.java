package com.asrori;

import com.asrori.configuration.BeanConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreProcedure {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        JdbcTemplate jdbcTemplate = applicationContext.getBean("jdbcTemplate", JdbcTemplate.class);

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate);

        simpleJdbcCall
                .withProcedureName("concat")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("param1", Types.VARCHAR),
                        new SqlParameter("param2", Types.VARCHAR)).
                returningResultSet("result", new SingleColumnRowMapper<String>(String.class));
        simpleJdbcCall.compile();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("param1", "hello ");
        paramMap.put("param2", "world!");
        Map<String,Object> resultMap = simpleJdbcCall.execute(paramMap);

        List<String> resultList = (List<String>) resultMap.get("result");
        for(String value:resultList) {
            System.out.println(value);
        }
    }

}
