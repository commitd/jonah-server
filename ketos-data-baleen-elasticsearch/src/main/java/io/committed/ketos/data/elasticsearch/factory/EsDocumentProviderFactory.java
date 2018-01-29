package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchDocumentProvider;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsDocumentProviderFactory
    extends AbstractElasticsearchDataProviderFactory<DocumentProvider> {

  private final ObjectMapper mapper;


  public EsDocumentProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-documents", DocumentProvider.class);
    this.mapper = mapper;
  }


  @Override
  public Mono<DocumentProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsDocumentService service = new EsDocumentService(mapper, elastic);

      return Mono.just(new ElasticsearchDocumentProvider(dataset, datasource, service));
    } catch (final Exception e) {
      log.error("Unable to create ES Document Provider", e);
      return Mono.empty();
    }
  }

}
