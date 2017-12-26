package io.committed.ketos.data.jpa;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.committed.ketos.data.jpa.repository.JpaDocumentRepository;

@Configuration
@ComponentScan(basePackageClasses = { BaleenJpaConfig.class })
@EnableJpaRepositories(basePackageClasses = { JpaDocumentRepository.class })
public class BaleenJpaConfig {


}
