package io.committed.ketos.plugins.data.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import io.committed.ketos.plugins.data.mongo.factory.MongoDocumentProviderFactory;
import io.committed.ketos.plugins.data.mongo.factory.MongoEntityProviderFactory;
import io.committed.ketos.plugins.data.mongo.factory.MongoMentionProviderFactory;
import io.committed.ketos.plugins.data.mongo.factory.MongoMetadataProviderFactory;
import io.committed.ketos.plugins.data.mongo.factory.MongoRelationProviderFactory;

@Configuration
@ComponentScan(basePackageClasses = {BaleenMongoConfig.class})
public class BaleenMongoConfig {

  @Bean
  public MongoDocumentProviderFactory mongoDocumentDataProviderFactory() {
    return new MongoDocumentProviderFactory();
  }

  @Bean
  public MongoEntityProviderFactory mongoEntityProviderFactory() {
    return new MongoEntityProviderFactory();
  }

  @Bean
  public MongoRelationProviderFactory mongoRelationDataProviderFactory() {
    return new MongoRelationProviderFactory();
  }

  @Bean
  public MongoMentionProviderFactory mongoMentionDataProviderFactory() {
    return new MongoMentionProviderFactory();
  }

  @Bean
  public MongoMetadataProviderFactory mongoMetadataDataProviderFactory() {
    return new MongoMetadataProviderFactory();
  }
}
