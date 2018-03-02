package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.CrudMentionProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchCrudMentionProvider;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsCrudMentionProviderFactory
    extends AbstractElasticsearchDataProviderFactory<CrudMentionProvider> {

  private final ObjectMapper mapper;

  public EsCrudMentionProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-crud-mentions", CrudMentionProvider.class, BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_MENTION_TYPE);
    this.mapper = mapper;
  }


  @Override
  public Mono<CrudMentionProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);


      final String entityType =
          (String) settings.getOrDefault("entityType", BaleenElasticsearchConstants.DEFAULT_ENTITY_TYPE);
      final String relationType =
          (String) settings.getOrDefault("relationType", BaleenElasticsearchConstants.DEFAULT_RELATION_TYPE);

      final EsMentionService mentions =
          new EsMentionService(elastic, mapper, getIndexName(settings), getTypeName(settings));
      final EsEntityService entities =
          new EsEntityService(elastic, mapper, getIndexName(settings), entityType);
      final EsRelationService relations =
          new EsRelationService(elastic, mapper, getIndexName(settings), relationType);

      return Mono
          .just(new ElasticsearchCrudMentionProvider(dataset, datasource, mentions, entities, relations));
    } catch (final Exception e) {
      log.error("Unable to create ES Mention Provider", e);
      return Mono.empty();
    }
  }

}
