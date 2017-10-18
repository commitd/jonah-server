package io.committed.ketos.plugins.data.baleenmongo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenDocumentRepository;

@Configuration
@ComponentScan(basePackageClasses = { BaleenMongoConfig.class })
@EnableMongoRepositories(basePackageClasses = { BaleenDocumentRepository.class })
public class BaleenMongoConfig {

}
