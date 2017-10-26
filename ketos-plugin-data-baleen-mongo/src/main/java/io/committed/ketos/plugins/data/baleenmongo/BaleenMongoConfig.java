package io.committed.ketos.plugins.data.baleenmongo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenDocumentRepository;

@Configuration
@ComponentScan(basePackageClasses = { BaleenMongoConfig.class })
@EnableReactiveMongoRepositories(basePackageClasses = { BaleenDocumentRepository.class })
public class BaleenMongoConfig {


}
