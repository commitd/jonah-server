package io.committed.vessel.plugin.data.jpa.factory;

import java.util.Map;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.vessel.plugin.data.jpa.providers.JpaDocumentProvider;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentMetadataRepository;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentRepository;
import reactor.core.publisher.Mono;

public class JpaDocumentProviderFactory
    extends AbstractJpaDataProviderFactory<DocumentProvider> {

  public JpaDocumentProviderFactory(final EntityManagerFactoryBuilder emfBuilder) {
    super(emfBuilder, "baleen-jpa-documents", DocumentProvider.class);
  }


  @Override
  public Mono<DocumentProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final JpaRepositoryFactory support = buildRepositoryFactory(settings);

    final JpaDocumentRepository documentRepository =
        support.getRepository(JpaDocumentRepository.class);
    final JpaDocumentMetadataRepository metadataRepository =
        support.getRepository(JpaDocumentMetadataRepository.class);

    return Mono
        .just(new JpaDocumentProvider(dataset, datasource, documentRepository, metadataRepository));
  }

}
