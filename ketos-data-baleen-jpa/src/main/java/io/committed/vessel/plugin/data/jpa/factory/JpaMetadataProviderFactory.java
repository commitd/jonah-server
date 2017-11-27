package io.committed.vessel.plugin.data.jpa.factory;

import java.util.Map;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.vessel.plugin.data.jpa.providers.JpaMetadataProvider;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentMetadataRepository;
import reactor.core.publisher.Mono;

public class JpaMetadataProviderFactory
    extends AbstractJpaDataProviderFactory<MetadataProvider> {

  public JpaMetadataProviderFactory(final EntityManagerFactoryBuilder emfBuilder) {
    super(emfBuilder, "baleen-jpa-metadata", MetadataProvider.class);
  }


  @Override
  public Mono<MetadataProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final JpaRepositoryFactory support = buildRepositoryFactory(settings);

    final JpaDocumentMetadataRepository metadataRepository =
        support.getRepository(JpaDocumentMetadataRepository.class);

    return Mono
        .just(new JpaMetadataProvider(dataset, datasource, metadataRepository));
  }

}
