package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.CrudRelationProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchCrudRelationProvider;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsCrudRelationProviderFactory
    extends AbstractElasticsearchDataProviderFactory<CrudRelationProvider> {

  private final ObjectMapper mapper;


  public EsCrudRelationProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-crud-relations", CrudRelationProvider.class, BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_RELATION_TYPE);
    this.mapper = mapper;
  }


  @Override
  public Mono<CrudRelationProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);

      final String documentType =
          (String) settings.getOrDefault("documentType", BaleenElasticsearchConstants.DEFAULT_DOCUMENT_TYPE);
      final EsRelationService relations =
          new EsRelationService(elastic, mapper, getIndexName(settings), getTypeName(settings));

      return Mono
          .just(new ElasticsearchCrudRelationProvider(dataset, datasource, documentType, relations));
    } catch (final Exception e) {
      log.error("Unable to create ES Relation Provider", e);
      return Mono.empty();
    }
  }

}
