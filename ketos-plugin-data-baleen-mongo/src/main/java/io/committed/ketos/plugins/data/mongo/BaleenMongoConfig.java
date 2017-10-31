package io.committed.ketos.plugins.data.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import io.committed.ketos.plugins.data.configurer.mongo.MongoDocumentProviderFactory;
import io.committed.ketos.plugins.data.configurer.mongo.MongoEntityProviderFactory;
import io.committed.ketos.plugins.data.configurer.mongo.MongoMentionProviderFactory;
import io.committed.ketos.plugins.data.configurer.mongo.MongoRelationProviderFactory;

@Configuration
@ComponentScan(basePackageClasses = { BaleenMongoConfig.class })
@EnableReactiveMongoRepositories
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

  // TODO: Shouldn't have these... they should be from config

  // @Bean
  // public DocumentProvider mongoDocumentProvider(
  // final MongoDocumentProviderFactory factory) {
  // return factory.build("baleen", Maps.newHashMap()).block();
  // }
  //
  // @Bean
  // public EntityProvider mongoEntityProvider(
  // final MongoEntityProviderFactory factory) {
  // return factory.build("baleen", Maps.newHashMap()).block();
  // }
  //
  // @Bean
  // public RelationProvider mongoRelationProvider(
  // final MongoRelationProviderFactory factory) {
  // return factory.build("baleen", Maps.newHashMap()).block();
  // }
  //
  // @Bean
  // public MentionProvider mongoMentionProvider(
  // final MongoMentionProviderFactory factory) {
  // return factory.build("baleen", Maps.newHashMap()).block();
  // }

}
