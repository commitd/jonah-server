package io.committed.ketos.plugins.data.configurer.mongo;

import java.util.Map;

import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;

import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.providers.MongoDocumentProvider;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import reactor.core.publisher.Mono;

public class MongoDocumentProviderFactory
    extends AbstractMongoDataProviderFactory<DocumentProvider> {

  public MongoDocumentProviderFactory() {
    super("baleen-mongo-documents", DocumentProvider.class);
  }


  @Override
  public Mono<DocumentProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final ReactiveRepositoryFactorySupport support = buildRepositoryFactory(settings);

    final BaleenDocumentRepository repository =
        support.getRepository(BaleenDocumentRepository.class);

    return Mono.just(new MongoDocumentProvider(dataset, datasource, repository));
  }

}
