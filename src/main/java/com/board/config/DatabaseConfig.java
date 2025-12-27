package com.board.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            // Convert Render's postgres:// URL to jdbc:postgresql:// format
            databaseUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");

            // Parse the URL to extract username, password, host, port, and database
            // Format: jdbc:postgresql://username:password@host:port/database
            String[] parts = databaseUrl.substring("jdbc:postgresql://".length()).split("@");
            String[] credentials = parts[0].split(":");
            String username = credentials[0];
            String password = credentials[1];

            String[] hostAndDb = parts[1].split("/");
            String hostAndPort = hostAndDb[0];
            String database = hostAndDb[1];

            String jdbcUrl = "jdbc:postgresql://" + hostAndPort + "/" + database;

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }

        // Fallback to default Spring Boot configuration
        return DataSourceBuilder.create().build();
    }
}
