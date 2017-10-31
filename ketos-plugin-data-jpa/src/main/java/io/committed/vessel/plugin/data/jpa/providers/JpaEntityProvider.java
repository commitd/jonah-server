package io.committed.vessel.plugin.data.jpa.providers;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenEntity;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DatasourceConstants;
import io.committed.ketos.plugins.graphql.baleenservices.providers.EntityProvider;
import io.committed.vessel.plugin.data.jpa.dao.JpaEntity;
import io.committed.vessel.plugin.data.jpa.repository.JpaEntityRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JpaEntityProvider implements EntityProvider {

  private final JpaEntityRepository entities;
  private final String corpus;

  @Autowired
  public JpaEntityProvider(final String corpus, final JpaEntityRepository entities) {
    this.corpus = corpus;
    this.entities = entities;
  }

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return Mono.justOrEmpty(entities.findByExternalid(id)).map(JpaEntity::toBaleenEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return Flux.fromStream(entities.findByDocId(document.getId())).map(JpaEntity::toBaleenEntity);
  }

  @Override
  public String getDatasource() {
    return DatasourceConstants.SQL;
  }

  @Override
  public String getCorpus() {
    return corpus;
  }
}
