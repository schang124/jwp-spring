package next.config;

import core.jdbc.ConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by schang124 on 2017/01/05.
 */
@Configuration
@ComponentScan(basePackages = {"next.dao", "next.service"})
public class AppConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(ConnectionManager.getDataSource());
    }
}
