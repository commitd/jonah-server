package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.CrudDocumentProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchCrudDocumentProvider;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsCrudDocumentProviderFactory
    extends AbstractElasticsearchDataProviderFactory<CrudDocumentProvider> {

  private final ObjectMapper mapper;


  public EsCrudDocumentProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-crud-documents", CrudDocumentProvider.class, BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_DOCUMENT_TYPE);
    this.mapper = mapper;
  }


  @Override
  public Mono<CrudDocumentProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);

      final String mentionType =
          (String) settings.getOrDefault("mentionType", BaleenElasticsearchConstants.DEFAULT_MENTION_TYPE);
      final String entityType =
          (String) settings.getOrDefault("entityType", BaleenElasticsearchConstants.DEFAULT_ENTITY_TYPE);
      final String relationType =
          (String) settings.getOrDefault("relationType", BaleenElasticsearchConstants.DEFAULT_RELATION_TYPE);

      final EsDocumentService documents =
          new EsDocumentService(elastic, mapper, getIndexName(settings), getTypeName(settings));
      final EsMentionService mentions =
          new EsMentionService(elastic, mapper, getIndexName(settings), mentionType);
      final EsEntityService entities =
          new EsEntityService(elastic, mapper, getIndexName(settings), entityType);
      final EsRelationService relations =
          new EsRelationService(elastic, mapper, getIndexName(settings), relationType);

      return Mono
          .just(new ElasticsearchCrudDocumentProvider(dataset, datasource, documents, mentions, entities, relations));
    } catch (final Exception e) {
      log.error("Unable to create ES Document Provider", e);
      return Mono.empty();
    }
  }

}
