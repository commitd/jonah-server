package io.committed.vessel.plugin.data.jdbc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.committed.vessel.plugin.data.jdbc.repository.SqlDocumentRepository;

@Configuration
@ComponentScan(basePackageClasses = { BaleenJdbcConfig.class })
@EnableJpaRepositories(basePackageClasses = { SqlDocumentRepository.class })
public class BaleenJdbcConfig {


}
