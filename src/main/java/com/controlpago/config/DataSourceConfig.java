package com.controlpago.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        try {
            // DATABASE_URL: mysql://user:pass@host:3306/db
            String jdbcUrl = convertToJdbcUrl(databaseUrl);

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error configurando DataSource", e);
        }
    }

    private String convertToJdbcUrl(String url) {
        // Elimina el prefijo "mysql://"
        url = url.replace("mysql://", "");

        // Divide en usuario/contraseña y host/db
        String[] parts = url.split("@");
        String userPass = parts[0];          // user:pass
        String hostDb = parts[1];            // host:3306/db

        String[] userPassSplit = userPass.split(":");
        String user = userPassSplit[0];
        String pass = userPassSplit[1];

        String[] hostDbSplit = hostDb.split("/", 2);
        String host = hostDbSplit[0];
        String db = hostDbSplit[1];

        // Construye la URL JDBC
        return "jdbc:mysql://" + host + "/" + db + "?user=" + user + "&password=" + pass;
    }
}
