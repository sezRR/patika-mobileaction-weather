package dev.sezrr.projects.patikaweatherproject.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig
{
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer()
    {
        try
        {
            return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.4"))
                    .withDatabaseName("patika_weather_project_test")
                    .withUsername("postgres")
                    .withPassword("password")
                    .withReuse(true);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to initialize PostgreSQL Test Container", e);
        }
    }
}
