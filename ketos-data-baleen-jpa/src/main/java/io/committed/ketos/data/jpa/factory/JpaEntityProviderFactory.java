package io.committed.ketos.data.jpa.factory;

import java.util.Map;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.jpa.providers.JpaEntityProvider;
import io.committed.ketos.data.jpa.repository.JpaEntityRepository;
import reactor.core.publisher.Mono;

public class JpaEntityProviderFactory
    extends AbstractJpaDataProviderFactory<EntityProvider> {

  public JpaEntityProviderFactory(final EntityManagerFactoryBuilder emfBuilder) {
    super(emfBuilder, "baleen-jpa-entities", EntityProvider.class);
  }

  @Override
  public Mono<EntityProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final JpaRepositoryFactory support = buildRepositoryFactory(settings);

    final JpaEntityRepository repository =
        support.getRepository(JpaEntityRepository.class);

    return Mono.just(new JpaEntityProvider(dataset, datasource, repository));
  }

}
