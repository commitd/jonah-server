package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.plugins.data.mongo.providers.MongoMetadataProvider;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import reactor.core.publisher.Mono;

public class MongoMetadataProviderFactory
    extends AbstractMongoDataProviderFactory<MetadataProvider> {

  public MongoMetadataProviderFactory() {
    super("baleen-mongo-metadata", MetadataProvider.class);
  }


  @Override
  public Mono<MetadataProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final ReactiveMongoTemplate mongoTemplate = buildMongoTemplate(settings);
    final ReactiveRepositoryFactorySupport support = buildRepositoryFactory(mongoTemplate);

    final BaleenDocumentRepository repository =
        support.getRepository(BaleenDocumentRepository.class);

    return Mono.just(new MongoMetadataProvider(dataset, datasource, mongoTemplate, repository));
  }

}
