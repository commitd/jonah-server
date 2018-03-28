package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.CrudEntityProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchCrudEntityProvider;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;

/** A factory for creating ES CrudEntityProvider objects. */
@Slf4j
@Service
public class EsCrudEntityProviderFactory
    extends AbstractElasticsearchDataProviderFactory<CrudEntityProvider> {

  private final ObjectMapper mapper;

  public EsCrudEntityProviderFactory(final ObjectMapper mapper) {
    super(
        "baleen-es-crud-entities",
        CrudEntityProvider.class,
        BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_ENTITY_TYPE);
    this.mapper = mapper;
  }

  @Override
  public Mono<CrudEntityProvider> build(
      final String dataset, final String datasource, final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);

      final String documentType =
          (String)
              settings.getOrDefault(
                  "documentType", BaleenElasticsearchConstants.DEFAULT_DOCUMENT_TYPE);

      final EsEntityService entities =
          new EsEntityService(elastic, mapper, getIndexName(settings), getTypeName(settings));
      final EsMentionService mentions =
          new EsMentionService(
              elastic,
              mapper,
              getIndexName(settings),
              BaleenElasticsearchConstants.DEFAULT_MENTION_TYPE);
      final EsRelationService relations =
          new EsRelationService(
              elastic,
              mapper,
              getIndexName(settings),
              BaleenElasticsearchConstants.DEFAULT_RELATION_TYPE);
      return Mono.just(
          new ElasticsearchCrudEntityProvider(
              dataset, datasource, documentType, mentions, entities, relations));
    } catch (final Exception e) {
      log.error("Unable to create ES Document Provider", e);
      return Mono.empty();
    }
  }
}
